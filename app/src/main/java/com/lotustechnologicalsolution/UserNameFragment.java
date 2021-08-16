package com.lotustechnologicalsolution;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.ModelClasses.RequestRegisterUserModel;


public class UserNameFragment extends Fragment implements View.OnClickListener {

    View view;
    RequestRegisterUserModel model;
    EditText etUserName, etUserNameFirst;
    Preferences preferences;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_name, container, false);
        InitControl();
        ActionControl();
        //Functions.showKeyboard(getActivity());
        return view;
    }

    private void InitControl() {
        preferences = new Preferences(view.getContext());
        //preferences.setKeyIsLogin(true);
        model = (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        etUserName = view.findViewById(R.id.et_user_name);
        etUserNameFirst = view.findViewById(R.id.et_user_name_first);

    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.clickless).setOnClickListener(this);
        view.findViewById(R.id.btn_submit_registration).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_submit_registration:
                Functions.HideSoftKeyboard(getActivity());
            {
                if (TextUtils.isEmpty(etUserName.getText().toString())) {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.cant_empty));
                    return;
                }
                if (etUserName.getText().toString().contains(" ")) {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.spaces_not_allow));
                    return;
                }

                //**********************added********************************

                if (TextUtils.isEmpty(etUserNameFirst.getText().toString())) {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.cant_empty));
                    return;
                }
                if (etUserNameFirst.getText().toString().contains(" ")) {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.spaces_not_allow));
                    return;
                }

//                Call_RegisterUserApi();
                preferences.setKeyUserFirstName(etUserNameFirst.getText().toString());
                preferences.setKeyUserLastName(etUserName.getText().toString());
                WhatIsYourEmail fragment = new WhatIsYourEmail();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("WhatsIsYourEmail");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_id, fragment, "WhatsIsYourEmail").commit();


            }
            break;
            default:
                break;
        }
    }
}