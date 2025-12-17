package com.example.tp6.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp6.Models.Company;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FormCompanyActivity extends AppCompatActivity {

    EditText edtName, edtServices, edtPhone, edtWebsite, edtEmail;
    Button btnSave,btnReturn;
    DatabaseHelper dbHelper;
    int companyId = -1;

    private static final int PICK_IMAGE = 100;
    Uri selectedImageUri = null;

    private String selectedImagePath = "";
    ImageView imgPreview;
    Spinner spinnerLocation;
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
        setContentView(R.layout.activity_form_company);

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtServices = findViewById(R.id.edtServices);
        edtPhone = findViewById(R.id.edtPhone);
        edtWebsite = findViewById(R.id.edtWebsite);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        btnReturn = findViewById(R.id.btnReturn);

        // Remplir spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, gouvernorats);
        spinnerLocation.setAdapter(adapter);

        imgPreview = findViewById(R.id.imgPreview);
        Button btnPickImage = findViewById(R.id.btnChooseImage);

        companyId = getIntent().getIntExtra("id", -1);

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1);
        });

        if (companyId != -1) {

            Company c = dbHelper.getCompanyById(companyId);
            edtName.setText(c.getName());
            edtServices.setText(c.getServices());
            edtPhone.setText(c.getPhone());
            edtWebsite.setText(c.getWebsite());
            edtEmail.setText(c.getEmail());
            spinnerLocation.setSelection(adapter.getPosition(c.getLocalisation()));
            if (c.getImageUri() != null && !c.getImageUri().isEmpty()) {
                selectedImagePath = c.getImageUri(); // chemin du fichier interne
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                imgPreview.setImageBitmap(bitmap);
            }
        }

        btnReturn.setOnClickListener(v->{

            Intent i = new Intent(this,ManageCompaniesActivity.class);
            startActivity(i);

        });
        btnSave.setOnClickListener(v -> {

            // --- VALIDATIONS ---
            if (!validateFields()) {
                return; // stop execution if invalid
            }

            Company company = new Company(
                    companyId,
                    edtName.getText().toString(),
                    edtServices.getText().toString(),
                    edtPhone.getText().toString(),
                    edtWebsite.getText().toString(),
                    spinnerLocation.getSelectedItem().toString(),
                    selectedImagePath,
                    edtEmail.getText().toString()
            );

            if (companyId == -1)
                dbHelper.insertCompany(company);
            else

                dbHelper.updateCompany(company);

            Toast.makeText(this, "Entreprise enregistrée avec succès", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(FormCompanyActivity.this, ManageCompaniesActivity.class));
            finish();
        });
    }

    // -------------------------
    // VALIDATION DES CHAMPS
    // -------------------------
    private boolean validateFields() {

        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Nom obligatoire");
            edtName.requestFocus();
            return false;
        }

        if (edtServices.getText().toString().trim().isEmpty()) {
            edtServices.setError("Services obligatoires");
            edtServices.requestFocus();
            return false;
        }

        if (edtPhone.getText().toString().trim().isEmpty()) {
            edtPhone.setError("Téléphone obligatoire");
            edtPhone.requestFocus();
            return false;
        }

        if (edtPhone.getText().toString().trim().length() < 8) {
            edtPhone.setError("Téléphone non valide");
            edtPhone.requestFocus();
            return false;
        }

        String website = edtWebsite.getText().toString().trim();

        // 1. Champ obligatoire
                if (website.isEmpty()) {
                    edtWebsite.setError("Site web obligatoire");
                    edtWebsite.requestFocus();
                    return false;
                }

        // 2. Format obligatoire : http://www.nom_site...
                String urlPattern = "^http://www\\.[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}.*$";

                if (!website.matches(urlPattern)) {
                    edtWebsite.setError("Format invalide (ex: http://www.monsite.com)");
                    edtWebsite.requestFocus();
                    return false;
                }


        if (spinnerLocation.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(this,"Location obligatoire",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtEmail.getText().toString().trim().isEmpty()) {
            edtEmail.setError("Email obligatoire");
            edtEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            edtEmail.setError("Email non valide");
            edtEmail.requestFocus();
            return false;
        }

        if (selectedImagePath == null) {
            Toast.makeText(this, "Veuillez choisir une image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                InputStream in = getContentResolver().openInputStream(uri);

                File file = new File(getFilesDir(), "img_" + System.currentTimeMillis() + ".jpg");

                OutputStream out = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                selectedImagePath = file.getAbsolutePath();
                imgPreview.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
