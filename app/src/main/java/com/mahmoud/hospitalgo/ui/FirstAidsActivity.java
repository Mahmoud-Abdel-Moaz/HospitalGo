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
import com.mahmoud.hospitalgo.pojo.FirstAid;

import java.util.ArrayList;
import java.util.List;

public class FirstAidsActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    EditText edit_aidSearch;
    RecyclerView recycler;
    FirstAidAdupter firstAidAdupter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aids);

        edit_aidSearch=findViewById(R.id.edit_aidSearch);
        recycler=findViewById(R.id.recycler);

        firstAidAdupter=new FirstAidAdupter(FirstAidsActivity.this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(firstAidAdupter);
        firebaseFirestore=FirebaseFirestore.getInstance();

        showDate("");

        edit_aidSearch.addTextChangedListener(new TextWatcher() {
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
        firebaseFirestore.collection("FirstAids").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<FirstAid> firstAids=new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //  Log.d(TAG, document.getId() + " => " + document.getData());
                        if (i.equals("")){
                            firstAids.add(document.toObject(FirstAid.class));
                        }else if (!i.isEmpty()){
                            if(document.toObject(FirstAid.class).getName().toLowerCase().startsWith(i.toLowerCase())){
                                firstAids.add(document.toObject(FirstAid.class));
                            }
                        }

                    }
                    firstAidAdupter.setitemsList(firstAids);
                } else {

                }

            }
        });
    }
}
