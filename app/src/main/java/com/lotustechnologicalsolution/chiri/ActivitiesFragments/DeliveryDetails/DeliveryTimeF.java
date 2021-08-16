package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.MyTimePicker;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.databinding.FragmentDeliveryTimeBinding;

import java.util.Objects;

public class DeliveryTimeF extends RootFragment
        implements View.OnClickListener {

    private FragmentDeliveryTimeBinding binding;
    private Context context;

    private FragmentManager fragmentManager;
    private DeliveryDetails deliveryDetails;

    private DeliveryMode deliveryMode;

    public DeliveryTimeF() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delivery_time, container, false);

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
        binding.rlCheckout.setOnClickListener(this);
        binding.lLayoutExpress.setOnClickListener(this);
        binding.lLayoutLine.setOnClickListener(this);
        binding.lLayoutCustom.setOnClickListener(this);
        binding.tvToday.setOnClickListener(this);

        new MyTimePicker(binding.tvTime);

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                fragmentManager.popBackStack();
                break;

            case R.id.rl_checkout:
                if (deliveryMode == DeliveryMode.MODE_CUSTOM)
                    deliveryDetails.setDeliveryTime(binding.tvToday.getText().toString() + " at " + binding.tvTime.getText().toString());
                else if (deliveryMode == DeliveryMode.MODE_LINE)
                    deliveryDetails.setDeliveryTime(binding.etLineDescription.getText().toString());
                else
                    deliveryDetails.setDeliveryTime("Express");
                OrderSummaryF fragments = new OrderSummaryF();
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle arguments = new Bundle();
                arguments.putParcelable("deliveryDetails", deliveryDetails);
                fragments.setArguments(arguments);
                ft.replace(R.id.fl_id, fragments, "OrderSummary_F").addToBackStack("OrderSummary_F").commit();
                break;

            case R.id.lLayoutExpress:
                clearSelector();
                deliveryMode = DeliveryMode.MODE_EXPRESS;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    binding.lLayoutExpress.setForeground(context.getDrawable(R.drawable.receiver_selector));
                break;

            case R.id.lLayoutLine:
                clearSelector();
                deliveryMode = DeliveryMode.MODE_LINE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.lLayoutLine.setForeground(context.getDrawable(R.drawable.receiver_selector));
                    binding.rLayoutLine.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.lLayoutCustom:
                clearSelector();
                deliveryMode = DeliveryMode.MODE_CUSTOM;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.lLayoutCustom.setForeground(context.getDrawable(R.drawable.receiver_selector));
                    binding.rLayoutCustom.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.tvToday:
                PopupMenu menu = new PopupMenu(getActivity(), v);
                menu.getMenu().add("Today"); // menus items
                menu.getMenu().add("Tomorrow"); // menus items
                menu.getMenu().add("Day after tomorrow"); // menus items
                menu.show();
                menu.setOnMenuItemClickListener(item -> {
                    binding.tvToday.setText(item.getTitle().toString());
                    return false;
                });
                break;
        }
    }

    private void clearSelector() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.lLayoutExpress.setForeground(null);
            binding.lLayoutLine.setForeground(null);
            binding.lLayoutCustom.setForeground(null);

            binding.rLayoutLine.setVisibility(View.GONE);
            binding.rLayoutCustom.setVisibility(View.GONE);
        }
    }

    private enum DeliveryMode {
        MODE_EXPRESS,
        MODE_LINE,
        MODE_CUSTOM
    }
}
