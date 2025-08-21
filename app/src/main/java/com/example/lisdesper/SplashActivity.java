package com.example.lisdesper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2700;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieAnimationView = findViewById(R.id.lottie_animation);

        lottieAnimationView.playAnimation();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, InicioActivity.class));
            finish();
        }, SPLASH_DURATION);
    }
}
