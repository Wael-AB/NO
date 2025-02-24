package com.example.usagetracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.usagetracker.service.UsageTrackerService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, UsageTrackerService::class.java)
            context.startService(serviceIntent)
        }
    }
} 