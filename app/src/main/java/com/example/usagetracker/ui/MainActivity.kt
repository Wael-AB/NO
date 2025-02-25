package com.example.usagetracker.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.usagetracker.R
import com.example.usagetracker.data.AppDatabase
import com.example.usagetracker.databinding.ActivityMainBinding
import com.example.usagetracker.service.UsageTrackerService
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.usagetracker.util.AccessibilityHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var periodSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AppDatabase.getDatabase(this)
        periodSpinner = binding.periodSpinner

        checkPermissions()
        checkAccessibilityPermission()
        setupChart()
        setupSpinner()
        setupButtons()
        startUsageTracking()
    }

    override fun onResume() {
        super.onResume()
        checkAccessibilityPermission()
        loadUsageData()
    }

    private fun checkPermissions() {
        val hasUsageStatsPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                packageName
            ) == AppOpsManager.MODE_ALLOWED
        } else {
            @Suppress("DEPRECATION")
            val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                packageName
            ) == AppOpsManager.MODE_ALLOWED
        }
        
        if (!hasUsageStatsPermission) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    private fun checkAccessibilityPermission() {
        if (!AccessibilityHelper.isAccessibilityServiceEnabled(this)) {
            showAccessibilityPermissionDialog()
        }
    }

    private fun showAccessibilityPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("To control app launches, Usage Tracker needs Accessibility Service permission. Please enable it in Settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                AccessibilityHelper.openAccessibilitySettings(this)
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "App launch control requires accessibility permission", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun setupChart() {
        binding.usageChart.apply {
            description.isEnabled = false
            setDrawEntryLabels(true)
            legend.isEnabled = true
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            centerText = "App Usage"
            setCenterTextSize(16f)
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.period_options,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        
        periodSpinner.adapter = adapter
        periodSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                loadUsageData()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
        
        periodSpinner.setSelection(0)
    }

    private fun setupButtons() {
        binding.controlListButton.setOnClickListener {
            startActivity(Intent(this, ControlListActivity::class.java))
        }
    }

    private fun startUsageTracking() {
        startService(Intent(this, UsageTrackerService::class.java))
        loadUsageData()
    }

    private fun loadUsageData() {
        lifecycleScope.launch {
            val currentTime = System.currentTimeMillis()
            val startTime = when (periodSpinner.selectedItem.toString()) {
                "Day" -> currentTime - TimeUnit.HOURS.toMillis(24)
                "Week" -> currentTime - TimeUnit.DAYS.toMillis(7)
                "Month" -> currentTime - TimeUnit.DAYS.toMillis(30)
                "Year" -> currentTime - TimeUnit.DAYS.toMillis(365)
                else -> currentTime - TimeUnit.HOURS.toMillis(24)
            }

            binding.usageChart.centerText = when (periodSpinner.selectedItem.toString()) {
                "Day" -> "Last 24 Hours"
                "Week" -> "Last 7 Days"
                "Month" -> "Last 30 Days"
                "Year" -> "Last 365 Days"
                else -> "Last 24 Hours"
            }

            // Get only controlled apps
            val controlledApps = database.appUsageDao().getControlledApps().first()
            
            if (controlledApps.isEmpty()) {
                binding.usageChart.clear()
                binding.usageChart.centerText = "No controlled apps"
                return@launch
            }
            
            val entries = mutableListOf<PieEntry>()
            
            for (app in controlledApps) {
                val usageTime = database.appUsageDao().getTotalUsageTime(
                    app.packageName,
                    startTime,
                    currentTime
                ) ?: 0L

                if (usageTime > 0) {
                    val hours = TimeUnit.MILLISECONDS.toHours(usageTime).toFloat()
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(usageTime) % 60
                    val displayValue = if (hours < 1) {
                        "${minutes}m"
                    } else {
                        String.format("%.1fh", hours)
                    }
                    entries.add(PieEntry(hours, app.appName, displayValue))
                }
            }

            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "Hours Used").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    valueTextSize = 14f
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return if (value < 1) {
                                String.format("%.0f min", value * 60)
                            } else {
                                String.format("%.1f h", value)
                            }
                        }
                    }
                }

                binding.usageChart.data = PieData(dataSet)
                binding.usageChart.invalidate()
            } else {
                binding.usageChart.clear()
                binding.usageChart.centerText = "No usage data"
            }
        }
    }
} 