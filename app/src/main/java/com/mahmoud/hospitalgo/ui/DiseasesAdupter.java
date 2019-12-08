package com.mahmoud.hospitalgo.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.Disease;

import java.util.ArrayList;
import java.util.List;


public class DiseasesAdupter extends RecyclerView.Adapter<DiseasesAdupter.DiseaseViewHolder> {

    private List<Disease> itemsList = new ArrayList<>();

    private Context context;

    public DiseasesAdupter(List<Disease> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    public DiseasesAdupter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiseaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseViewHolder holder, final int position) {
        holder.txt_dis.setText(itemsList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(),DiseaseDetailsActivity.class);
                intent.putExtra("name",itemsList.get(position).getName());
                intent.putExtra("info",itemsList.get(position).getInfo());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setitemsList(List<Disease> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public class DiseaseViewHolder extends RecyclerView.ViewHolder {
        TextView txt_dis;
        public DiseaseViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_dis=itemView.findViewById(R.id.txt_dis);
        }
    }
}
