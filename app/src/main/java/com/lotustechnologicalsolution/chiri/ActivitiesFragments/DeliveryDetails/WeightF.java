package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.databinding.FragmentWeightBinding;

import java.util.Objects;

public class WeightF extends RootFragment implements View.OnClickListener {

    public static FragmentClickCallback mfFragmentClickCallback;
    private Context context;
    private DeliveryDetails deliveryDetails;
    private FragmentManager fragmentManager;
    private FragmentWeightBinding binding;
    private int selectedWeight;

    public WeightF() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weight, container, false);
        context = getActivity();

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        Bundle bundle = getArguments();
        if (bundle != null) {
            deliveryDetails = bundle.getParcelable("deliveryDetails");
        }

        if (deliveryDetails == null) {
            Toast.makeText(context, "Enter required details.", Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStack();
        }

        binding.rlNext.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);

        binding.rulerPicker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                selectedWeight = selectedValue;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onIntermediateValueChange(int selectedValue) {
                binding.tvWeightValue.setText("Weight : " + selectedValue + "kg");
            }
        });

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_next:
                deliveryDetails.setWeight(selectedWeight);
                PacketSizeF fragments = new PacketSizeF();
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle arguments = new Bundle();
                arguments.putParcelable("deliveryDetails", deliveryDetails);
                fragments.setArguments(arguments);
                ft.replace(R.id.fl_id, fragments, "PacketSize_F").addToBackStack("PacketSize_F").commit();
                break;

            case R.id.iv_back:
                fragmentManager.popBackStack();
                break;
        }
    }
}
