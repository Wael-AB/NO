package com.example.usagetracker.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.usagetracker.R
import com.example.usagetracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.Calendar

class PopupService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var popupView: View
    private lateinit var database: AppDatabase
    private lateinit var preferences: SharedPreferences
    
    private val TAG = "PopupService"
    private val PREFS_NAME = "AppLaunchPrefs"
    private val RESPONSE_TIME_PREFIX = "response_time_"

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        database = AppDatabase.getDatabase(this)
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        Log.d(TAG, "Service created")
        
        // Show a toast to confirm the service is running
        Toast.makeText(this, "Popup Service Started", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val packageName = intent?.getStringExtra("package_name")
        
        if (packageName == null) {
            Log.e(TAG, "No package name provided, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }
        
        Log.d(TAG, "Service started for package: $packageName")
        Toast.makeText(this, "Checking app: $packageName", Toast.LENGTH_SHORT).show()
        
        // We'll let the popup show regardless of shared preferences
        // This ensures at least one popup will show
        showPopup(packageName)
        return START_NOT_STICKY
    }

    private fun showPopup(packageName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Check if this app is in the controlled list
                val controlledApps = database.appUsageDao().getControlledPackageNames()
                val isControlled = packageName in controlledApps
                
                Log.d(TAG, "Checking app: $packageName, controlled: $isControlled, controlled apps: $controlledApps")

                if (!isControlled) {
                    Log.d(TAG, "App not controlled: $packageName")
                    Toast.makeText(applicationContext, "App not controlled: $packageName", Toast.LENGTH_SHORT).show()
                    stopSelf()
                    return@launch
                }
                
                Log.d(TAG, "App is controlled: $packageName")
                Toast.makeText(applicationContext, "App is controlled: $packageName", Toast.LENGTH_SHORT).show()
                
                val currentTime = System.currentTimeMillis()
                
                // Get last 24 hours usage
                val last24HoursStart = currentTime - TimeUnit.HOURS.toMillis(24)
                val last24HoursUsage = database.appUsageDao().getTotalUsageTime(
                    packageName,
                    last24HoursStart,
                    currentTime
                ) ?: 0L

                // Get app name
                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    packageName
                }

                // Format usage time
                val hours = TimeUnit.MILLISECONDS.toHours(last24HoursUsage)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(last24HoursUsage) % 60
                val usageTimeFormatted = "${hours}h ${minutes}m"
                
                Log.d(TAG, "Showing popup for: $packageName ($appName) with usage: $usageTimeFormatted")
                Toast.makeText(applicationContext, "Preparing popup for: $appName", Toast.LENGTH_SHORT).show()

                createAndShowPopupWindow(appName, usageTimeFormatted, packageName)
            } catch (e: Exception) {
                Log.e(TAG, "Error showing popup for: $packageName", e)
                e.printStackTrace()
                Toast.makeText(applicationContext, "Error showing popup: ${e.message}", Toast.LENGTH_LONG).show()
                stopSelf()
            }
        }
    }

    private fun createAndShowPopupWindow(appName: String, usageTime: String, packageName: String) {
        try {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            popupView = inflater.inflate(R.layout.popup_layout, null)

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT
            )
            params.gravity = Gravity.CENTER

            // Set the app name in the header
            popupView.findViewById<TextView>(R.id.headerText).text = "App Launch Confirmation"

            // Set the message with usage time
            popupView.findViewById<TextView>(R.id.usageText).text = 
                "You spent $usageTime on $appName last 24 hours, are you sure you want to open it!"

            // Set up buttons
            val okButton = popupView.findViewById<Button>(R.id.okButton)
            okButton.text = "Yes"
            okButton.setOnClickListener {
                try {
                    windowManager.removeView(popupView)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing view", e)
                }
                
                // Record response time
                preferences.edit().putLong(RESPONSE_TIME_PREFIX + packageName, System.currentTimeMillis()).apply()
                Log.d(TAG, "User clicked Yes for: $packageName")
                Toast.makeText(applicationContext, "Launching: $appName", Toast.LENGTH_SHORT).show()
                
                // Launch the app
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(launchIntent)
                }
                
                stopSelf()
            }

            // Add a No button
            val noButton = popupView.findViewById<Button>(R.id.noButton)
            noButton.text = "No"
            noButton.setOnClickListener {
                // Record response time
                preferences.edit().putLong(RESPONSE_TIME_PREFIX + packageName, System.currentTimeMillis()).apply()
                Log.d(TAG, "User clicked No for: $packageName")
                Toast.makeText(applicationContext, "Cancelled: $appName", Toast.LENGTH_SHORT).show()
                
                try {
                    windowManager.removeView(popupView)
                } catch (e: Exception) {
                    Log.e(TAG, "Error removing view", e)
                }
                stopSelf()
            }

            windowManager.addView(popupView, params)
            Log.d(TAG, "Popup window added for: $packageName")
            Toast.makeText(applicationContext, "Popup shown for: $appName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error creating popup window", e)
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error creating popup: ${e.message}", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::popupView.isInitialized && popupView.isAttachedToWindow) {
            try {
                windowManager.removeView(popupView)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing view on destroy", e)
            }
        }
        Log.d(TAG, "Service destroyed")
    }
} 