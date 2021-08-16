package com.lotustechnologicalsolution;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.CountryAndGoodsF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignUp.RegisterPhoneNoF;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.RequestRegisterUserModel;

import java.util.Locale;


public class fragment_get_started extends Fragment implements View.OnClickListener {
    //
    View view;
    RequestRegisterUserModel model;
    private EditText tvCountry;
    private String deviceToken, countryId = "", countryCode = "", countryIos = "";
    Preferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_get_started, container, false);
        model = new RequestRegisterUserModel();

        //        activity level nightmode apply
        preferences = new Preferences(getActivity());
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setLocale(preferences.getKeyLocale());

        tvCountry = view.findViewById(R.id.tv_country);
        tvCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                METHOD_openTypes_of_Goods_F();

            }
        });
        METHOD_init_views();


        return view;
    }


    private void METHOD_openTypes_of_Goods_F() {
        //ClearFocusAllTextField();
        CountryAndGoodsF f = new CountryAndGoodsF(true, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                if (bundle != null) {
                    tvCountry.setText(bundle.getString("selected_country"));
                    countryId = bundle.getString("selected_country_id");
                    countryCode = bundle.getString("selected_country_code");
                    countryIos = bundle.getString("selected_country_ios");

                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_id, f).addToBackStack(null).commit();
    }

    private void METHOD_init_views() {
        view.findViewById(R.id.tv_get_started).setOnClickListener(this);
    }


    //        activity level locale apply
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
        //this.recreate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_started: {
                /*if(TextUtils.isEmpty(tvCountry.getText().toString()))
                {
                    tvCountry.setError(""+getResources().getString(R.string.cant_empty));
                    return;
                }*/

                model.setCountryId("" + countryId);
                model.setCountryCode("" + countryCode);
                model.setCountryIos("" + countryIos);
                model.setDeviceToken("" + deviceToken);


//                DateAndTimePicker frg_ment = new DateAndTimePicker();
//                FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
//                Bundle args = new Bundle();
//                args.putSerializable("UserData",model);
//                frg_ment.setArguments(args);
//                transaction.addToBackStack("RegisterPhoneNo_A");
//                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//                transaction.replace(R.id.fl_id, frg_ment,"RegisterPhoneNo_A").commit();


                RegisterPhoneNoF frg_ment = new RegisterPhoneNoF();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putSerializable("UserData", model);
                frg_ment.setArguments(args);
                transaction.addToBackStack("RegisterPhoneNo_A");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_id, frg_ment, "RegisterPhoneNo_A").commit();

            }
            break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //  getFragmentManager().beginTransaction().remove((Fragment) fragment_get_started).commitAllowingStateLoss();
    }
}