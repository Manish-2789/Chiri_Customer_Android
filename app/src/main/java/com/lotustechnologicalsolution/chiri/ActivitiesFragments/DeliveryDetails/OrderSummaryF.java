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

import com.bumptech.glide.Glide;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.databinding.FragmentCheckoutBinding;

import java.util.Objects;

public class OrderSummaryF extends RootFragment implements View.OnClickListener {

    private Context context;
    private FragmentManager fragmentManager;
    private DeliveryDetails deliveryDetails;

    private FragmentCheckoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false);

        context = getActivity();

        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        Bundle bundle = getArguments();
        if (bundle != null) {
            deliveryDetails = bundle.getParcelable("deliveryDetails");
        }

        if (deliveryDetails == null) {
            Toast.makeText(context, "Enter required details.", Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStack();
        } else {
            setDataToView();
        }

        binding.ivBack.setOnClickListener(this);

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView() {

        if (deliveryDetails.getParcelImagePath() != null && !deliveryDetails.getParcelImagePath().equalsIgnoreCase(""))
            Glide.with(Objects.requireNonNull(getActivity())).load(deliveryDetails.getParcelImagePath()).into(binding.imgPacket);

        binding.tvPickup.setText(deliveryDetails.getFromAddress());
        binding.tvDropUp.setText(deliveryDetails.getToAddress());
        binding.tvDateTime.setText(deliveryDetails.getDeliveryTime());
        binding.tvWeight.setText(deliveryDetails.getWeight() + " Kg.");
        binding.tvPocketSize.setText(deliveryDetails.getSizeOfPacket());
        binding.tvOrderDis.setText(deliveryDetails.getOrderDescription());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                fragmentManager.popBackStack();
                break;

        }
    }
}
