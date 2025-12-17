package com.example.tp6.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.Activities.FormCompanyActivity;
import com.example.tp6.Activities.FormUserActivity;
import com.example.tp6.DatabaseHelper;
import com.example.tp6.Models.UserModel;
import com.example.tp6.R;

import java.util.List;

public class ManageUsersAdapter extends RecyclerView.Adapter<ManageUsersAdapter.EventViewHolder> {

    private List<UserModel> list;
    private final Context ctx;
    private final DatabaseHelper db;

    public ManageUsersAdapter(Context ctx, List<UserModel> list, DatabaseHelper db) {
        this.ctx = ctx;
        this.list = list;
        this.db = db;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_user_card, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder h, int pos) {
        UserModel u = list.get(pos);

        // Affichage
        h.nom.setText(u.getName());
        h.email.setText(u.getEmail());
        h.gender.setText(u.getGender());
        h.date.setText(u.getDateOfBirth());
        h.phone.setText(u.getPhone());
        h.place.setText(u.getPlace());


        // Supprimer
        h.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(ctx)
                    .setTitle("Delete ?")
                    .setMessage("Delete User : " + u.getName() + " ?")
                    .setPositiveButton("Yes", (dd,i)-> { db.deleteUser((int)u.getId());
                        list.remove(u);
                        notifyDataSetChanged();
                        Toast.makeText(ctx, "User deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null).show();

        });

        // Update
        h.btnEdit.setOnClickListener(v->{
            Intent intent = new Intent(ctx, FormUserActivity.class);
            intent.putExtra("id", u.getId());
            ctx.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nom, email,gender,date,phone,place;
        Button btnDelete,btnEdit;

        public EventViewHolder(@NonNull View v) {
            super(v);
            nom = v.findViewById(R.id.userName);
            email = v.findViewById(R.id.userEmail);
            gender = v.findViewById(R.id.userGender);
            date = v.findViewById(R.id.userDate);
            phone = v.findViewById(R.id.userPhone);
            place = v.findViewById(R.id.userPlace);
            btnDelete = v.findViewById(R.id.btnDelUser);
            btnEdit = v.findViewById(R.id.btnUpdateUser);

        }
    }
}

