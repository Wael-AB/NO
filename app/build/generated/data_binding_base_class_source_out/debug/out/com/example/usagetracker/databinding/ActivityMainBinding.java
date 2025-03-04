// Generated by view binder compiler. Do not edit!
package com.example.usagetracker.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.usagetracker.R;
import com.github.mikephil.charting.charts.PieChart;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button controlListButton;

  @NonNull
  public final Spinner periodSpinner;

  @NonNull
  public final PieChart usageChart;

  private ActivityMainBinding(@NonNull LinearLayout rootView, @NonNull Button controlListButton,
      @NonNull Spinner periodSpinner, @NonNull PieChart usageChart) {
    this.rootView = rootView;
    this.controlListButton = controlListButton;
    this.periodSpinner = periodSpinner;
    this.usageChart = usageChart;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.controlListButton;
      Button controlListButton = ViewBindings.findChildViewById(rootView, id);
      if (controlListButton == null) {
        break missingId;
      }

      id = R.id.periodSpinner;
      Spinner periodSpinner = ViewBindings.findChildViewById(rootView, id);
      if (periodSpinner == null) {
        break missingId;
      }

      id = R.id.usageChart;
      PieChart usageChart = ViewBindings.findChildViewById(rootView, id);
      if (usageChart == null) {
        break missingId;
      }

      return new ActivityMainBinding((LinearLayout) rootView, controlListButton, periodSpinner,
          usageChart);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
