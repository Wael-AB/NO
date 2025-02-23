package com.example.usagetracker.ui

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.usagetracker.data.AppDatabase
import com.example.usagetracker.databinding.ActivityMainBinding
import com.example.usagetracker.service.UsageTrackerService
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AppDatabase.getDatabase(this)

        checkPermissions()
        setupChart()
        setupButtons()
        startUsageTracking()
        loadUsageData()
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
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setScaleEnabled(true)
            setPinchZoom(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        val date = Calendar.getInstance().apply {
                            timeInMillis = value.toLong()
                        }
                        return dateFormat.format(date.time)
                    }
                }
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val hours = TimeUnit.MILLISECONDS.toHours(value.toLong())
                        return "${hours}h"
                    }
                }
            }

            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun setupButtons() {
        binding.controlListButton.setOnClickListener {
            startActivity(Intent(this, ControlListActivity::class.java))
        }
    }

    private fun startUsageTracking() {
        startService(Intent(this, UsageTrackerService::class.java))
    }

    private fun loadUsageData() {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startTime = calendar.timeInMillis

            database.appUsageDao().getUsageByTimeRange(startTime, System.currentTimeMillis())
                .collect { usageList ->
                    val dailyUsage = usageList.groupBy { usage ->
                        Calendar.getInstance().apply {
                            timeInMillis = usage.date
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis
                    }

                    val entries = dailyUsage.map { (date, usages) ->
                        BarEntry(
                            date.toFloat(),
                            usages.sumOf { it.usageTimeInMillis }.toFloat()
                        )
                    }.sortedBy { it.x }

                    val dataSet = BarDataSet(entries, "Daily Usage").apply {
                        setDrawValues(true)
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val hours = TimeUnit.MILLISECONDS.toHours(value.toLong())
                                return "${hours}h"
                            }
                        }
                    }

                    binding.usageChart.data = BarData(dataSet)
                    binding.usageChart.invalidate()
                }
        }
    }
} 