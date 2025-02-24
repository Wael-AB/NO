package com.example.usagetracker;

import android.app.Application;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.example.usagetracker.data.AppDatabase;
import com.example.usagetracker.service.PopupService;
import kotlinx.coroutines.*;
import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u000bH\u0002J\b\u0010\f\u001a\u00020\u000bH\u0007J\b\u0010\r\u001a\u00020\u000bH\u0016J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/example/usagetracker/UsageTrackerApp;", "Landroid/app/Application;", "Landroidx/lifecycle/LifecycleObserver;", "()V", "appScope", "Lkotlinx/coroutines/CoroutineScope;", "database", "Lcom/example/usagetracker/data/AppDatabase;", "usageStatsManager", "Landroid/app/usage/UsageStatsManager;", "checkCurrentApp", "", "onAppForegrounded", "onCreate", "showUsageWarning", "packageName", "", "app_debug"})
public final class UsageTrackerApp extends android.app.Application implements androidx.lifecycle.LifecycleObserver {
    private android.app.usage.UsageStatsManager usageStatsManager;
    private com.example.usagetracker.data.AppDatabase database;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope appScope = null;
    
    public UsageTrackerApp() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @androidx.lifecycle.OnLifecycleEvent(value = androidx.lifecycle.Lifecycle.Event.ON_START)
    public final void onAppForegrounded() {
    }
    
    private final void checkCurrentApp() {
    }
    
    private final void showUsageWarning(java.lang.String packageName) {
    }
}