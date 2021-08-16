package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.databinding.FragmentPocketBinding;

import java.util.Objects;

public class PacketSizeF extends RootFragment implements View.OnClickListener {

    public static FragmentClickCallback mfFragmentClickCallback;

    RelativeLayout rl_next;

    private View view;

    private FragmentPocketBinding binding;

    private FragmentManager fragmentManager;

    private Context context;

    private DeliveryDetails deliveryDetails;

    private String packetSize = "Small";

    public PacketSizeF() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pocket, container, false);

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

        binding.ivBack.setOnClickListener(this);
        binding.rlNext.setOnClickListener(this);
        binding.lLayoutSmall.setOnClickListener(this);
        binding.lLayoutMedium.setOnClickListener(this);
        binding.lLayoutLarge.setOnClickListener(this);
        binding.lLayoutXlarge.setOnClickListener(this);

        binding.cbIsFragile.setOnCheckedChangeListener((buttonView, isChecked) ->
                deliveryDetails.setFragileParcel(isChecked));

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                fragmentManager.popBackStack();
                break;

            case R.id.rl_next:
                deliveryDetails.setSizeOfPacket(packetSize);
                DeliveryTimeF fragments = new DeliveryTimeF();
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle arguments = new Bundle();
                arguments.putParcelable("deliveryDetails", deliveryDetails);
                fragments.setArguments(arguments);
                ft.replace(R.id.fl_id, fragments, "DeliveryTime_F").addToBackStack("DeliveryTime_F").commit();
                break;

            case R.id.lLayoutSmall:
                goneSelector();
                packetSize = "Small";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.lLayoutSmall.setForeground(context.getDrawable(R.drawable.receiver_selector));
                break;

            case R.id.lLayoutMedium:
                goneSelector();
                packetSize = "Medium";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.lLayoutMedium.setForeground(context.getDrawable(R.drawable.receiver_selector));
                break;

            case R.id.lLayoutLarge:
                goneSelector();
                packetSize = "Large";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.lLayoutLarge.setForeground(context.getDrawable(R.drawable.receiver_selector));
                break;

            case R.id.lLayoutXlarge:
                goneSelector();
                packetSize = "X-Large";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.lLayoutXlarge.setForeground(context.getDrawable(R.drawable.receiver_selector));
                break;
        }
    }

    private void goneSelector() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.lLayoutSmall.setForeground(null);
            binding.lLayoutMedium.setForeground(null);
            binding.lLayoutLarge.setForeground(null);
            binding.lLayoutXlarge.setForeground(null);
        }
    }
}
