package com.example.usagetracker.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.usagetracker.R
import com.example.usagetracker.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PopupService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var popupView: View
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        database = AppDatabase.getDatabase(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val packageName = intent?.getStringExtra("package_name") ?: return START_NOT_STICKY
        showPopup(packageName)
        return START_NOT_STICKY
    }

    private fun showPopup(packageName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val currentTime = System.currentTimeMillis()
            val todayStart = currentTime - TimeUnit.HOURS.toMillis(24)
            
            val usageTime = database.appUsageDao().getTotalUsageTime(
                packageName,
                todayStart,
                currentTime
            ) ?: 0L

            val lastDayUsageTime = database.appUsageDao().getTotalUsageTime(
                packageName,
                todayStart - TimeUnit.HOURS.toMillis(24),
                todayStart
            ) ?: 0L

            createAndShowPopupWindow(packageName, usageTime, lastDayUsageTime)
        }
    }

    private fun createAndShowPopupWindow(packageName: String, todayUsage: Long, lastDayUsage: Long) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER

        val todayHours = TimeUnit.MILLISECONDS.toHours(todayUsage)
        val todayMinutes = TimeUnit.MILLISECONDS.toMinutes(todayUsage) % 60
        val lastDayHours = TimeUnit.MILLISECONDS.toHours(lastDayUsage)
        val lastDayMinutes = TimeUnit.MILLISECONDS.toMinutes(lastDayUsage) % 60

        popupView.findViewById<TextView>(R.id.usageText).text = 
            "Today's usage: ${todayHours}h ${todayMinutes}m\n" +
            "Last 24h usage: ${lastDayHours}h ${lastDayMinutes}m"

        popupView.findViewById<Button>(R.id.okButton).setOnClickListener {
            windowManager.removeView(popupView)
            stopSelf()
        }

        windowManager.addView(popupView, params)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (::popupView.isInitialized && popupView.isAttachedToWindow) {
            windowManager.removeView(popupView)
        }
    }
} 