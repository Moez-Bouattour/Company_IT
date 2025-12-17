package com.example.tp6.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Adapters.CompanyAdapter;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.Company;
import com.example.tp6.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DashboardUserActivity extends BaseUserActivity {

    DatabaseHelper dbHelper;
    ArrayList<Company> companies;
    RecyclerView recycler;
    String role;
    int userId;
    Button btnCreate;
    CompanyAdapter adapter;
    Spinner spinnerFilter,spinnerFilterLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_companies);

        dbHelper = new DatabaseHelper(this);
        btnCreate = findViewById(R.id.btnCreate);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        role = prefs.getString("role", "USER"); // USER par défaut
        userId = prefs.getInt("userId",1);
        btnCreate= findViewById(R.id.btnCreate);

        recycler = findViewById(R.id.listCompanies);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        spinnerFilter = findViewById(R.id.spinnerAvailability);
        spinnerFilterLocation = findViewById(R.id.spinnerLocation);

        adapter = new CompanyAdapter(this, dbHelper.getAllCompanies(),role, userId);
        recycler.setAdapter(adapter);

        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"All Status", "Pending", "Accepted", "Refused"});
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spAdapter);

        spinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id)
            {
                String filter = spinnerFilter.getSelectedItem().toString();
                adapter.filter(filter);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        ArrayAdapter<String> spAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"All Locations", "Tunis", "Ariana", "Ben Arous", "Manouba",
                        "Nabeul", "Zaghouan", "Bizerte", "Béja", "Jendouba",
                        "Le Kef", "Siliana", "Sousse", "Monastir", "Mahdia",
                        "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid",
                        "Gabès", "Médenine", "Tataouine", "Gafsa", "Tozeur", "Kebili"})
                ;
        spAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterLocation.setAdapter(spAdapter1);

        spinnerFilterLocation.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id)
            {
                String filter = spinnerFilterLocation.getSelectedItem().toString();
                adapter.filterPlace(filter);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnCreate.setVisibility(View.GONE);
        loadCompanies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCompanies();
    }

    private void loadCompanies() {
        adapter.updateCompanies(dbHelper.getAllCompanies());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = savePdfToInternalStorage(uri); // méthode pour sauvegarder localement
            int companyId = requestCode - 101;
            dbHelper.addRequest(userId, companies.get(companyId).getId(), path);
            Toast.makeText(this, "Request sent !", Toast.LENGTH_SHORT).show();
            loadCompanies();
        }
    }

    private String savePdfToInternalStorage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "CV_" + System.currentTimeMillis() + ".pdf");
            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) os.write(buffer, 0, len);
            os.close(); is.close();
            return file.getAbsolutePath();
        } catch(Exception e){ e.printStackTrace(); }
        return null;
    }

}