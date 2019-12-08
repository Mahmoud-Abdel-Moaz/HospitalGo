package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.AmbulanceLocation;
import com.mahmoud.hospitalgo.pojo.Emergency;
import com.mahmoud.hospitalgo.pojo.PatientLocation;
import com.mahmoud.hospitalgo.pojo.Person;

public class PatientPopupActivity extends AppCompatActivity {

    Button but_accept,but_reject;
    TextView txt_clientname;

    Intent intent;

    String client_name,emergency_id,patient_id;

    FirebaseFirestore firebaseFirestore;

    DatabaseReference mDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_popup);

        txt_clientname=findViewById(R.id.txt_clientname);
        but_accept=findViewById(R.id.but_accept);
        but_reject=findViewById(R.id.but_reject);

        auth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        intent=getIntent();

       // client_name=intent.getStringExtra("name");
        emergency_id=intent.getStringExtra("emergency_id");
       // patient_id=intent.getStringExtra("patient_id");

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*1),(int)(height*.3));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;

        mDatabase.child("Emergencies").child(emergency_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Emergency emergency=dataSnapshot.getValue(Emergency.class);
                patient_id=emergency.getPatient_id();
                firebaseFirestore.collection("Users").document(emergency.getPatient_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Person person=task.getResult().toObject(Person.class);
                            txt_clientname.setText(person.getName());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        but_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Emergencies").child(emergency_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mDatabase.child("Emergencies").child(emergency_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Emergency em=dataSnapshot.getValue(Emergency.class);
                                    patient_id=em.getPatient_id();
                                    mDatabase.child("PatientLocations").child(patient_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            PatientLocation patientLocation=dataSnapshot.getValue(PatientLocation.class);
                                            patientLocation.setStatus("normal");
                                            mDatabase.child("PatientLocations").child(patient_id).setValue(patientLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(PatientPopupActivity.this, "The Request has been rejected", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }
                });
            }
        });

        but_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Emergencies").child(emergency_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Emergency emergency=dataSnapshot.getValue(Emergency.class);
                        emergency.setStatus("accepted");
                        patient_id=emergency.getPatient_id();
                        mDatabase.child("Emergencies").child(emergency_id).setValue(emergency).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUser user=auth.getCurrentUser();
                                final String userId=user.getUid();
                                mDatabase.child("AmbulanceLocations").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        AmbulanceLocation location=dataSnapshot.getValue(AmbulanceLocation.class);
                                        location.setStatus("busy");
                                        mDatabase.child("AmbulanceLocations").child(userId).setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    mDatabase.child("PatientLocations").child(patient_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            PatientLocation patientLocation=dataSnapshot.getValue(PatientLocation.class);
                                                            patientLocation.setStatus("inway");
                                                            mDatabase.child("PatientLocations").child(patient_id).setValue(patientLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        Toast.makeText(PatientPopupActivity.this, "The Request has been accepted", Toast.LENGTH_SHORT).show();
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        /*DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*.7),(int)(height*.8));

        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        but_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference()
                        .child("Send_Orders").child(OrderId);
                reference.removeValue();
                finish();
            }
        });
        but_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SandOrder();
                finish();
            }
        });*/
    }
}
