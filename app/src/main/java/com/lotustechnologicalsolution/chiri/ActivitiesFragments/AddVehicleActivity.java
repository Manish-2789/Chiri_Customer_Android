package com.lotustechnologicalsolution.chiri.ActivitiesFragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.VehicleAdapter;

public class AddVehicleActivity extends AppCompatActivity {
    private ImageView ivCamera;
    private ImageView ivArrow;
    private TextView tvYear;
    private TextView tvDeliveryTime;
    private EditText etLicence;
    private EditText etColor;
    private EditText etModel;
    private EditText etMark;
    private TextView tvUploadPhoto;
    private TextView tvVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        ivCamera = findViewById(R.id.iv_camera);
        ivArrow = findViewById(R.id.iv_arrow);
        tvYear = findViewById(R.id.tv_year);
        tvUploadPhoto = findViewById(R.id.tv_upload_photo);
        tvDeliveryTime = findViewById(R.id.rvDeliveryType);
        etLicence = findViewById(R.id.et_licence);
        etColor = findViewById(R.id.et_color);
        etModel = findViewById(R.id.et_model);
        etMark = findViewById(R.id.et_mark);
        tvVehicle = findViewById(R.id.tv_vehicle);
        tvVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddVehicleActivity.this, VehicleAdapter.class);
                finish();
            }
        });
        }
    private boolean validate() {
        boolean value = false;
        if (etLicence.getText().toString().equals(""))
        {
            etLicence.setError("please enter Licence plates");
        }
        else if (etColor.getText().toString().equals(""))
        {
            etColor.setError("please enter colours");
        }
        else if (etMark.getText().toString().equals(""))
        {
            etMark.setError("please enter marks");
        }
        else if (etModel.getText().toString().equals(""))
        {
            etModel.setError("please enter model number");
        }
        else {
            value = true;
        }
        return value;
    }

    }