package com.example.usagetracker.data;

import androidx.room.*;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J,\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\'J\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J(\u0010\f\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\rJ$\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\'J\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00f8\u0001\u0000\u0082\u0002\u0006\n\u0004\b!0\u0001\u00a8\u0006\u0017\u00c0\u0006\u0001"}, d2 = {"Lcom/example/usagetracker/data/AppUsageDao;", "", "getAppUsageByTimeRange", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/example/usagetracker/data/AppUsageEntity;", "packageName", "", "startTime", "", "endTime", "getControlledApps", "getTotalUsageTime", "(Ljava/lang/String;JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUsageByTimeRange", "insertUsage", "", "usage", "(Lcom/example/usagetracker/data/AppUsageEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateAppControlStatus", "isControlled", "", "(Ljava/lang/String;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
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
    
    @androidx.room.Query(value = "SELECT * FROM app_usage WHERE isControlled = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.usagetracker.data.AppUsageEntity>> getControlledApps();
    
    @androidx.room.Query(value = "UPDATE app_usage SET isControlled = :isControlled WHERE packageName = :packageName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateAppControlStatus(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, boolean isControlled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT SUM(usageTimeInMillis) FROM app_usage WHERE packageName = :packageName AND date >= :startTime AND date <= :endTime")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTotalUsageTime(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, long startTime, long endTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
}