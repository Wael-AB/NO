package com.example.usagetracker.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.usagetracker.R
import com.example.usagetracker.data.AppDatabase
import com.example.usagetracker.data.AppUsageEntity
import com.example.usagetracker.databinding.ActivityControlListBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ControlListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControlListBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: AppListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        setupRecyclerView()
        loadApps()
    }

    private fun setupRecyclerView() {
        adapter = AppListAdapter { app, isChecked ->
            lifecycleScope.launch {
                database.appUsageDao().updateAppControlStatus(app.packageName, isChecked)
            }
        }
        binding.appList.adapter = adapter
    }

    private fun loadApps() {
        lifecycleScope.launch {
            val packageManager = packageManager
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { app ->
                    packageManager.getLaunchIntentForPackage(app.packageName) != null
                }
                .map { app ->
                    AppItem(
                        packageName = app.packageName,
                        appName = packageManager.getApplicationLabel(app).toString(),
                        icon = packageManager.getApplicationIcon(app.packageName)
                    )
                }
                .sortedBy { it.appName }

            val controlledApps = database.appUsageDao().getControlledApps().first()
                .map { it.packageName }
                .toSet()

            adapter.submitList(installedApps.map { app ->
                app.copy(isControlled = app.packageName in controlledApps)
            })
        }
    }
}

data class AppItem(
    val packageName: String,
    val appName: String,
    val icon: android.graphics.drawable.Drawable,
    val isControlled: Boolean = false
)

class AppListAdapter(
    private val onCheckedChanged: (AppItem, Boolean) -> Unit
) : ListAdapter<AppItem, AppListAdapter.ViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val controlCheckbox: CheckBox = itemView.findViewById(R.id.controlCheckbox)

        fun bind(item: AppItem) {
            appIcon.setImageDrawable(item.icon)
            appName.text = item.appName
            controlCheckbox.isChecked = item.isControlled
            
            controlCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChanged(item, isChecked)
            }
        }
    }
}

class AppDiffCallback : DiffUtil.ItemCallback<AppItem>() {
    override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem): Boolean {
        return oldItem == newItem
    }
} 