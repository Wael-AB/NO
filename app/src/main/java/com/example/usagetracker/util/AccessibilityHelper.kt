package com.example.usagetracker.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils

object AccessibilityHelper {
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val serviceName = context.packageName + "/" + 
            context.packageName + ".service.AppLaunchAccessibilityService"
        
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        
        return enabledServices?.contains(serviceName) == true
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
} 