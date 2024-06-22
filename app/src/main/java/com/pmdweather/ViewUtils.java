package com.pmdweather;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

public class ViewUtils {
    public static void applyTextViewStyle(View root, int styleResId) {
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyTextViewStyle(group.getChildAt(i), styleResId);
            }
        } else if (root instanceof TextView) {
            TextViewCompat.setTextAppearance((TextView) root, styleResId);
        }
    }
}
