package com.example.usagetracker.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.example.usagetracker.data.AppDatabase;
import kotlinx.coroutines.Dispatchers;
import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0006H\u0002J\u0010\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\b\u0010\u0017\u001a\u00020\u0012H\u0016J\b\u0010\u0018\u001a\u00020\u0012H\u0016J\b\u0010\u0019\u001a\u00020\u0012H\u0014J \u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u001c\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00040\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lcom/example/usagetracker/service/AppLaunchAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "()V", "COOLDOWN_PERIOD", "", "TAG", "", "currentDialog", "Landroid/app/AlertDialog;", "database", "Lcom/example/usagetracker/data/AppDatabase;", "lastCheckTime", "lastCheckedPackage", "mainHandler", "Landroid/os/Handler;", "respondedApps", "", "checkAppAndShowDialog", "", "packageName", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onCreate", "onInterrupt", "onServiceConnected", "showConfirmationDialog", "appName", "usageTime", "app_debug"})
public final class AppLaunchAccessibilityService extends android.accessibilityservice.AccessibilityService {
    private com.example.usagetracker.data.AppDatabase database;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String lastCheckedPackage = "";
    private long lastCheckTime = 0L;
    @org.jetbrains.annotations.Nullable()
    private android.app.AlertDialog currentDialog;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler mainHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.Long> respondedApps = null;
    private final long COOLDOWN_PERIOD = 5000L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "AppLaunchAccessibility";
    
    public AppLaunchAccessibilityService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    private final void checkAppAndShowDialog(java.lang.String packageName) {
    }
    
    private final void showConfirmationDialog(java.lang.String appName, java.lang.String packageName, java.lang.String usageTime) {
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
}