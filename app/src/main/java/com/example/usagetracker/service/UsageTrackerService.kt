package com.example.usagetracker.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.usagetracker.R
import com.example.usagetracker.data.AppDatabase
import com.example.usagetracker.data.AppUsageEntity
import com.example.usagetracker.ui.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit

class UsageTrackerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var database: AppDatabase
    private lateinit var wakeLock: PowerManager.WakeLock
    private var lastCheckedTime = System.currentTimeMillis()
    private var isServiceRunning = false
    private val TAG = "UsageTrackerService"

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "usage_tracker_channel"
        
        // Define constants to replace deprecated ones
        private const val MOVE_TO_FOREGROUND = 1
        private const val MOVE_TO_BACKGROUND = 2
    }

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        database = AppDatabase.getDatabase(this)
        setupWakeLock()
        startForeground()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceRunning) {
            isServiceRunning = true
            startTracking()
            Log.d(TAG, "Service started tracking")
            Toast.makeText(this, "Usage Tracker Service Started", Toast.LENGTH_SHORT).show()
        }
        return START_STICKY
    }

    private fun setupWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "UsageTracker:WakeLock"
        )
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
        Log.d(TAG, "Wake lock acquired")
    }

    private fun startForeground() {
        val channelId = CHANNEL_ID
        val channelName = "Usage Tracker Service"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Usage Tracker")
            .setContentText("Monitoring app usage")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        Log.d(TAG, "Started in foreground")
    }

    private fun startTracking() {
        serviceScope.launch {
            while (isServiceRunning) {
                try {
                    trackUsageStats()
                } catch (e: Exception) {
                    Log.e(TAG, "Error tracking usage stats", e)
                    e.printStackTrace()
                }
                delay(TimeUnit.MINUTES.toMillis(1)) // Check every minute
            }
        }
    }

    private suspend fun trackUsageStats() {
        val currentTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(lastCheckedTime, currentTime)
        val usageEvent = UsageEvents.Event()
        val usageMap = mutableMapOf<String, Long>()
        var lastEventTime = lastCheckedTime
        var appStartTime = mutableMapOf<String, Long>()

        // Get the list of controlled apps
        val controlledPackages = database.appUsageDao().getControlledPackageNames()
        Log.d(TAG, "Controlled packages: $controlledPackages")
        
        // If no controlled apps, just update the last checked time and return
        if (controlledPackages.isEmpty()) {
            lastCheckedTime = currentTime
            Log.d(TAG, "No controlled apps found")
            return
        }

        while (events.hasNextEvent()) {
            events.getNextEvent(usageEvent)
            if (usageEvent.eventType == MOVE_TO_FOREGROUND ||
                usageEvent.eventType == MOVE_TO_BACKGROUND
            ) {
                val packageName = usageEvent.packageName
                val timeStamp = usageEvent.timeStamp
                lastEventTime = timeStamp
                
                // Only process controlled apps
                if (packageName in controlledPackages) {
                    Log.d(TAG, "Processing controlled app event: $packageName, event: ${if (usageEvent.eventType == MOVE_TO_FOREGROUND) "FOREGROUND" else "BACKGROUND"}")
                    
                    if (usageEvent.eventType == MOVE_TO_FOREGROUND) {
                        appStartTime[packageName] = timeStamp
                        if (packageName != applicationContext.packageName) {
                            Log.d(TAG, "Showing usage warning for: $packageName")
                            showUsageWarning(packageName)
                        }
                    } else if (usageEvent.eventType == MOVE_TO_BACKGROUND) {
                        val startTime = appStartTime[packageName] ?: lastCheckedTime
                        val usageTime = timeStamp - startTime
                        if (usageTime > 0) {
                            usageMap[packageName] = (usageMap[packageName] ?: 0) + usageTime
                            Log.d(TAG, "Recorded usage time for $packageName: $usageTime ms")
                        }
                        appStartTime.remove(packageName)
                    }
                }
            }
        }

        // Add usage time for apps that are still in foreground
        appStartTime.forEach { (packageName, startTime) ->
            // Only process controlled apps
            if (packageName in controlledPackages) {
                val usageTime = currentTime - startTime
                if (usageTime > 0) {
                    usageMap[packageName] = (usageMap[packageName] ?: 0) + usageTime
                    Log.d(TAG, "Recorded ongoing usage time for $packageName: $usageTime ms")
                }
            }
        }

        // Store usage data only for controlled apps
        usageMap.forEach { (packageName, timeInForeground) ->
            if (timeInForeground > 0 && packageName in controlledPackages) {
                val packageManager = applicationContext.packageManager
                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    packageName
                }

                // Store with current timestamp to allow for 24-hour queries
                database.appUsageDao().insertUsage(
                    AppUsageEntity(
                        packageName = packageName,
                        appName = appName,
                        usageTimeInMillis = timeInForeground,
                        date = currentTime,
                        isControlled = true
                    )
                )
                Log.d(TAG, "Stored usage data for $packageName ($appName): $timeInForeground ms")
            }
        }

        lastCheckedTime = lastEventTime
    }

    private fun showUsageWarning(packageName: String) {
        // Try both PopupService and direct intent to ensure at least one works
        try {
            val intent = Intent(applicationContext, PopupService::class.java).apply {
                putExtra("package_name", packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startService(intent)
            Log.d(TAG, "Started PopupService for: $packageName")
            Toast.makeText(this, "Showing warning for: $packageName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting PopupService", e)
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        serviceScope.cancel()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        
        // Restart the service if it was killed
        val restartIntent = Intent(applicationContext, UsageTrackerService::class.java)
        startService(restartIntent)
        Log.d(TAG, "Service destroyed and restarted")
    }
} 