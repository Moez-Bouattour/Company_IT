package com.example.tp6.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.UserModel;
import com.example.tp6.R;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        db = new DatabaseHelper(this);
        TextView textRegister = findViewById(R.id.textView6);
        btnLogin.setOnClickListener(v -> validateLogin(v));
        textRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void validateLogin(View view) {
        String sEmail = email.getText().toString().trim();
        String sPass = password.getText().toString().trim();

        boolean ok = db.checkUser(sEmail,sPass);
        Cursor c = db.login(sEmail, sPass);

        if (sEmail.isEmpty() ) {
            Snackbar.make(view, "Enter your Email", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            Snackbar.make(view, "Email invalid ", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (sPass.isEmpty() ) {
            Snackbar.make(view, "Enter your password", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!ok){
            Snackbar.make(view, "Login incorrect", Snackbar.LENGTH_LONG).show();
            return;
        }

        UserModel u = db.getUserByEmail(sEmail);

        c.moveToFirst();
        int userId = c.getInt(c.getColumnIndex("id"));

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("role", u.getRole());
        editor.putInt("userId",userId);
        editor.apply();

        if (u.getRole().equals("ADMIN")){
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
        else {
            Intent i = new Intent(this, DashboardUserActivity.class);
            i.putExtra("id",userId);
            startActivity(i);
        }
    }

}