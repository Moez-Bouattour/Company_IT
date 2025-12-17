package com.example.tp6.Activities;

import android.app.DatePickerDialog;
    import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.RadioButton;
    import android.widget.RadioGroup;
    import android.widget.Spinner;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.tp6.DatabaseHelper;
    import com.example.tp6.Models.Company;
    import com.example.tp6.Models.UserModel;
    import com.example.tp6.R;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.util.Calendar;
    public class FormUserActivity extends BaseUserActivity {

        EditText edtName, edtPhone, edtDate, edtEmail, edtPassword;
        Button btnSave,btnReturn;
        DatabaseHelper dbHelper;
        long userId = -1;
        Spinner spinnerPlace;
        RadioGroup radioGender;
        RadioButton radioMale, radioFemale;
        String role1;

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
            setContentView(R.layout.activity_form_user);

            dbHelper = new DatabaseHelper(this);

            edtName = findViewById(R.id.edtName);
            edtPhone = findViewById(R.id.edtPhone);
            edtDate = findViewById(R.id.edtDate);
            edtEmail = findViewById(R.id.edtEmail);
            edtPassword = findViewById(R.id.edtPassword);
            spinnerPlace = findViewById(R.id.spinnerPlace);

            radioGender = findViewById(R.id.radioGender);
            radioMale = findViewById(R.id.radioMale);
            radioFemale = findViewById(R.id.radioFemale);

            // Remplir spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_dropdown_item, gouvernorats);
            spinnerPlace.setAdapter(adapter);

            btnSave = findViewById(R.id.btnSave);
            btnReturn = findViewById(R.id.btnReturn);

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            role1 = prefs.getString("role", "USER"); // USER par défaut
            userId = getIntent().getLongExtra("id", -1);

            if (role1.equals("ADMIN")) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                navigationView.setVisibility(View.GONE);
            }

            edtDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();

                // Si l'utilisateur est en mode édition, initialiser le calendrier avec la date de naissance
                String dateText = edtDate.getText().toString().trim();
                if (!dateText.isEmpty()) {
                    // date attendue au format "dd/MM/yyyy"
                    String[] parts = dateText.split("/");
                    if (parts.length == 3) {
                        try {
                            int day = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]) - 1; // Calendar.MONTH est 0-index
                            int year = Integer.parseInt(parts[2]);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.YEAR, year);
                        } catch (NumberFormatException e) {
                            e.printStackTrace(); // fallback à la date actuelle
                        }
                    }
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            edtDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            });


            if (userId != -1) {
                UserModel u = dbHelper.getUserById((int) userId);
                edtName.setText(u.getName());
                edtPhone.setText(u.getPhone());
                edtDate.setText(u.getDateOfBirth());
                edtEmail.setText(u.getEmail());
                edtPassword.setText(dbHelper.decryptPassword(u.getPassword()));

                if (u.getGender().equals("Male")) radioMale.setChecked(true);
                else radioFemale.setChecked(true);

                spinnerPlace.setSelection(adapter.getPosition(u.getPlace()));
            }

            if (role1.equals("USER")){
                btnReturn.setVisibility(View.GONE);
            }

            btnReturn.setOnClickListener(v->{

                Intent i= new Intent(this, ManageUsersActivity.class);
                startActivity(i);
            });

            btnSave.setOnClickListener(v -> {

                // --- VALIDATIONS ---
                if (!validateFields()) {
                    return; // stop execution if invalid
                }

                String gender = radioMale.isChecked() ? "Male" : "Female";

                String name = edtName.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String birth = edtDate.getText().toString().trim();
                String place = spinnerPlace.getSelectedItem().toString();
                String role = "USER";

                UserModel user = new UserModel(
                 (int)userId,
                 name,
                 pass,
                 email,
                 role,
                 gender,
                 phone,
                 birth,
                 place
                );

                if (userId == -1)
                    dbHelper.addUser(name,email,pass,role,gender,phone,birth,place);
                else

                    dbHelper.updateUser(user);

                Toast.makeText(this, "User Saved successfully", Toast.LENGTH_SHORT).show();

                Intent i ;
                if(role1.equals("ADMIN")){
                    i = new Intent(this, ManageUsersActivity.class);
                }
                else{
                    i = new Intent(this, DashboardUserActivity.class);
                }
                startActivity(i);
                finish();
            });
        }

        // -------------------------
        // VALIDATION DES CHAMPS
        // -------------------------
        private boolean validateFields() {

            if (edtName.getText().toString().trim().isEmpty()) {
                edtName.setError("Username required");
                edtName.requestFocus();
                return false;
            }

            if (radioGender.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Gender required", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (edtPhone.getText().toString().trim().isEmpty()) {
                edtPhone.setError("Phone required");
                edtPhone.requestFocus();
                return false;
            }

            if (edtPhone.getText().toString().trim().length() != 8) {
                edtPhone.setError("Phone invalid");
                edtPhone.requestFocus();
                return false;
            }

            if (edtDate.getText().toString().trim().isEmpty()) {
                edtDate.setError("Date required");
                edtDate.requestFocus();
                return false;
            }

            if (edtEmail.getText().toString().trim().isEmpty()) {
                edtEmail.setError("Email required");
                edtEmail.requestFocus();
                return false;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
                edtEmail.setError("Email invalid");
                edtEmail.requestFocus();
                return false;
            }

            if (edtPassword.getText().toString().isEmpty()) {
                edtPassword.setError("Password required");
                edtPassword.requestFocus();
                return false;
            }

            return true;
        }

    }