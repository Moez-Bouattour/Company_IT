package com.example.tp6.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tp6.R;

public class SplashCompanyActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 secondes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_company);

        new Handler().postDelayed(() -> {

            Intent intent;
            intent = new Intent(this, LoginActivity.class);

            startActivity(intent);
            finish();

        }, SPLASH_DELAY);
    }
}