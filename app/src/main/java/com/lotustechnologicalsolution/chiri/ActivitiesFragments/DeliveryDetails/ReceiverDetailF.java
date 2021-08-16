package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.PermissionManager;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.PermissionManagerListener;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.databinding.FragmentReceiverBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ReceiverDetailF extends RootFragment
        implements View.OnClickListener, PermissionManagerListener {

    static final int PICK_CONTACT = 1;
    private static final int RequestPermissionCode = 1;
    public static FragmentClickCallback mfFragmentClickCallback;
    private FragmentReceiverBinding binding;
    private Context context;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private PermissionManager permissionManager;

    private DeliveryDetails deliveryDetails;

    public ReceiverDetailF() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receiver, container, false);

        context = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();

        permissionManager = new PermissionManager(getActivity(), this);

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

        binding.rLayoutMe.setOnClickListener(this);
        binding.rLayoutAnotherPerson.setOnClickListener(this);
        binding.ivContact.setOnClickListener(this);

        preferences = new Preferences(getActivity());
        binding.tvNumber.setText(preferences.getKeyUserPhone());

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
                WeightF fragments = new WeightF();
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle arguments = new Bundle();
                arguments.putParcelable("deliveryDetails", deliveryDetails);
                fragments.setArguments(arguments);
                ft.replace(R.id.fl_id, fragments, "Weight_F").addToBackStack("Weight_F").commit();
                break;

            case R.id.rLayoutMe:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.rLayoutAnotherPerson.setForeground(null);
                    binding.rLayoutMe.setForeground(context.getDrawable(R.drawable.receiver_selector));
                }
                break;

            case R.id.rLayoutAnotherPerson:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.rLayoutMe.setForeground(null);
                    binding.rLayoutAnotherPerson
                            .setForeground(context.getDrawable(R.drawable.receiver_selector));
                }
                break;

            case R.id.ivContact:
                requestPermission();
                break;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {

            case PICK_CONTACT:

                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();

                    Cursor c = getActivity().managedQuery(contactData, null, null, null, null);

                    if (c.moveToFirst()) {

                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String phoneNumber = "";
                        String name;

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            phoneNumber = phones.getString(phones.getColumnIndex("data1"));
                            Log.d("TAG", "onActivityResult: " + phoneNumber);
                            binding.tvNumber.setText(phoneNumber);
                        }

                        name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        deliveryDetails.setReceiverContactName(name);
                        deliveryDetails.setReceiverContactNumber(phoneNumber);
                    }
                }
                break;
        }
    }

    public void requestPermission() {
        permissionManager.setSinglePermission(Manifest.permission.READ_CONTACTS);
    }

    @Override
    public void onSinglePermissionGranted(String permission) {
        if (permission.equalsIgnoreCase(Manifest.permission.READ_CONTACTS)) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        } else {
            Toast.makeText(context, "You have denied a contacts permission", Toast.LENGTH_SHORT).show();
            requestPermission();
        }
    }

    @Override
    public void onMultiplePermissionGranted(ArrayList<String> permissions) {
    }
}
