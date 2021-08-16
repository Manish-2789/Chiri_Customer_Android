package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.lotustechnologicalsolution.R;

import java.util.Objects;

public class FinalDeliveryA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_delivery);

        FinalDeliveryF fragments = new FinalDeliveryF();
        FragmentTransaction ft = Objects.requireNonNull(FinalDeliveryA.this).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.add(R.id.fl_id, fragments, "FinalDeliveryF").addToBackStack("FinalDeliveryF").commit();
    }
}