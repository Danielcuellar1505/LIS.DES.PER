package com.example.lisdesper;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.lisdesper.databinding.ActivityInicioBinding;
import com.example.lisdesper.ui.deudores.DeudoresFragment;
import com.example.lisdesper.ui.acreedores.AcreedoresFragment;
import com.example.lisdesper.Helpers.ThemeHelper;
import com.example.lisdesper.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    private ActivityInicioBinding binding;
    private boolean isLightTheme;
    private MenuItem themeMenuItem;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME = "isLightTheme";
    private boolean isDateFilterActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isLightTheme = prefs.getBoolean(KEY_THEME, true);
        ThemeHelper themeHelper = new ThemeHelper(this);
        if (isLightTheme) {
            themeHelper.setLightTheme();
        } else {
            themeHelper.setDarkTheme();
        }

        super.onCreate(savedInstanceState);

        binding = ActivityInicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_deudores, R.id.navigation_acreedores)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_inicio);
        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        themeMenuItem = menu.findItem(R.id.action_toggle_theme);
        updateThemeIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ThemeHelper themeHelper = new ThemeHelper(this);

        if (item.getItemId() == R.id.action_toggle_theme) {
            if (isLightTheme) {
                themeHelper.setDarkTheme();
                isLightTheme = false;
            } else {
                themeHelper.setLightTheme();
                isLightTheme = true;
            }
            prefs.edit().putBoolean(KEY_THEME, isLightTheme).apply();
            updateThemeIcon();
            recreate();
            return true;
        } else if (item.getItemId() == R.id.action_filter_date) {
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_activity_inicio)
                    .getChildFragmentManager()
                    .getFragments()
                    .get(0);
            if (currentFragment instanceof DeudoresFragment) {
                if (isDateFilterActive) {
                    ((DeudoresFragment) currentFragment).clearDateFilter();
                    isDateFilterActive = false;
                    item.setIcon(R.drawable.ic_calendar_black_24dp);
                } else {
                    showCustomCalendarDialog(currentFragment);
                    isDateFilterActive = true;
                    item.setIcon(R.drawable.ic_clear_filter_24dp);
                }
            } else if (currentFragment instanceof AcreedoresFragment) {
                if (isDateFilterActive) {
                    ((AcreedoresFragment) currentFragment).clearDateFilter();
                    isDateFilterActive = false;
                    item.setIcon(R.drawable.ic_calendar_black_24dp);
                } else {
                    showCustomCalendarDialog(currentFragment);
                    isDateFilterActive = true;
                    item.setIcon(R.drawable.ic_clear_filter_24dp);
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCustomCalendarDialog(Fragment fragment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_calendar, null);
        builder.setView(dialogView);

        CalendarView calendarView = dialogView.findViewById(R.id.calendarView);
        final Calendar selectedDate = Calendar.getInstance();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
        });

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selectedDate.getTime());
            if (fragment instanceof DeudoresFragment) {
                ((DeudoresFragment) fragment).filterByDate(formattedDate);
            } else if (fragment instanceof AcreedoresFragment) {
                ((AcreedoresFragment) fragment).filterByDate(formattedDate);
            }
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateThemeIcon() {
        if (themeMenuItem != null) {
            themeMenuItem.setIcon(isLightTheme ? R.drawable.ic_light_mode_black_24dp : R.drawable.ic_dark_mode_black_24dp);
        }
    }
}