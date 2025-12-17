package com.example.tp6.Activities;

import android.content.Intent;
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

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        drawerLayout = findViewById(R.id.baseDrawer);
        navigationView = findViewById(R.id.baseNavigation);
        toolbar = findViewById(R.id.baseToolbar);

        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_sidebar, R.string.close_sidebar
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(
                getResources().getColor(android.R.color.white)
        );

        // gestion du menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if( id ==  R.id.nav_home)
                startActivity(new Intent(this, DashboardActivity.class));

            else if ( id == R.id.nav_companies )
                startActivity(new Intent(this, ManageCompaniesActivity.class));

            else if(id == R.id.nav_users)
                startActivity(new Intent(this, ManageUsersActivity.class));

            else if(id == R.id.nav_requests)
                startActivity(new Intent(this, RequestAdminActivity.class));

            else if(id == R.id.nav_logout)
                startActivity(new Intent(this, LoginActivity.class));

            drawerLayout.closeDrawers();
            return true;
        });
    }

    // injecte le layout de l'activité enfant
    @Override
    public void setContentView(int layoutResID) {
        LinearLayout baseLayout = findViewById(R.id.baseContent);
        getLayoutInflater().inflate(layoutResID, baseLayout, true);
    }
}
