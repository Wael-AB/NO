package com.example.usagetracker.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.usagetracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AppLaunchInterceptorActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var preferences: SharedPreferences
    
    private val TAG = "AppLaunchInterceptor"
    private val PREFS_NAME = "AppLaunchPrefs"
    private val RESPONSE_TIME_PREFIX = "response_time_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = AppDatabase.getDatabase(this)
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        Log.d(TAG, "Activity created")

        val launchIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(Intent.EXTRA_INTENT)
        }
        
        if (launchIntent == null) {
            Log.d(TAG, "No launch intent found, finishing activity")
            finish()
            return
        }

        val packageName = launchIntent.component?.packageName
        if (packageName == null) {
            Log.d(TAG, "No package name found, finishing activity")
            finish()
            return
        }
        
        Log.d(TAG, "Processing package: $packageName")
        
        // We'll let the interceptor show regardless of shared preferences
        // This ensures at least one popup will show
        checkAppAndShowDialog(packageName, launchIntent)
    }

    private fun checkAppAndShowDialog(packageName: String, launchIntent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if this app is in the controlled list
                val controlledApps = database.appUsageDao().getControlledPackageNames()
                val isControlled = packageName in controlledApps
                
                Log.d(TAG, "Checking app: $packageName, controlled: $isControlled, controlled apps: $controlledApps")

                if (!isControlled) {
                    // If app is not controlled, launch it directly
                    Log.d(TAG, "App not controlled, launching directly: $packageName")
                    withContext(Dispatchers.Main) {
                        startActivity(launchIntent)
                        finish()
                    }
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
                val currentTime = System.currentTimeMillis()
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

                withContext(Dispatchers.Main) {
                    showConfirmationDialog(appName, launchIntent, usageTimeFormatted, packageName)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking app: $packageName", e)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // In case of error, just launch the app
                    startActivity(launchIntent)
                    finish()
                }
            }
        }
    }

    private fun showConfirmationDialog(appName: String, launchIntent: Intent, usageTime: String, packageName: String) {
        try {
            Log.d(TAG, "Creating dialog for: $packageName")
            AlertDialog.Builder(this)
                .setTitle("App Launch Confirmation")
                .setMessage("You spent $usageTime on $appName last 24 hours, are you sure you want to open it!")
                .setPositiveButton("Yes") { _, _ ->
                    // Record response time
                    preferences.edit().putLong(RESPONSE_TIME_PREFIX + packageName, System.currentTimeMillis()).apply()
                    Log.d(TAG, "User clicked Yes for: $packageName")
                    
                    startActivity(launchIntent)
                    finish()
                }
                .setNegativeButton("No") { _, _ ->
                    // Record response time
                    preferences.edit().putLong(RESPONSE_TIME_PREFIX + packageName, System.currentTimeMillis()).apply()
                    Log.d(TAG, "User clicked No for: $packageName")
                    
                    finish()
                }
                .setCancelable(false)
                .show()
            Log.d(TAG, "Dialog shown for: $packageName")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing dialog for: $packageName", e)
            e.printStackTrace()
            // In case of error, just launch the app
            startActivity(launchIntent)
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed")
    }
} 