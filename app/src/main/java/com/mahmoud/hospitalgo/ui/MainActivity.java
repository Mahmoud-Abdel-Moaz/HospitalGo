package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.Disease;
import com.mahmoud.hospitalgo.pojo.FirstAid;
import com.mahmoud.hospitalgo.pojo.Person;
import com.mahmoud.hospitalgo.pojo.Step;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;

    FirebaseAuth auth;
    EditText EditText_Email,EditText_Password;
    Button logIn_btn;
    TextView signUpNow_btn;
    String email,pass;

    FirebaseUser firebaseUser;

   @Override
    protected void onStart() {
        super.onStart();
        //Auto Login
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            assert firebaseUser != null;
            String userid = firebaseUser.getUid();
            firebaseFirestore=FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Person person=task.getResult().toObject(Person.class);
                   // Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                    if (person.getType().equals("client")){
                        startActivity(new Intent(MainActivity.this,ClientMapsActivity.class));
                        finish();
                    }else if(person.getType().equals("ambulance")){
                        startActivity(new Intent(MainActivity.this,AmbulanceMapsActivity.class));
                        finish();
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText_Email=findViewById(R.id.EditText_Email);
        EditText_Password=findViewById(R.id.EditText_Password);
        logIn_btn=findViewById(R.id.logIn_btn);
        signUpNow_btn=findViewById(R.id.signUpNow_btn);

        firebaseFirestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();



        logIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUpNow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });

    }

    private void login() {

        email=EditText_Email.getText().toString().trim();
        pass=EditText_Password.getText().toString().trim();
        if (email.isEmpty()) {
            EditText_Email.setError("Email is required ");
            EditText_Email.requestFocus();
        }
        if (pass.isEmpty()) {
            EditText_Password.setError("Password is required");
            EditText_Password.requestFocus();
        }
        if (email.isEmpty()||pass.isEmpty()){
            Toast.makeText(this, "Enter all required Information", Toast.LENGTH_SHORT).show();
            return;
        }else {
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();
                        firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Person person=task.getResult().toObject(Person.class);
                                Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                if (person.getType().equals("client")){
                                    startActivity(new Intent(MainActivity.this,ClientMapsActivity.class));
                                    finish();
                                }else if(person.getType().equals("ambulance")){
                                    startActivity(new Intent(MainActivity.this,AmbulanceMapsActivity.class));
                                    finish();
                                }
                            }
                        });


                    }

                }
            });
        }
    }


}
