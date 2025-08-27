package com.example.lisdesper.Helpers;

import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private Context context;

    public ThemeHelper(Context context) {
        this.context = context;
    }

    public void setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void setDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public boolean isLightTheme() {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO;
    }
}