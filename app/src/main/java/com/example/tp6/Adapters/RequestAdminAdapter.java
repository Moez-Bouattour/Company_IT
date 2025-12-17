package com.example.tp6.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.Company;
import com.example.tp6.Models.RequestModel;
import com.example.tp6.Models.UserModel;
import com.example.tp6.R;

import java.io.File;
import java.util.List;

public class RequestAdminAdapter extends RecyclerView.Adapter<RequestAdminAdapter.ViewHolder> {

    public interface OnActionListener {
        void onAccept(RequestModel req);
        void onRefuse(RequestModel req);
        void onOpenCV(RequestModel req);
    }

    private Context context;
    private List<RequestModel> list;
    private OnActionListener listener;

    DatabaseHelper db;

    public RequestAdminAdapter(Context context, List<RequestModel> list, OnActionListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request_admin_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {

        RequestModel req = list.get(pos);

        UserModel user = db.getUserById(req.getUserId());
        Company company = db.getCompanyById(req.getCompanyId());
        h.txtUser.setText("User : " + user.getName());
        h.txtCompany.setText("Company : " + company.getName());
        h.txtStatus.setText("Status : " + req.getStatus());

        h.btnAccept.setOnClickListener(v -> listener.onAccept(req));
        h.btnRefuse.setOnClickListener(v -> listener.onRefuse(req));
        h.btnOpenCV.setOnClickListener(v -> {
            File file = new File(req.getCvPath());
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Aucune application PDF installée", Toast.LENGTH_SHORT).show();
            }
        });
        switch (req.getStatus()) {
            case "Pending":
                h.txtStatus.setTextColor(Color.parseColor("#FFA726")); // Orange
                h.btnAccept.setVisibility(View.VISIBLE);
                h.btnRefuse.setVisibility(View.VISIBLE);
                break;
            case "Accepted":
                h.txtStatus.setTextColor(Color.parseColor("#4CAF50")); // Vert
                h.btnAccept.setVisibility(View.GONE);
                h.btnRefuse.setVisibility(View.GONE);
                break;
            case "Refused":
                h.txtStatus.setTextColor(Color.parseColor("#F44336")); // Rouge
                h.btnAccept.setVisibility(View.GONE);
                h.btnRefuse.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtCompany, txtStatus;
        Button btnAccept, btnRefuse, btnOpenCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtCompany = itemView.findViewById(R.id.txtCompany);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRefuse = itemView.findViewById(R.id.btnRefuse);
            btnOpenCV = itemView.findViewById(R.id.btnOpenCV);
        }
    }
}
