package com.example.usagetracker.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.usagetracker.data.AppDatabase
import com.example.usagetracker.data.AppUsageEntity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit

class UsageTrackerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var database: AppDatabase
    private lateinit var wakeLock: PowerManager.WakeLock
    private var lastCheckedTime = System.currentTimeMillis()

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        database = AppDatabase.getDatabase(this)
        setupWakeLock()
        startForeground()
        startTracking()
    }

    private fun setupWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "UsageTracker:WakeLock"
        )
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
    }

    private fun startForeground() {
        val channelId = "usage_tracker_channel"
        val channel = NotificationChannel(
            channelId,
            "Usage Tracker Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Usage Tracker")
            .setContentText("Monitoring app usage")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .build()

        startForeground(1, notification)
    }

    private fun startTracking() {
        serviceScope.launch {
            while (true) {
                trackUsageStats()
                delay(TimeUnit.MINUTES.toMillis(5)) // Check every 5 minutes
            }
        }
    }

    private suspend fun trackUsageStats() {
        val currentTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(lastCheckedTime, currentTime)
        val usageEvent = UsageEvents.Event()
        val usageMap = mutableMapOf<String, Long>()

        while (events.hasNextEvent()) {
            events.getNextEvent(usageEvent)
            if (usageEvent.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                usageEvent.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND
            ) {
                val packageName = usageEvent.packageName
                val timeStamp = usageEvent.timeStamp
                
                usageMap[packageName] = (usageMap[packageName] ?: 0) +
                    (if (usageEvent.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND)
                        timeStamp - (lastCheckedTime.coerceAtLeast(usageEvent.timeStamp))
                    else 0)
            }
        }

        // Store usage data
        usageMap.forEach { (packageName, timeInForeground) ->
            if (timeInForeground > 0) {
                val packageManager = applicationContext.packageManager
                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    packageName
                }

                database.appUsageDao().insertUsage(
                    AppUsageEntity(
                        packageName = packageName,
                        appName = appName,
                        usageTimeInMillis = timeInForeground,
                        date = Calendar.getInstance().timeInMillis
                    )
                )
            }
        }

        lastCheckedTime = currentTime
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
} 