package com.example.usagetracker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.usagetracker.data.AppDatabase;
import kotlinx.coroutines.Dispatchers;
import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0012\u0010\u0010\u001a\u00020\f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0014J\b\u0010\u0013\u001a\u00020\fH\u0014J(\u0010\u0014\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/example/usagetracker/ui/AppLaunchInterceptorActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "PREFS_NAME", "", "RESPONSE_TIME_PREFIX", "TAG", "database", "Lcom/example/usagetracker/data/AppDatabase;", "preferences", "Landroid/content/SharedPreferences;", "checkAppAndShowDialog", "", "packageName", "launchIntent", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "showConfirmationDialog", "appName", "usageTime", "app_debug"})
public final class AppLaunchInterceptorActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.usagetracker.data.AppDatabase database;
    private android.content.SharedPreferences preferences;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String TAG = "AppLaunchInterceptor";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String PREFS_NAME = "AppLaunchPrefs";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String RESPONSE_TIME_PREFIX = "response_time_";
    
    public AppLaunchInterceptorActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void checkAppAndShowDialog(java.lang.String packageName, android.content.Intent launchIntent) {
    }
    
    private final void showConfirmationDialog(java.lang.String appName, android.content.Intent launchIntent, java.lang.String usageTime, java.lang.String packageName) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}