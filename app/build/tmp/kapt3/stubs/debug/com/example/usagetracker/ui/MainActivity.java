package com.example.usagetracker.ui;

import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import com.example.usagetracker.data.AppDatabase;
import com.example.usagetracker.databinding.ActivityMainBinding;
import com.example.usagetracker.service.UsageTrackerService;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0002J\b\u0010\t\u001a\u00020\bH\u0002J\u0012\u0010\n\u001a\u00020\b2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0014J\b\u0010\r\u001a\u00020\bH\u0002J\b\u0010\u000e\u001a\u00020\bH\u0002J\b\u0010\u000f\u001a\u00020\bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/example/usagetracker/ui/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/example/usagetracker/databinding/ActivityMainBinding;", "database", "Lcom/example/usagetracker/data/AppDatabase;", "checkPermissions", "", "loadUsageData", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupButtons", "setupChart", "startUsageTracking", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.example.usagetracker.databinding.ActivityMainBinding binding;
    private com.example.usagetracker.data.AppDatabase database;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void checkPermissions() {
    }
    
    private final void setupChart() {
    }
    
    private final void setupButtons() {
    }
    
    private final void startUsageTracking() {
    }
    
    private final void loadUsageData() {
    }
}