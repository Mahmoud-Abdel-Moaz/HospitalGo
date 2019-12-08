package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.Disease;

import java.util.ArrayList;
import java.util.List;

public class DiseasesActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    EditText edit_disSearch;
    RecyclerView recycler;
    DiseasesAdupter diseasesAdupter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        edit_disSearch=findViewById(R.id.edit_disSearch);
        recycler=findViewById(R.id.recycler);

        diseasesAdupter=new DiseasesAdupter(DiseasesActivity.this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(diseasesAdupter);
        firebaseFirestore=FirebaseFirestore.getInstance();

        showDate("");

        edit_disSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showDate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void showDate(final String i){
        firebaseFirestore.collection("Diseases").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Disease> diseases=new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //  Log.d(TAG, document.getId() + " => " + document.getData());
                        if (i.equals("")){
                            diseases.add(document.toObject(Disease.class));
                        }else if (!i.isEmpty()){
                            if(document.toObject(Disease.class).getName().toLowerCase().startsWith(i.toLowerCase())){
                                diseases.add(document.toObject(Disease.class));
                            }
                        }

                    }
                    diseasesAdupter.setitemsList(diseases);
                } else {

                }

            }
        });
    }
}
