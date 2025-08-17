package com.example.lisdesper.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.lisdesper.R;

public class ThemeHelper {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "current_theme";

    private final Context context;
    private final ImageButton btnThemeToggle; // puede ser null

    public ThemeHelper(Context context, ImageButton btnThemeToggle) {
        this.context = context;
        this.btnThemeToggle = btnThemeToggle;
        init();
    }

    public ThemeHelper(Context context) {
        this(context, null); // nuevo constructor sin botón
    }

    private void init() {
        int savedTheme = getSavedTheme();
        AppCompatDelegate.setDefaultNightMode(savedTheme);
        updateButtonIcon(savedTheme);

        if (btnThemeToggle != null) {
            btnThemeToggle.setOnClickListener(v -> toggleTheme());
        }
    }

    private int getSavedTheme() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void saveTheme(int mode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, mode);
        editor.apply();
    }

    private void updateButtonIcon(int mode) {
        if (btnThemeToggle != null) {
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                btnThemeToggle.setImageResource(R.drawable.imgluna);
            } else {
                btnThemeToggle.setImageResource(R.drawable.imgsol);
            }
        }
    }

    public void setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        saveTheme(AppCompatDelegate.MODE_NIGHT_NO);
        updateButtonIcon(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void setDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        saveTheme(AppCompatDelegate.MODE_NIGHT_YES);
        updateButtonIcon(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void toggleTheme() {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            setLightTheme();
        } else {
            setDarkTheme();
        }
    }
}
