package com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignUp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.lotustechnologicalsolution.UserNameFragment;
import com.lotustechnologicalsolution.WelcomeFragment;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.MainA;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.ModelClasses.RequestRegisterUserModel;
import com.lotustechnologicalsolution.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterPhoneVerificationF extends RootFragment implements View.OnClickListener  {

    private PinView etCode;
    TextView txtSubTitle,tv_timer,tv_resend_code;
    private View view;
    RequestRegisterUserModel model;
    Preferences preferences;

    public RegisterPhoneVerificationF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_register_phone_verification, container, false);
        METHOD_init_views();
        //Functions.showKeyboard(getActivity());

        return view;
    }


    private void METHOD_init_views(){
        preferences=new Preferences(view.getContext());
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");

        txtSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);
        tv_timer = view.findViewById(R.id.tv_timer);
        tv_resend_code = view.findViewById(R.id.tv_resend_code);

        view.findViewById(R.id.iv_back).setOnClickListener(this);

        view.findViewById(R.id.btn_verify_code).setOnClickListener(this);

        tv_resend_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallApi_ResendCode(model.getPhoneNumber());
            }
        });

        SetupScreenData();

        startCountdownTimer();
    }

    private void startCountdownTimer()
    {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_timer.setText("Resend confirmation code (0:" + (millisUntilFinished*2) / 1000+")");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                //mTextField.setText("done!");
                tv_timer.setVisibility(View.GONE);
                tv_resend_code.setVisibility(View.VISIBLE);
            }

        }.start();
    }

    private void SetupScreenData() {
        txtSubTitle.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve)+" "+model.getPhoneNumber());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.btn_verify_code:
                Functions.HideSoftKeyboard(getActivity());
                if(TextUtils.isEmpty(etCode.getText().toString()))
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.enter_verification_code));
                    return;
                }
                if(etCode.getText().toString().length()!=4)
                {
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.incomplete_verification_code));
                    return;
                }
                CallApi_verifyforgotPasswordCode(etCode.getText().toString());
                break;
            default:
                break;
        }
    }

    private void ShowRegisterAuth() {

        UserNameFragment frg_ment = new UserNameFragment();
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("UserData",model);
        frg_ment.setArguments(args);
        transaction.addToBackStack("Register_DOB_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.auth_fl_id, frg_ment,"Register_DOB_F").commit();


//        RegisterDOBF frg_ment = new RegisterDOBF();
//        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
//        Bundle args = new Bundle();
//        args.putSerializable("UserData",model);
//        frg_ment.setArguments(args);
//        transaction.addToBackStack("Register_DOB_F");
//        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//        transaction.replace(R.id.auth_fl_id, frg_ment,"Register_DOB_F").commit();
    }

    private void CallApi_verifyforgotPasswordCode(String code) {
        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("phone", model.getPhoneNumber());
            sendobj.put("verify", "1");
            sendobj.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(),false,false);
        ApiRequest.CallApi(getContext(), ApiUrl.verifyRegisterphoneAuthcode, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            etCode.setText("");
                            preferences.setKeyIsLogin(true);
                            preferences.setKeyUserId(respobj.getString("user_id"));
                            if (respobj.getString("msg").equalsIgnoreCase("This phone has already been registered")){
                                /*WelcomeFragment fragment = new WelcomeFragment();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.addToBackStack("Welcome");
                                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                                transaction.replace(R.id.fl_id, fragment, "Welcome").commit();*/
                                startActivity(new Intent(getActivity(), MainA.class));
                                getActivity().finish();

                                //ShowRegisterAuth();
                            }else {
                                ShowRegisterAuth();
                            }

                        }else {
                            Functions.Show_Alert(view.getContext(),""+view.getContext().getString(R.string.verification_status),""+respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void CallApi_ResendCode(String phoneNo) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("phone", phoneNo);
            sendobj.put("verify", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getContext(), false, false);
        ApiRequest.CallApi(getContext(), ApiUrl.verifyRegisterphoneAuthcode, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            //ShowRegisterAuth();
                            tv_resend_code.setVisibility(View.GONE);
                            tv_timer.setVisibility(View.VISIBLE);
                            startCountdownTimer();
                        } else {
                            Functions.ShowToast(view.getContext(), "" + respobj.optString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }

}