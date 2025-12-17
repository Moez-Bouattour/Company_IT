package com.example.tp6.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Adapters.ServiceAdapter;
import com.example.tp6.R;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ImageView imageEntreprise;
    TextView nomEntreprise;
    ListView listViewServices;
    Button btnReturn;
    ImageView iconWeb, iconPhone, iconMail, iconLocation;
    String telephone, siteWeb, email, localisation,role;
    TextView textPhone, textEmail, textWeb, textLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageEntreprise = findViewById(R.id.imageEntreprise);
        nomEntreprise = findViewById(R.id.nomEntreprise);
        RecyclerView recycler = findViewById(R.id.recyclerServices);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        btnReturn = findViewById(R.id.btnReturn);

        iconWeb = findViewById(R.id.iconWeb);
        iconPhone = findViewById(R.id.iconPhone);
        iconMail = findViewById(R.id.iconMail);
        iconLocation = findViewById(R.id.iconLocation);

        textPhone = findViewById(R.id.textPhone);
        textEmail = findViewById(R.id.textEmail);
        textWeb = findViewById(R.id.textWeb);
        textLocation = findViewById(R.id.textLocation);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        role = prefs.getString("role", "USER"); // USER par défaut

        btnReturn.setOnClickListener(v->{

            Intent i ;
            if(role.equals("ADMIN")) {
                i = new Intent(this, ManageCompaniesActivity.class);
                startActivity(i);
            }
            else {
                i = new Intent(this, DashboardUserActivity.class);
                startActivity(i);
            }
        });

        // Récupération des données envoyées par Intent
        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        String s = intent.getStringExtra("services");

        ArrayList<String> services = new ArrayList<>();
        if (s != null) {
            for (String item : s.split("\n")) {
                services.add(item);
            }
        }
        ServiceAdapter adapter = new ServiceAdapter(this, services);
        recycler.setAdapter(adapter);

        telephone = intent.getStringExtra("telephone");
        siteWeb = intent.getStringExtra("url");
        email = intent.getStringExtra("email");
        localisation = intent.getStringExtra("localisation");

        // Affichage dans les TextViews
        textPhone.setText(telephone);
        textEmail.setText(email);
        textWeb.setText(siteWeb);
        textLocation.setText(localisation);
        nomEntreprise.setText(nom);
        String imagePath = getIntent().getStringExtra("image");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageEntreprise.setImageBitmap(bitmap);

        iconWeb.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(siteWeb));
                startActivity(i);
        });

        iconPhone.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephone));
                startActivity(i);
        });

        iconMail.setOnClickListener(v -> {
                showEmailInputDialog();

        });

        iconLocation.setOnClickListener(v -> {
            String geoUri = "geo:0,0?q=" + Uri.encode(localisation);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(mapIntent);
        });

    }
    private void showEmailInputDialog() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 20, 50, 20);
        layout.setLayoutParams(params);

        final EditText editTextSubject = new EditText(this);
        editTextSubject.setHint("Subject");
        editTextSubject.setLayoutParams(params);

        final EditText editTextMessage = new EditText(this);
        editTextMessage.setHint("Message");
        editTextMessage.setLayoutParams(params);

        editTextMessage.setLines(4);

        layout.addView(editTextSubject);
        layout.addView(editTextMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send an e-mail");
        builder.setView(layout);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subject = editTextSubject.getText().toString().trim();
                String message = editTextMessage.getText().toString().trim();
                Intent i = new Intent(Intent.ACTION_SENDTO);

                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, subject);
                i.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(i);
                showRatingBar();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showRatingBar() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Évaluate this company");

        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(30, 30, 30, 30);
        layout.addView(ratingBar);

        dialog.setView(layout);

        dialog.setPositiveButton("OK", (d, which) -> {
            float rating = ratingBar.getRating();
            Toast.makeText(this, "Thank you for your rating : " + rating, Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }
}
