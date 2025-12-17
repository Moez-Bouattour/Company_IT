package com.example.tp6.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Models.Company;
import com.example.tp6.Adapters.CompanyAdapter;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ManageCompaniesActivity extends BaseActivity {

        DatabaseHelper dbHelper;
        Button btnCreate;
        ArrayList<Company> companies;
        RecyclerView recycler;
        String role;
        int userId;
        Spinner spinnerFilter,spinnerFilterPlace;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_manage_companies);

            dbHelper = new DatabaseHelper(this);
            btnCreate = findViewById(R.id.btnCreate);

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            role = prefs.getString("role", "USER"); // USER par défaut
            userId = prefs.getInt("userId",1);

            spinnerFilter = findViewById(R.id.spinnerAvailability);
            spinnerFilterPlace = findViewById(R.id.spinnerLocation);

            spinnerFilter.setVisibility(View.GONE);
            spinnerFilterPlace.setVisibility(View.GONE);

            recycler = findViewById(R.id.listCompanies);
            recycler.setLayoutManager(new LinearLayoutManager(this));

            btnCreate.setOnClickListener(v -> {
                Intent intent = new Intent(ManageCompaniesActivity.this, FormCompanyActivity.class);
                startActivity(intent);
            });

                loadCompanies();
        }

    private void loadCompanies() {
        companies = dbHelper.getAllCompanies();
        recycler.setAdapter(new CompanyAdapter(this, companies,role,userId));
    }
}