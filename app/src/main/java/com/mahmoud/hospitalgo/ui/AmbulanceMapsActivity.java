package com.mahmoud.hospitalgo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahmoud.hospitalgo.R;
import com.mahmoud.hospitalgo.pojo.AmbulanceLocation;
import com.mahmoud.hospitalgo.pojo.Emergency;
import com.mahmoud.hospitalgo.pojo.PatientLocation;
import com.mahmoud.hospitalgo.pojo.Person;

import java.util.ArrayList;
import java.util.List;

public class AmbulanceMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;

    private FloatingActionButton but_location;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng marker_you;
    Button but_log_out;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    List<Marker> markerList = new ArrayList<Marker>();
    boolean busy;
    DatabaseReference databaseReference;

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                //Change This Code becouse your app will crash
                startActivity(new Intent(AmbulanceMapsActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        busy=false;
        but_log_out=findViewById(R.id.but_log_out);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Ambulance");

        //toolbar.inflateMenu(R.menu.menu);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");

        but_location=findViewById(R.id.but_location);
        firebaseFirestore=FirebaseFirestore.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        but_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker_you!=null){
                    CameraPosition cp = CameraPosition.builder().target(marker_you).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 2000, null);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker_you,15));
                }
            }
        });
        but_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //Change This Code becouse your app will crash
                startActivity(new Intent(AmbulanceMapsActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                // finish();
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
        googleMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            assert firebaseUser != null;
            final String userid = firebaseUser.getUid();
            databaseReference.child("AmbulanceLocations").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear();
                    markerList.clear();
                    AmbulanceLocation ambulanceLocation=dataSnapshot.getValue(AmbulanceLocation.class);
                    marker_you=new LatLng(ambulanceLocation.getLog(),ambulanceLocation.getLot());
                    Marker marker =mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    markerList.add(marker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker_you,13));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference.child("AmbulanceLocations").child(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear();
                    markerList.clear();
                    markerList=new ArrayList<>();
                    final AmbulanceLocation ambulanceLocation=dataSnapshot.getValue(AmbulanceLocation.class);
                    marker_you=new LatLng(ambulanceLocation.getLog(),ambulanceLocation.getLot());
                    Marker marker =mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    markerList.add(marker);
                 //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker_you,13));
                   // boolean busy;
                    busy=ambulanceLocation.getStatus().equals("busy");

                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                    reference.child("Emergencies").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mMap.clear();
                            markerList.clear();
                            markerList=new ArrayList<>();
                            marker_you=new LatLng(ambulanceLocation.getLog(),ambulanceLocation.getLot());
                            Marker marker =mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            markerList.add(marker);
                            List<Emergency> emergencies=new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Emergency emergency=snapshot.getValue(Emergency.class);
                                DatabaseReference reference1=FirebaseDatabase.getInstance().getReference().child("PatientLocations");
                                if (emergency.getAmbulance_id().equals(userid)){
                                    if (busy&&emergency.getStatus().equals("accepted")){
                                        reference1.child(emergency.getPatient_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              //  PatientLocation pLoc=dataSnapshot.getValue(PatientLocation.class);
                                              //  LatLng marker_pit=new LatLng(pLoc.getLog(),pLoc.getLot());
                                               // Marker marker1 =mMap.addMarker(new MarkerOptions().position(marker_pit).title("Patient").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                                //markerList.add(marker1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        break;
                                    }else if (!busy){
                                        reference1.child(emergency.getPatient_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                PatientLocation pLoc=dataSnapshot.getValue(PatientLocation.class);
                                                //Toast.makeText(AmbulanceMapsActivity.this, busy+" "+pLoc.getLog(), Toast.LENGTH_SHORT).show();
                                                LatLng marker_pit=new LatLng(pLoc.getLog(),pLoc.getLot());
                                                Marker marker1 =mMap.addMarker(new MarkerOptions().position(marker_pit).title("Patient").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                markerList.add(marker1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final android.location.Location location) {
                // Add a marker in Sydney and move the camera
                mMap.clear();
                markerList.clear();
                markerList=new ArrayList<>();
                if (marker_you==null){
                    marker_you=new LatLng(location.getLatitude(),location.getLongitude());
                }
                mMap.addMarker(new MarkerOptions().position(marker_you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser!=null){
                    assert firebaseUser != null;
                    final String userid = firebaseUser.getUid();
                    databaseReference.child("AmbulanceLocations").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            AmbulanceLocation loc=dataSnapshot.getValue(AmbulanceLocation.class);
                            loc.setLog(location.getLatitude());
                            loc.setLot(location.getLongitude());
                            databaseReference.child("AmbulanceLocations").child(userid).setValue(loc);
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

                // mMap.clear();
                //  LatLng userlocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                // mMap.addMarker(new MarkerOptions().position(userlocation).title("Your PatientLocation"));
                //  mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocation));
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        int markerId = -1;

        String str = marker.getId();
        for(int i=0; i<markerList.size(); i++) {
            markerId = i;
            Marker m = markerList.get(i);
            if(m.getId().equals(marker.getId()))
                break;
        }

        final int ind =markerId-1;
       // Toast.makeText(this, "ind="+ind+"markerId"+markerId, Toast.LENGTH_SHORT).show();

        if (ind > -1&&!busy) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser!=null){
                assert firebaseUser != null;
                final String userId = firebaseUser.getUid();

                databaseReference.child("Emergencies").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent=new Intent(AmbulanceMapsActivity.this,PatientPopupActivity.class);
                        List<String> al=new ArrayList<String>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Emergency emergency=snapshot.getValue(Emergency.class);
                            if (emergency.getAmbulance_id().equals(userId)){
                                al.add(snapshot.getKey());
                            }
                        }
                        intent.putExtra("emergency_id",al.get(ind));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        //    startActivity(intent);
        }

        return false;
    }

}
