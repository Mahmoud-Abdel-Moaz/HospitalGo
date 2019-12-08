package com.mahmoud.hospitalgo.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.Step;

import java.util.ArrayList;
import java.util.List;


public class StepsAdupter extends RecyclerView.Adapter<StepsAdupter.StepsViewHolder> {

    private List<Step> itemsList = new ArrayList<>();

    FirebaseStorage storage ;

    private Context context;
    StorageReference storageRef;

    public StepsAdupter(List<Step> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public StepsAdupter(Context context) {
        this.context = context;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

    }


    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StepsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final StepsViewHolder holder, final int position) {
        Step step=itemsList.get(position);
        holder.step_txt.setText(step.getText());
        if (step.getImage().equals("none")){
            holder.step_img.setVisibility(View.GONE);
        }else{
            storageRef.child(step.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Glide.with(context).load(uri).into(holder.step_img);

                    }catch (Exception e){

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setitemsList(List<Step> itemsList) {
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public class StepsViewHolder extends RecyclerView.ViewHolder {
        TextView step_txt;
        ImageView step_img;
        public StepsViewHolder(@NonNull View itemView) {
            super(itemView);
            step_txt=itemView.findViewById(R.id.step_txt);
            step_img=itemView.findViewById(R.id.step_img);

        }
    }
}
