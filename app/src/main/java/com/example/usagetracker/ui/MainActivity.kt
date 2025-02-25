package com.example.usagetracker.ui

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        setupChart()
        setupSpinner()
        setupButtons()
        startUsageTracking()
    }

    private fun checkPermissions() {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        
        if (mode != AppOpsManager.MODE_ALLOWED) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
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
                "Day" -> {
                    Calendar.getInstance().apply {
                        timeInMillis = currentTime
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                }
                "Week" -> currentTime - TimeUnit.DAYS.toMillis(7)
                "Month" -> currentTime - TimeUnit.DAYS.toMillis(30)
                "Year" -> currentTime - TimeUnit.DAYS.toMillis(365)
                else -> currentTime - TimeUnit.DAYS.toMillis(1)
            }

            val controlledApps = database.appUsageDao().getControlledApps().first()
            val entries = mutableListOf<PieEntry>()
            
            for (app in controlledApps) {
                val usageTime = database.appUsageDao().getTotalUsageTime(
                    app.packageName,
                    startTime,
                    currentTime
                ) ?: 0L

                if (usageTime > 0) {
                    val hours = TimeUnit.MILLISECONDS.toHours(usageTime).toFloat()
                    entries.add(PieEntry(hours, app.appName))
                }
            }

            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "Hours Used").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    valueTextSize = 14f
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.1f h", value)
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