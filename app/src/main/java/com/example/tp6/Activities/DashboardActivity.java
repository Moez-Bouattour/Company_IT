package com.example.tp6.Activities;

import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.tp6.DatabaseHelper;
import com.example.tp6.R;

public class DashboardActivity extends BaseActivity {

    CardView cardUsers, cardCompanies,cardRequests;
    TextView txtUsersCount, txtCompaniesCount,txtRequestsCount;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        cardUsers = findViewById(R.id.cardUsers);
        cardCompanies = findViewById(R.id.cardCompanies);
        cardRequests = findViewById(R.id.cardRequests);

        db = new DatabaseHelper(this);

        txtUsersCount = findViewById(R.id.txtUsersCount);
        txtCompaniesCount = findViewById(R.id.txtCompaniesCount);
        txtRequestsCount = findViewById(R.id.txtRequestsCount);

        int numberOfUsers = db.getAllUsers().size();
        int numberOfCompanies = db.getAllCompanies().size();
        int numberOfRequests = db.getAllRequests().size();

        txtUsersCount.setText(String.valueOf(numberOfUsers));
        txtCompaniesCount.setText(String.valueOf(numberOfCompanies));
        txtRequestsCount.setText(String.valueOf(numberOfRequests));

        cardUsers.setOnClickListener(v ->
                startActivity(new Intent(this, ManageUsersActivity.class))
        );

        cardCompanies.setOnClickListener(v ->
                startActivity(new Intent(this, ManageCompaniesActivity.class))
        );

        cardRequests.setOnClickListener(v ->
                startActivity(new Intent(this, RequestAdminActivity.class))
        );
    }
}
