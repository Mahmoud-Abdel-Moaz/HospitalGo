package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.AmbulanceLocation;
import com.mahmoud.hospitalgo.pojo.PatientLocation;
import com.mahmoud.hospitalgo.pojo.Person;

public class SignUpActivity extends AppCompatActivity {

    EditText edit_name,edit_email,edit_mobile,edit_password,edit_cPassword;

    Button signUp_btn;

    Spinner spinner;

    String name,email,phone,pass,cpass,type;

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edit_name=findViewById(R.id.edit_name);
        edit_email=findViewById(R.id.edit_email);
        edit_mobile=findViewById(R.id.edit_mobile);
        edit_password=findViewById(R.id.edit_password);
        edit_cPassword=findViewById(R.id.edit_cPassword);
        signUp_btn=findViewById(R.id.signUp_btn);
        spinner=findViewById(R.id.spinner);



        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        type="client";

        ArrayAdapter adapter=ArrayAdapter.createFromResource(
                this,
                R.array.spinner_array,
                R.layout.color_spinner_layout
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
               if (position==0){
                   type="client";
                 //  Toast.makeText(SignUpActivity.this, type, Toast.LENGTH_SHORT).show();
               }else if (position==1){
                   type="ambulance";
                 //  Toast.makeText(SignUpActivity.this, type, Toast.LENGTH_SHORT).show();
               }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp(){
        name=edit_name.getText().toString().trim();
        email=edit_email.getText().toString().trim();
        phone=edit_mobile.getText().toString().trim();
        pass=edit_password.getText().toString().trim();
        cpass=edit_cPassword.getText().toString().trim();
        //trim to eliminate spaces

        if (name.isEmpty()) {
            edit_name.setError("Name is required");
            edit_name.requestFocus();
        }
        if (phone.isEmpty()) {
            edit_mobile.setError("phone number is required");
            edit_mobile.requestFocus();
        }

        if (email.isEmpty()) {
            edit_email.setError("Email is required ");
            edit_email.requestFocus();
        }
        if (!email.contains("@")) {
            edit_email.setError("it's not Email ");
            edit_email.requestFocus();
        }

        if (pass.isEmpty()) {
            edit_password.setError("Password is required");
            edit_password.requestFocus();
        }

        if (!cpass.equals(pass)) {
            edit_cPassword.setError("It's not the same Password");
            edit_cPassword.requestFocus();
        }

        if (name.isEmpty()||phone.isEmpty()||email.isEmpty()||!email.contains("@")||pass.isEmpty()||!cpass.equals(pass)){
            return;
        }else {
            final Person person=new Person(name,email,phone,type);

            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();
                        firebaseFirestore.collection("Users").document(userid).set(person).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    assert firebaseUser != null;
                                    String userid = firebaseUser.getUid();
                                    if (type.equals("client")){
                                        PatientLocation patientLocation=new PatientLocation(0.0,0.0,"normal");
                                        //stored in realtime data base instead of fire store
                                        mDatabase.child("PatientLocations").child(userid).setValue(patientLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    startActivity(new Intent(SignUpActivity.this,ClientMapsActivity.class));
                                                    finish();
                                                }
                                            }
                                        });

                                    }else if (type.equals("ambulance")){
                                        AmbulanceLocation ambulanceLocation=new AmbulanceLocation(0.0,0.0,"free");
                                        mDatabase.child("AmbulanceLocations").child(userid).setValue(ambulanceLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                startActivity(new Intent(SignUpActivity.this,AmbulanceMapsActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                  /*  firebaseFirestore.collection("Locations").document(userid).set(new PatientLocation(0.0,0.0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                if (type.equals("client")){
                                                    startActivity(new Intent(SignUpActivity.this,ClientMapsActivity.class));
                                                }else if (type.equals("ambulance")){
                                                    startActivity(new Intent(SignUpActivity.this,AmbulanceMapsActivity.class));
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });*/

                                }

                            }
                        });
                    }
                }
            });

        }

    }
}
