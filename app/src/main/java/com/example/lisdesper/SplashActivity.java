package com.example.lisdesper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView splashText = findViewById(R.id.splash_text);
        ImageView splashIcon = findViewById(R.id.splash_icon);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_entrada);

        splashText.startAnimation(anim);
        splashIcon.startAnimation(anim);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, InicioActivity.class));
            finish();
        }, SPLASH_DURATION);
    }
}
