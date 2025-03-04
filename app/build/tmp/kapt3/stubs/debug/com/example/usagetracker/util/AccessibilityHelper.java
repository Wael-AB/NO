package com.example.usagetracker.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\t"}, d2 = {"Lcom/example/usagetracker/util/AccessibilityHelper;", "", "()V", "isAccessibilityServiceEnabled", "", "context", "Landroid/content/Context;", "openAccessibilitySettings", "", "app_debug"})
public final class AccessibilityHelper {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.usagetracker.util.AccessibilityHelper INSTANCE = null;
    
    private AccessibilityHelper() {
        super();
    }
    
    public final boolean isAccessibilityServiceEnabled(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final void openAccessibilitySettings(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}