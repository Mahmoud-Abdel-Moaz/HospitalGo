package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.FirstAid;

public class FirstAidDetailsActivity extends AppCompatActivity {

    TextView txt_name;
    Intent intent;
    String name;
    RecyclerView recycler;
    StepsAdupter stepsAdupter;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid_details);

        txt_name=findViewById(R.id.txt_name);
        recycler=findViewById(R.id.recycler);

        stepsAdupter=new StepsAdupter(FirstAidDetailsActivity.this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(stepsAdupter);
        intent=getIntent();
        name=intent.getStringExtra("name");
        firebaseFirestore=FirebaseFirestore.getInstance();
        txt_name.setText(name);


        firebaseFirestore.collection("FirstAids").document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    FirstAid firstAid=task.getResult().toObject(FirstAid.class);
                    stepsAdupter.setitemsList(firstAid.getSteps());
                }
            }
        });

    }
}
