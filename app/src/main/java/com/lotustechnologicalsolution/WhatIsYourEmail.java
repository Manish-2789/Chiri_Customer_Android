package com.lotustechnologicalsolution;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.MainA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.PaymentMethod.AddPaymentCardF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignUp.RegisterPhoneNoF;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.ModelClasses.RequestRegisterUserModel;

import org.json.JSONException;
import org.json.JSONObject;


public class WhatIsYourEmail extends Fragment implements View.OnClickListener {
    View view;
    LinearLayout btn_email_confirm;
    RequestRegisterUserModel model;
    EditText et_email;
    Preferences preferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view =  inflater.inflate(R.layout.fragment_what_is_your_email, container, false);

       preferences = new Preferences(view.getContext());
       model = new RequestRegisterUserModel();
       et_email = view.findViewById(R.id.et_email);
       btn_email_confirm = view.findViewById(R.id.btn_email_confirm);
       view.findViewById(R.id.iv_back).setOnClickListener(this);
       btn_email_confirm.setOnClickListener(this);

        //Functions.showKeyboard(getActivity());
       return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_email_confirm:
                Functions.HideSoftKeyboard(getActivity());
                model.setEmail( et_email.getText().toString());
                preferences.setKeyUserEmail(et_email.getText().toString());
                CallApi_SendUserDetail();

                break;
            default:
                break;
        }


    }

    private void CallApi_SendUserDetail() {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", "" + preferences.getKeyUserId());
            sendobj.put("username", preferences.getKeyUserFirstName());
            sendobj.put("first_name", preferences.getKeyUserFirstName());
            sendobj.put("last_name", preferences.getKeyUserLastName());
            sendobj.put("email", et_email.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.CallApi(getContext(), ApiUrl.saveUserInfo, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            AddPaymentCardF frg_ment = new AddPaymentCardF();
                            FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
                            Bundle args = new Bundle();
                            args.putSerializable("UserData",model);
                            frg_ment.setArguments(args);
                            transaction.addToBackStack("RegisterPhoneNo_A");
                            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                            transaction.replace(R.id.fl_id, frg_ment,"RegisterPhoneNo_A").commit();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}