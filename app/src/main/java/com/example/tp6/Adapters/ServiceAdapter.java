package com.example.tp6.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tp6.R;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    Context ctx;
    ArrayList<String> services;

    public ServiceAdapter(Context ctx, ArrayList<String> services) {
        this.ctx = ctx;
        this.services = services;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.service_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        h.serviceName.setText(services.get(position));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        public ViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
        }
    }
}

