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

    @Transaction
    suspend fun updateAppControlStatus(packageName: String, isControlled: Boolean) {
        val existingApp = getAppByPackageName(packageName)
        if (existingApp != null) {
            updateControlStatus(packageName, isControlled)
        } else {
            insertUsage(
                AppUsageEntity(
                    packageName = packageName,
                    appName = packageName, // Will be updated when usage is tracked
                    usageTimeInMillis = 0,
                    date = System.currentTimeMillis(),
                    isControlled = isControlled
                )
            )
        }
    }

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppByPackageName(packageName: String): AppUsageEntity?

    @Query("UPDATE app_usage SET isControlled = :isControlled WHERE packageName = :packageName")
    suspend fun updateControlStatus(packageName: String, isControlled: Boolean)

    @Query("SELECT SUM(usageTimeInMillis) FROM app_usage WHERE packageName = :packageName AND date >= :startTime AND date <= :endTime")
    suspend fun getTotalUsageTime(packageName: String, startTime: Long, endTime: Long): Long?

    @Query("SELECT date, SUM(usageTimeInMillis) as totalUsage FROM app_usage WHERE date >= :startTime AND date <= :endTime GROUP BY date")
    fun getDailyUsage(startTime: Long, endTime: Long): Flow<List<DailyUsage>>
} 