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
import com.mahmoud.hospitalgo.pojo.FirstAid;

import java.util.ArrayList;
import java.util.List;


public class FirstAidAdupter extends RecyclerView.Adapter<FirstAidAdupter.FirstAidViewHolder> {

    private List<FirstAid> itemsList = new ArrayList<>();

    private Context context;

    public FirstAidAdupter(List<FirstAid> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    public FirstAidAdupter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public FirstAidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FirstAidViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FirstAidViewHolder holder, final int position) {
        holder.txt_aid.setText(itemsList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context.getApplicationContext(),FirstAidDetailsActivity.class);
                intent.putExtra("name",itemsList.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setitemsList(List<FirstAid> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public class FirstAidViewHolder extends RecyclerView.ViewHolder {
        TextView txt_aid;
        public FirstAidViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_aid=itemView.findViewById(R.id.txt_dis);
        }
    }
}
