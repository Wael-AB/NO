package com.example.usagetracker.data;

import androidx.room.*;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J,\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\n0\t2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\n0\tH\'J\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\nH\u00a7@\u00a2\u0006\u0002\u0010\u0011J$\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\n0\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J(\u0010\u0014\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0015J$\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\n0\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\'J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u001e\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u001dH\u0097@\u00a2\u0006\u0002\u0010\u001eJ\u001e\u0010\u001f\u001a\u00020\u00182\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u001dH\u00a7@\u00a2\u0006\u0002\u0010\u001e\u00f8\u0001\u0000\u0082\u0002\u0006\n\u0004\b!0\u0001\u00a8\u0006 \u00c0\u0006\u0001"}, d2 = {"Lcom/example/usagetracker/data/AppUsageDao;", "", "getAppByPackageName", "Lcom/example/usagetracker/data/AppUsageEntity;", "packageName", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAppNameByPackage", "getAppUsageByTimeRange", "Lkotlinx/coroutines/flow/Flow;", "", "startTime", "", "endTime", "getControlledApps", "Lcom/example/usagetracker/data/ControlledAppInfo;", "getControlledPackageNames", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDailyUsage", "Lcom/example/usagetracker/data/DailyUsage;", "getTotalUsageTime", "(Ljava/lang/String;JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUsageByTimeRange", "insertUsage", "", "usage", "(Lcom/example/usagetracker/data/AppUsageEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAppControlStatus", "isControlled", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateControlStatus", "app_debug"})
@androidx.room.Dao()
public abstract interface AppUsageDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertUsage(@org.jetbrains.annotations.NotNull()
    com.example.usagetracker.data.AppUsageEntity usage, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM app_usage WHERE date >= :startTime AND date <= :endTime")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.usagetracker.data.AppUsageEntity>> getUsageByTimeRange(long startTime, long endTime);
    
    @androidx.room.Query(value = "SELECT * FROM app_usage WHERE packageName = :packageName AND date >= :startTime AND date <= :endTime")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.usagetracker.data.AppUsageEntity>> getAppUsageByTimeRange(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, long startTime, long endTime);
    
    @androidx.room.Query(value = "SELECT DISTINCT packageName, appName, isControlled FROM app_usage WHERE isControlled = 1 GROUP BY packageName")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.usagetracker.data.ControlledAppInfo>> getControlledApps();
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public default java.lang.Object updateAppControlStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, boolean isControlled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @androidx.room.Query(value = "SELECT * FROM app_usage WHERE packageName = :packageName LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAppByPackageName(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.usagetracker.data.AppUsageEntity> $completion);
    
    @androidx.room.Query(value = "SELECT appName FROM app_usage WHERE packageName = :packageName LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAppNameByPackage(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @androidx.room.Query(value = "UPDATE app_usage SET isControlled = :isControlled WHERE packageName = :packageName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateControlStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, boolean isControlled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT SUM(usageTimeInMillis) FROM app_usage WHERE packageName = :packageName AND date >= :startTime AND date <= :endTime")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTotalUsageTime(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, long startTime, long endTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT date, SUM(usageTimeInMillis) as totalUsage FROM app_usage WHERE date >= :startTime AND date <= :endTime GROUP BY date")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.usagetracker.data.DailyUsage>> getDailyUsage(long startTime, long endTime);
    
    @androidx.room.Query(value = "SELECT DISTINCT packageName FROM app_usage WHERE isControlled = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getControlledPackageNames(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
}