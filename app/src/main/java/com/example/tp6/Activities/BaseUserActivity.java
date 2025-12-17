package com.example.tp6.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.tp6.R;
import com.google.android.material.navigation.NavigationView;

public class BaseUserActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Charger le layout parent
        super.setContentView(R.layout.activity_base_user);

        // Maintenant les views existent
        drawerLayout = findViewById(R.id.baseDrawer);
        navigationView = findViewById(R.id.baseNavigation);
        toolbar = findViewById(R.id.baseToolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_sidebar, R.string.close_sidebar
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        long userId = prefs.getInt("userId", 1);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardUserActivity.class));
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(this, FormUserActivity.class);
                i.putExtra("id", userId);
                startActivity(i);
            }
            else if(id == R.id.nav_logout)
                startActivity(new Intent(this, LoginActivity.class));

            drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        // après que activity_base_user soit chargé
        LinearLayout baseLayout = findViewById(R.id.baseContent);

        // injecte uniquement dans la zone de contenu
        getLayoutInflater().inflate(layoutResID, baseLayout, true);
    }
}
