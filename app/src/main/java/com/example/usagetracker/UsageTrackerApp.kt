package com.example.usagetracker

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.usagetracker.data.AppDatabase
import com.example.usagetracker.service.PopupService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class UsageTrackerApp : Application(), LifecycleObserver {
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var database: AppDatabase
    private var lastCheckedTime = System.currentTimeMillis()

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        database = AppDatabase.getDatabase(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        checkCurrentApp()
    }

    private fun checkCurrentApp() {
        CoroutineScope(Dispatchers.Default).launch {
            val currentTime = System.currentTimeMillis()
            val events = usageStatsManager.queryEvents(
                currentTime - TimeUnit.MINUTES.toMillis(1),
                currentTime
            )
            val event = UsageEvents.Event()
            var lastForegroundApp = ""

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastForegroundApp = event.packageName
                }
            }

            if (lastForegroundApp.isNotEmpty()) {
                database.appUsageDao().getControlledApps().collect { controlledApps ->
                    if (controlledApps.any { it.packageName == lastForegroundApp }) {
                        showUsageWarning(lastForegroundApp)
                    }
                }
            }
        }
    }

    private fun showUsageWarning(packageName: String) {
        val intent = Intent(this, PopupService::class.java).apply {
            putExtra("package_name", packageName)
        }
        startService(intent)
    }
} 