package com.example.usagetracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: AppUsageEntity)

    @Query("SELECT * FROM app_usage WHERE date >= :startTime AND date <= :endTime")
    fun getUsageByTimeRange(startTime: Long, endTime: Long): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName AND date >= :startTime AND date <= :endTime")
    fun getAppUsageByTimeRange(packageName: String, startTime: Long, endTime: Long): Flow<List<AppUsageEntity>>

    @Query("SELECT * FROM app_usage WHERE isControlled = 1")
    fun getControlledApps(): Flow<List<AppUsageEntity>>

    @Query("UPDATE app_usage SET isControlled = :isControlled WHERE packageName = :packageName")
    suspend fun updateAppControlStatus(packageName: String, isControlled: Boolean)

    @Query("SELECT SUM(usageTimeInMillis) FROM app_usage WHERE packageName = :packageName AND date >= :startTime AND date <= :endTime")
    suspend fun getTotalUsageTime(packageName: String, startTime: Long, endTime: Long): Long?
} 