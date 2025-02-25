package com.example.usagetracker.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.usagetracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AppLaunchAccessibilityService : AccessibilityService() {
    private lateinit var database: AppDatabase
    private var lastCheckedPackage = ""
    private var lastCheckTime = 0L
    private var currentDialog: AlertDialog? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // Track apps that user has already responded to
    private val respondedApps = mutableMapOf<String, Long>()
    
    // Cooldown period in milliseconds (5 seconds)
    private val COOLDOWN_PERIOD = 5000L
    
    private val TAG = "AppLaunchAccessibility"

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        Log.d(TAG, "Service created")
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 0L // Reduced to 0 for immediate response
        }
        serviceInfo = info
        Toast.makeText(this, "App Launch Control Service Started", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        
        val packageName = event.packageName?.toString() ?: return
        val currentTime = System.currentTimeMillis()
        
        Log.d(TAG, "Accessibility event: $packageName")
        
        // Prevent multiple checks for the same package in a short time
        if (packageName == lastCheckedPackage && currentTime - lastCheckTime < 500) {
            Log.d(TAG, "Skipping duplicate event for $packageName")
            return
        }
        
        lastCheckedPackage = packageName
        lastCheckTime = currentTime

        // Only filter out obvious system packages
        if (packageName == applicationContext.packageName) {
            Log.d(TAG, "Skipping our own package: $packageName")
            return
        }
        
        if (packageName == "com.android.systemui") {
            Log.d(TAG, "Skipping system UI: $packageName")
            return
        }
        
        // Don't filter out launcher packages too aggressively
        // Some apps might have "launcher" in their name but not be actual launchers
        if (packageName == "com.android.launcher3" || 
            packageName == "com.google.android.launcher") {
            Log.d(TAG, "Skipping launcher: $packageName")
            return
        }

        // Check if this is a controlled app and show dialog
        checkAppAndShowDialog(packageName)
    }

    private fun checkAppAndShowDialog(packageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if this app is in the controlled list
                val controlledApps = database.appUsageDao().getControlledPackageNames()
                val isControlled = packageName in controlledApps
                
                Log.d(TAG, "Checking app: $packageName, controlled: $isControlled, controlled apps: $controlledApps")

                if (!isControlled) {
                    Log.d(TAG, "App not controlled: $packageName")
                    return@launch
                }
                
                Log.d(TAG, "App is controlled: $packageName")
                
                // Check if we've already shown a dialog for this app recently
                val currentTime = System.currentTimeMillis()
                val lastResponseTime = respondedApps[packageName] ?: 0L
                if (currentTime - lastResponseTime < COOLDOWN_PERIOD) {
                    // User has responded to this app recently, skip showing dialog
                    Log.d(TAG, "Skipping recently responded app: $packageName")
                    return@launch
                }

                // Get app name
                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    packageName
                }

                // Get last 24 hours usage
                val last24HoursStart = currentTime - TimeUnit.HOURS.toMillis(24)
                val last24HoursUsage = database.appUsageDao().getTotalUsageTime(
                    packageName,
                    last24HoursStart,
                    currentTime
                ) ?: 0L

                // Format usage time
                val hours = TimeUnit.MILLISECONDS.toHours(last24HoursUsage)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(last24HoursUsage) % 60
                val usageTimeFormatted = "${hours}h ${minutes}m"
                
                Log.d(TAG, "Showing dialog for: $packageName ($appName) with usage: $usageTimeFormatted")

                // Try using the PopupService instead of showing a dialog directly
                val intent = Intent(applicationContext, com.example.usagetracker.service.PopupService::class.java).apply {
                    putExtra("package_name", packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startService(intent)
                Log.d(TAG, "Started PopupService for: $packageName")
                
                // Also try showing our own dialog as a fallback
                withContext(Dispatchers.Main) {
                    // Dismiss any existing dialog
                    currentDialog?.dismiss()
                    showConfirmationDialog(appName, packageName, usageTimeFormatted)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking app: $packageName", e)
                e.printStackTrace()
            }
        }
    }

    private fun showConfirmationDialog(appName: String, packageName: String, usageTime: String) {
        mainHandler.post {
            try {
                Log.d(TAG, "Creating dialog for: $packageName")
                currentDialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setTitle("App Launch Confirmation")
                    .setMessage("You spent $usageTime on $appName last 24 hours, are you sure you want to open it!")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        // Record that user has responded to this app
                        respondedApps[packageName] = System.currentTimeMillis()
                        Log.d(TAG, "User clicked Yes for: $packageName")
                        
                        // Allow the app to launch
                        performGlobalAction(GLOBAL_ACTION_HOME)
                        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                        if (launchIntent != null) {
                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(launchIntent)
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                        // Record that user has responded to this app
                        respondedApps[packageName] = System.currentTimeMillis()
                        Log.d(TAG, "User clicked No for: $packageName")
                        
                        // Go back to home screen
                        performGlobalAction(GLOBAL_ACTION_HOME)
                    }
                    .setCancelable(false)
                    .create()
                    .apply {
                        window?.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY)
                        show()
                        Log.d(TAG, "Dialog shown for: $packageName")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error showing dialog for: $packageName", e)
                e.printStackTrace()
                
                // Try using the PopupService as a fallback
                val intent = Intent(applicationContext, com.example.usagetracker.service.PopupService::class.java).apply {
                    putExtra("package_name", packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startService(intent)
                Log.d(TAG, "Started PopupService as fallback for: $packageName")
            }
        }
    }

    override fun onInterrupt() {
        // Clean up any existing dialogs
        currentDialog?.dismiss()
        currentDialog = null
        Log.d(TAG, "Service interrupted")
    }
} 