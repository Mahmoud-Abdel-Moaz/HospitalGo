package com.mahmoud.hospitalgo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.mahmoud.hospitalgo.R;

public class DiseaseDetailsActivity extends AppCompatActivity {

    TextView txt_name,txt_info;

    Intent intent;

    String name,info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        txt_name=findViewById(R.id.txt_name);
        txt_info=findViewById(R.id.txt_info);

        intent=getIntent();

        name=intent.getStringExtra("name");
        info=intent.getStringExtra("info");

        txt_info.setText(info);
        txt_name.setText(name);
    }
}
