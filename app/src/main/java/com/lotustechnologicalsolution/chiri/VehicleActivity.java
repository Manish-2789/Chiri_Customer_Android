package com.lotustechnologicalsolution.chiri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.AddVehicleActivity;

public class VehicleActivity extends AppCompatActivity {
    private ImageView ivBackArrow;
    private LinearLayout llVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        ivBackArrow = findViewById(R.id.iv_back_arrow);
        llVehicle = findViewById(R.id.ll_add_vehicle);
        llVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleActivity.this,AddVehicleActivity.class);
                startActivity(intent);
            }
        });
    }
}