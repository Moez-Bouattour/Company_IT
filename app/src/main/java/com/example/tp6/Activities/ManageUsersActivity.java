package com.example.tp6.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Adapters.ManageUsersAdapter;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.UserModel;
import com.example.tp6.R;

import java.util.List;

public class ManageUsersActivity extends BaseActivity {

    DatabaseHelper db;
    Button btnAddUser;
    RecyclerView recycler;


    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_manage_users);

        db = new DatabaseHelper(this);
        btnAddUser = findViewById(R.id.btnAddUserAdmin);

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormUserActivity.class);
            startActivity(intent);
        });

        recycler = findViewById(R.id.recyclerUsersAdmin);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        List<UserModel> users = db.getAllUsers();
        recycler.setAdapter(new ManageUsersAdapter(this, users, db));

    }
}
