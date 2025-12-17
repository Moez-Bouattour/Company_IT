package com.example.tp6.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp6.DatabaseHelper;
import com.example.tp6.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;


public class RegisterActivity extends AppCompatActivity {
    EditText edtName, edtPassword,edtEmail, edtConfirm,edtGender, edtPhone, edtDate, edtPlace;
    Button btnRegister, btnGoLogin;
    DatabaseHelper db;

    Spinner spinnerPlace;
    RadioGroup radioGender;
    RadioButton radioMale, radioFemale;

    String[] gouvernorats = {
            "Tunis", "Ariana", "Ben Arous", "Manouba",
            "Nabeul", "Zaghouan", "Bizerte", "Béja", "Jendouba",
            "Le Kef", "Siliana", "Sousse", "Monastir", "Mahdia",
            "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid",
            "Gabès", "Médenine", "Tataouine", "Gafsa", "Tozeur", "Kebili"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtDate = findViewById(R.id.editDate);
        edtConfirm = findViewById(R.id.edtConfirm);
        spinnerPlace = findViewById(R.id.spinnerPlace);
        radioGender = findViewById(R.id.radioGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        // Remplir spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, gouvernorats);
        spinnerPlace.setAdapter(adapter);

        edtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        edtDate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });


        btnRegister = findViewById(R.id.btnRegister);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        db = new DatabaseHelper(this);

        btnRegister.setOnClickListener(v -> registerUser());
        btnGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String birth = edtDate.getText().toString().trim();
        String place = spinnerPlace.getSelectedItem().toString();
        String role = "USER";
        String gender = radioMale.isChecked() ? "Male" : "Female";

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty() || birth.isEmpty()
                || place.isEmpty() || radioGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "The passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this,"Format email invalid",Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si utilisateur existe déjà
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT * FROM users WHERE email = ?",
                new String[]{email}
        );

        if (c.getCount() > 0) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }


        db.addUser(name, email, pass, role, gender, phone, birth, place);
        Toast.makeText(this, "Registration successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}