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
import kotlinx.coroutines.flow.first
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
                try {
                    trackUsageStats()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(TimeUnit.MINUTES.toMillis(1)) // Check every minute instead of 5
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

        val controlledApps = database.appUsageDao().getControlledApps().first()
        val controlledPackages = controlledApps.map { it.packageName }.toSet()

        while (events.hasNextEvent()) {
            events.getNextEvent(usageEvent)
            if (usageEvent.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                usageEvent.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND
            ) {
                val packageName = usageEvent.packageName
                val timeStamp = usageEvent.timeStamp
                lastEventTime = timeStamp
                
                if (usageEvent.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    appStartTime[packageName] = timeStamp
                    if (packageName in controlledPackages && packageName != applicationContext.packageName) {
                        showUsageWarning(packageName)
                    }
                } else if (usageEvent.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    val startTime = appStartTime[packageName] ?: lastCheckedTime
                    val usageTime = timeStamp - startTime
                    if (usageTime > 0) {
                        usageMap[packageName] = (usageMap[packageName] ?: 0) + usageTime
                    }
                    appStartTime.remove(packageName)
                }
            }
        }

        // Add usage time for apps that are still in foreground
        appStartTime.forEach { (packageName, startTime) ->
            val usageTime = currentTime - startTime
            if (usageTime > 0) {
                usageMap[packageName] = (usageMap[packageName] ?: 0) + usageTime
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

                // Normalize date to midnight
                val normalizedDate = Calendar.getInstance().apply {
                    timeInMillis = currentTime
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                database.appUsageDao().insertUsage(
                    AppUsageEntity(
                        packageName = packageName,
                        appName = appName,
                        usageTimeInMillis = timeInForeground,
                        date = normalizedDate,
                        isControlled = packageName in controlledPackages
                    )
                )
            }
        }

        lastCheckedTime = lastEventTime
    }

    private fun showUsageWarning(packageName: String) {
        val intent = Intent(applicationContext, PopupService::class.java).apply {
            putExtra("package_name", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startService(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // If service gets killed, restart it
        return START_STICKY
    }
} 