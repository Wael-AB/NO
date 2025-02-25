package com.example.usagetracker

import android.app.Application
import com.example.usagetracker.data.AppDatabase

class UsageTrackerApp : Application() {
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
    }
} 