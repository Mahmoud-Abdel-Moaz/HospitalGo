package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.text.DecimalFormat;

public class ClientMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button but_emergency,but_diseases,but_firstAids,but_done,but_call,but_log_out;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng marker_you;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    LinearLayout lin_normal,lin_emg;
    DatabaseReference databaseReference;
    ProgressDialog pd;
    private static final int REQUSET_CALL=2;

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:

                return true;
        }
        return false;
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);
                }
            }
        }
        if (requestCode==REQUSET_CALL){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                call();
            }else {
                Toast.makeText(this, "You must Allow for make a call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Patient");
        //toolbar.inflateMenu(R.menu.menu);

        but_diseases=findViewById(R.id.but_diseases);
        but_emergency=findViewById(R.id.but_emergency);
        but_firstAids=findViewById(R.id.but_firstAids);
        but_done=findViewById(R.id.but_done);
        but_call=findViewById(R.id.but_call);
        lin_normal=findViewById(R.id.lin_normal);
        lin_emg=findViewById(R.id.lin_emg);
        but_log_out=findViewById(R.id.but_log_out);

        firebaseFirestore=FirebaseFirestore.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        pd=new ProgressDialog(ClientMapsActivity.this);
        pd.setMessage("Waiting For Help");
        lin_normal.setVisibility(View.GONE);
        lin_emg.setVisibility(View.GONE);
        databaseReference.child("PatientLocations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PatientLocation patientLocation=dataSnapshot.getValue(PatientLocation.class);
                String status=patientLocation.getStatus();
                if (status.equals("normal")){
                    lin_normal.setVisibility(View.VISIBLE);
                    lin_emg.setVisibility(View.GONE);
                    try {
                        pd.dismiss();
                    }catch (Exception e){

                    }
                }else if (status.equals("waiting")){
                    lin_normal.setVisibility(View.GONE);
                    lin_emg.setVisibility(View.GONE);
                    try {
                        pd.show();
                    }catch (Exception e){

                    }
                }else if (status.equals("inway")){
                    lin_normal.setVisibility(View.GONE);
                    lin_emg.setVisibility(View.VISIBLE);
                    try {
                        pd.dismiss();
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //pd.show();

        but_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        but_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //Change This Code becouse your app will crash
                startActivity(new Intent(ClientMapsActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
               // finish();
            }
        });

        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Emergencies").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Emergency emergency=snapshot.getValue(Emergency.class);
                            final String emergencyId=snapshot.getKey();
                            if (emergency.getPatient_id().equals(userId)){
                                databaseReference.child("AmbulanceLocations").child(emergency.getAmbulance_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        AmbulanceLocation aLoc=dataSnapshot.getValue(AmbulanceLocation.class);
                                        aLoc.setStatus("free");
                                        databaseReference.child("AmbulanceLocations").child(emergency.getAmbulance_id()).setValue(aLoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    databaseReference.child("Emergencies").child(emergencyId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                databaseReference.child("PatientLocations").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        PatientLocation location=dataSnapshot.getValue(PatientLocation.class);
                                                                        location.setStatus("normal");
                                                                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                                                                        reference.child("PatientLocations").child(userId).setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
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
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        but_firstAids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientMapsActivity.this,FirstAidsActivity.class));
            }
        });

        but_diseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClientMapsActivity.this,DiseasesActivity.class));
            }
        });
        but_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser!=null){
                    assert firebaseUser != null;
                    final String userid = firebaseUser.getUid();
                    makeEmergeny(userid);
                }
            }
        });
    }

    private void makeEmergeny(final String userid) {
        databaseReference.child("PatientLocations").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final PatientLocation patientLocation=dataSnapshot.getValue(PatientLocation.class);
                final LatLng latLng=new LatLng(patientLocation.getLog(),patientLocation.getLot());
                databaseReference.child("AmbulanceLocations").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        String ambulanceId="";
                        double oldDistance=-1;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AmbulanceLocation ambulanceLocation=snapshot.getValue(AmbulanceLocation.class);
                            if (ambulanceLocation.getStatus().equals("free")){// x=1;
                                LatLng alatLng=new LatLng(ambulanceLocation.getLog(),ambulanceLocation.getLot());
                                double newDistance = 0;
                                newDistance=CalculationByDistance(latLng,alatLng);
                                if (oldDistance==-1){
                                    oldDistance=newDistance;
                                    ambulanceId=snapshot.getKey();
                                }
                                if (oldDistance!=-1&&oldDistance>newDistance){
                                    oldDistance=newDistance;
                                    ambulanceId=snapshot.getKey();
                                }

                            }
                        }
                        if (oldDistance!=-1){
                            Emergency emergency=new Emergency(userid,ambulanceId,"waiting");
                         //   FirebaseDatabase database = FirebaseDatabase.getInstance();
                           // String key = database.getReference("Emergencies")..push().getKey();
                            String key=databaseReference.child("Emergencies").push().getKey();
                            databaseReference.child("Emergencies").child(key).setValue(emergency).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       databaseReference.child("PatientLocations").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               PatientLocation patientLoc=dataSnapshot.getValue(PatientLocation.class);
                                               patientLoc.setStatus("waiting");
                                               databaseReference.child("PatientLocations").child(userid).setValue(patientLoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       if (task.isSuccessful()){

                                                           lin_emg.setVisibility(View.GONE);
                                                           lin_normal.setVisibility(View.GONE);

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

                        }else {
                            Toast.makeText(ClientMapsActivity.this, "Sorry There Is No Ambulance available for now!", Toast.LENGTH_SHORT).show();
                        }
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //put first marker for current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            assert firebaseUser != null;
            final String userid = firebaseUser.getUid();
            databaseReference.child("PatientLocations").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear();
                    final PatientLocation location=dataSnapshot.getValue(PatientLocation.class);
                    marker_you=new LatLng(location.getLog(),location.getLot());
                    mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker_you,13));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("PatientLocations").child(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear();
                    final PatientLocation location=dataSnapshot.getValue(PatientLocation.class);
                    marker_you=new LatLng(location.getLog(),location.getLot());
                    mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                 //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker_you,13));
                    if (location.getStatus().equals("inway")){
                        //from request table, the request id is cheked to be matched with the user id
                        databaseReference.child("Emergencies").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Emergency emergency=snapshot.getValue(Emergency.class);
                                    if (emergency.getPatient_id().equals(userid)){
                                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                                        reference.child("AmbulanceLocations").child(emergency.getAmbulance_id()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                AmbulanceLocation loc=dataSnapshot.getValue(AmbulanceLocation.class);
                                                mMap.clear();
                                               // PatientLocation location=dataSnapshot.getValue(PatientLocation.class);
                                                marker_you=new LatLng(location.getLog(),location.getLot());
                                                mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                LatLng marker_amb=new LatLng(loc.getLog(),loc.getLot());
                                                mMap.addMarker(new MarkerOptions().position(marker_amb).title("Ambulance").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final android.location.Location location) {
                // Add a marker in Sydney and move the camera
                //mMap.clear();

               // marker_you=new LatLng(location.getLatitude(),location.getLongitude());
               // mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser!=null){
                    final String uId=firebaseUser.getUid();
                   databaseReference.child("PatientLocations").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           PatientLocation pLoc=dataSnapshot.getValue(PatientLocation.class);
                           pLoc.setLog(location.getLatitude());
                           pLoc.setLot(location.getLongitude());
                           databaseReference.child("PatientLocations").child(uId).setValue(pLoc);
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                android.location.Location lastKnownLocation=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

              /*  mMap.clear();
                if (lastKnownLocation!=null){
                    marker_you=new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser!=null){
                        assert firebaseUser != null;
                        final String userid = firebaseUser.getUid();
                        PatientLocation loc=new PatientLocation(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                        firebaseFirestore.collection("Locations").document(userid).set(loc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){}
                            }
                        });
                    }
                }*/

            }
        }

    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    private void call(){
        final String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("Emergencies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Emergency emergency = snapshot.getValue(Emergency.class);
                    if (emergency.getPatient_id().equals(userId)){
                        firebaseFirestore.collection("Users").document(emergency.getAmbulance_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Person person=task.getResult().toObject(Person.class);
                                    String number =person.getPhone();
                                    if (number.trim().length()>0){
                                        if (ContextCompat.checkSelfPermission(ClientMapsActivity.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                                            ActivityCompat.requestPermissions(ClientMapsActivity.this,
                                                    new String[]{Manifest.permission.CALL_PHONE},REQUSET_CALL);
                                        }else {
                                            String dial="tel:"+number.trim();
                                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                                        }
                                    }

                                }
                            }
                        });
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void normal(){
        lin_emg.setVisibility(View.GONE);
        lin_normal.setVisibility(View.VISIBLE);
    }
}
