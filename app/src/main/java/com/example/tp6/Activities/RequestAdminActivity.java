package com.example.tp6.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Adapters.RequestAdminAdapter;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.RequestModel;
import com.example.tp6.R;
import java.util.List;

public class RequestAdminActivity extends BaseActivity {

    RecyclerView recyclerView;
    DatabaseHelper db;
    List<RequestModel> requests;
    RequestAdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_admin);

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRequests();
    }

    private void loadRequests() {
        requests = db.getAllRequests();
        adapter = new RequestAdminAdapter(this, requests, new RequestAdminAdapter.OnActionListener() {

            @Override
            public void onAccept(RequestModel req) {
                db.updateRequestStatus(req.getId(), "Accepted");
                Toast.makeText(RequestAdminActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                loadRequests();
            }

            @Override
            public void onRefuse(RequestModel req) {
                db.updateRequestStatus(req.getId(), "Refused");
                Toast.makeText(RequestAdminActivity.this, "Request Refused", Toast.LENGTH_SHORT).show();
                loadRequests();
            }

            @Override
            public void onOpenCV(RequestModel req) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(req.getCvPath()), "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
