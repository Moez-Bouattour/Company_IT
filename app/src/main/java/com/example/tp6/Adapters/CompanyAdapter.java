package com.example.tp6.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Activities.DetailActivity;
import com.example.tp6.Activities.FormCompanyActivity;
import com.example.tp6.Activities.ManageCompaniesActivity;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.Company;
import com.example.tp6.Models.RequestModel;
import com.example.tp6.R;

import java.util.ArrayList;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private Context context;
    private List<Company> companyList;
    private List<Company> fullList;
    private DatabaseHelper db;
    private String role;
    private int userId;

    private String selectedStatus = "All Status";
    private String selectedPlace = "All Locations";

    public CompanyAdapter(Context context, List<Company> companyList,String role,int id) {
        this.context = context;
        this.companyList = companyList;
        this.db = new DatabaseHelper(context);
        this.role = role;
        this.userId = id;
        this.fullList = new ArrayList<>(companyList);
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        this.role = prefs.getString("role", "USER"); // "USER" par défaut
    }

    @Override
    public int getItemCount() { return companyList.size(); }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.company_item, parent, false);
        return new CompanyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder h, int pos) {

        Company company = companyList.get(pos);

        h.txtName.setText(company.getName());

        Uri uri = Uri.parse(company.getImageUri());
        h.image.setImageURI(uri);

        if (role.equals("ADMIN")) {
            h.btnEdit.setVisibility(View.VISIBLE);
            h.btnDelete.setVisibility(View.VISIBLE);
            h.btnApply.setVisibility(View.GONE);
            h.txtStatus.setVisibility(View.GONE);
        } else { // USER
            h.btnEdit.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.GONE);

            // Statut du user par rapport à l'entreprise
            RequestModel req = db.getRequestByUserAndCompany(userId, company.getId());
            if (req != null) {
                h.txtStatus.setText("Status: " + req.getStatus());
                switch (req.getStatus()) {
                    case "Pending":
                        h.txtStatus.setTextColor(Color.parseColor("#FFA726"));
                        h.btnApply.setVisibility(View.GONE);
                        break;
                    case "Accepted":
                        h.txtStatus.setTextColor(Color.parseColor("#4CAF50"));
                        h.btnApply.setVisibility(View.GONE);
                        break;
                    case "Refused":
                        h.txtStatus.setTextColor(Color.parseColor("#F44336"));
                        h.btnApply.setVisibility(View.GONE);
                        break;
                }

            }
            else{
                h.btnApply.setVisibility(View.VISIBLE);
            }
        }

        // --------------------------
        // Apply (USER)
        // --------------------------
        h.btnApply.setOnClickListener(v -> {
            // Ouvrir le sélecteur de fichier PDF
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            ((Activity)context).startActivityForResult(intent, 101 + pos); // code unique
        });

        // --------------------------
        // Delete (ADMIN)
        // --------------------------
        h.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete ?")
                    .setMessage("Delete Company : " + company.getName() + " ?")
                    .setPositiveButton("Yes", (dd, i) -> {
                        db.deleteCompany(company.getId());
                        companyList.remove(pos);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // --------------------------
        // Edit (ADMIN)
        // --------------------------
        h.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormCompanyActivity.class);
            intent.putExtra("id", company.getId());
            context.startActivity(intent);
        });

        h.linearLayout.setOnClickListener(v->{
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("nom", company.getName());
            intent.putExtra("services", company.getServices());
            intent.putExtra("telephone", company.getPhone());
            intent.putExtra("url", company.getWebsite());
            intent.putExtra("email", company.getEmail());
            intent.putExtra("localisation", company.getLocalisation());
            intent.putExtra("image", company.getImageUri());
            context.startActivity(intent);
        });
    }


    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
    TextView txtName,txtStatus;
    Button btnEdit, btnDelete,btnApply;
    ImageView image;

    LinearLayout linearLayout;

    public CompanyViewHolder(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtCompanyName);
        txtStatus = itemView.findViewById(R.id.txtCompanyStatus);
        btnDelete = itemView.findViewById(R.id.btnDelete);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnApply = itemView.findViewById(R.id.btnApply);
        image = itemView.findViewById(R.id.imageView);
        linearLayout = itemView.findViewById(R.id.linearLayout);
    }
}
    public Company getCompanyAt(int position) {
        return companyList.get(position);
    }

    public void updateCompanies(List<Company> newList) {
        fullList.clear();
        fullList.addAll(newList);
        companyList.clear();
        companyList.addAll(newList);
        notifyDataSetChanged();
    }


    // fonction pour le filtrage
    public void filter(String selection) {
        selectedStatus = selection;
        applyFilters();
    }

    public void filterPlace(String selection) {
        selectedPlace = selection;
        applyFilters();
    }

    private void applyFilters() {
        companyList.clear();

        for (Company c : fullList) {

            boolean statusOk = true;
            boolean placeOk = true;

            // ---------- FILTRE STATUS ----------
            if (!selectedStatus.equals("All Status")) {
                RequestModel req = db.getRequestByUserAndCompany(userId, c.getId());
                statusOk = (req != null && selectedStatus.equalsIgnoreCase(req.getStatus()));
            }

            // ---------- FILTRE LOCALISATION ----------
            if (!selectedPlace.equals("All Locations")) {
                placeOk = c.getLocalisation()
                        .equalsIgnoreCase(selectedPlace);
            }

            // ---------- RESULTAT ----------
            if (statusOk && placeOk) {
                companyList.add(c);
            }
        }

        notifyDataSetChanged();
    }

}


