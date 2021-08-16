package com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignUp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.SplashScreenA;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.ModelClasses.RequestRegisterUserModel;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.ObjIntConsumer;

public class RegisterPhoneNoF extends RootFragment implements View.OnClickListener {

    RequestRegisterUserModel model;
    View view;
    EditText etCountryCode, etPhoneNumber;
    CountryCodePicker ccp;
    private EditText tvCountry;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_phone_number, container, false);

        InitControl();
        ActionControl();
        //Functions.showKeyboard(getActivity());

        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.register_no_continue).setOnClickListener(this);

//        tvCountry = view.findViewById(R.id.tv_country);
//        tvCountry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Functions.HideSoftKeyboard(getActivity());
//                METHOD_openTypes_of_Goods_F();
//            }
//        });
    }

    private void InitControl() {
        model = (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        etCountryCode = view.findViewById(R.id.et_country_code);
        etPhoneNumber = view.findViewById(R.id.et_phoneno);
        ccp = view.findViewById(R.id.ccp);



        ccp.registerPhoneNumberTextView(etPhoneNumber);
        view.findViewById(R.id.iv_back).setOnClickListener(this);

        SetupScreenData();
    }

    private void SetupScreenData() {
//        if (!model.getSocialType().equalsIgnoreCase("email"))
//        {
//            etCountryCode.setOnClickListener(this);
//        }
//        else
//        {
//            etCountryCode.setText(""+model.getCountryCode());
//            ccp.setDefaultCountryUsingNameCode(""+model.getCountryIos());
//            ccp.setCountryForNameCode(""+model.getCountryIos());
//        }

        etCountryCode.setText("" + "+91");
        //etCountryCode.setText(""+model.getCountryCode());
        ccp.setDefaultCountryUsingNameCode("" + model.getCountryIos());
        ccp.setCountryForNameCode("" + model.getCountryIos());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
            case R.id.register_no_continue:
                Functions.HideSoftKeyboard(getActivity());
            {
                if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                    etPhoneNumber.setError("" + getResources().getString(R.string.cant_empty));
                    return;
                }
                if (!ccp.isValid()) {
                    etPhoneNumber.setError("" + getResources().getString(R.string.invalid_phone_no));
                    return;
                }

                String phoneNo = etPhoneNumber.getText().toString();
                if (phoneNo.charAt(0) == '0') {
                    phoneNo = phoneNo.substring(1);
                }
                phoneNo = phoneNo.replace("+", "");
                //phoneNo = phoneNo.replace(ccp.getSelectedCountryCode(), "");
                phoneNo = ccp.getSelectedCountryCodeWithPlus() + phoneNo;
                phoneNo = phoneNo.replace(" ", "");
                phoneNo = phoneNo.replace("(", "");
                phoneNo = phoneNo.replace(")", "");
                phoneNo = phoneNo.replace("-", "");
                model.setPhoneNumber("" + phoneNo);
                CallApi_verifyforgotPasswordCode(phoneNo);

            }
            break;
//            case R.id.et_country_code:
//            {
//                Functions.HideSoftKeyboard(getActivity());
//                METHOD_openTypes_of_Goods_F();
//            }
//            break;
            default:
                break;
        }
    }

    private void ShowRegisterAuth() {
        RegisterPhoneVerificationF fragment = new RegisterPhoneVerificationF();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("UserData", model);
        fragment.setArguments(args);
        transaction.addToBackStack("Register_Authentication_F");
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.replace(R.id.fl_id, fragment, "Register_Authentication_F").commit();
    }

    private void CallApi_verifyforgotPasswordCode(String phoneNo) {
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
                            ShowRegisterAuth();
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

//
//    private void METHOD_openTypes_of_Goods_F() {
////        ClearFocusAllTextField();
//        CountryAndGoodsF f = new CountryAndGoodsF(true, new FragmentClickCallback() {
//            @Override
//            public void OnItemClick(int postion, Bundle bundle) {
//                if (bundle!=null){
//                    tvCountry.setText(bundle.getString("selected_country"));
//                    String countryId = bundle.getString("selected_country_id");
//                     String countryCode = bundle.getString("selected_country_code");
//                     String countryIos = bundle.getString("selected_country_ios");
//
//
//
//                }
//            }
//        });
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//        ft.replace(R.id.reg_fl_id, f).addToBackStack(null).commit();
//    }


//    private void METHOD_openTypes_of_Goods_F() {
//        CountryAndGoodsF f = new CountryAndGoodsF(true, new FragmentClickCallback() {
//            @Override
//            public void OnItemClick(int postion, Bundle bundle) {
//                if (bundle!=null){
//                    tvCountry.setText(bundle.getString("selected_country"));
//                    String countryCode = bundle.getString("selected_country_code");
//                    String countryId = bundle.getString("selected_country_id");
//                    String countryIos = bundle.getString("selected_country_ios");
//
////                    new Handler().postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////
////                            model.setCountryId(" "+countryId);
////                            model.setCountryCode(""+countryCode);
////                            model.setCountryIos(""+countryIos);
////                            //etCountryCode.setText(""+countryCode);
////                            ccp.setDefaultCountryUsingNameCode(""+countryIos);
////                            ccp.setCountryForNameCode(""+countryIos);
////                        }
////                    },200);
//                }
//            }
//        });
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//        ft.replace(R.id.fl_id, f).addToBackStack(null).commit();
//    }


    @Override
    public void onDetach() {
        Functions.HideSoftKeyboard(getActivity());
        super.onDetach();
    }
}