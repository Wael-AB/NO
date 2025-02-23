// Generated by view binder compiler. Do not edit!
package com.example.usagetracker.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.usagetracker.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityControlListBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final RecyclerView appList;

  @NonNull
  public final TextView descriptionText;

  @NonNull
  public final TextView titleText;

  private ActivityControlListBinding(@NonNull ConstraintLayout rootView,
      @NonNull RecyclerView appList, @NonNull TextView descriptionText,
      @NonNull TextView titleText) {
    this.rootView = rootView;
    this.appList = appList;
    this.descriptionText = descriptionText;
    this.titleText = titleText;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityControlListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityControlListBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_control_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityControlListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appList;
      RecyclerView appList = ViewBindings.findChildViewById(rootView, id);
      if (appList == null) {
        break missingId;
      }

      id = R.id.descriptionText;
      TextView descriptionText = ViewBindings.findChildViewById(rootView, id);
      if (descriptionText == null) {
        break missingId;
      }

      id = R.id.titleText;
      TextView titleText = ViewBindings.findChildViewById(rootView, id);
      if (titleText == null) {
        break missingId;
      }

      return new ActivityControlListBinding((ConstraintLayout) rootView, appList, descriptionText,
          titleText);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
