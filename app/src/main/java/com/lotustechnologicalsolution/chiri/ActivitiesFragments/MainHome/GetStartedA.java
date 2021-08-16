package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.lotustechnologicalsolution.chiri.ActivitiesFragments.Setting.NoInternetA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignIn.LoginF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignUp.RegisterPhoneNoF;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.InternetCheckCallback;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ModelClasses.RequestRegisterUserModel;

import java.util.Locale;

public class GetStartedA extends AppCompatActivity {

////    final String socialId = "socialId";
////    final String SocialType = "socialType";
////    final String firstName = "fname";
////    final String lastName = "lname";
////    final String email = "email";
////    final String image = "image";
////    final String authToken = "authToken";
////    final String deviceToken = "deviceToken";
////    final String password = "password";
////    final String passwordd = "passwordd";
////    final String countryId = "countryId";
////    final String roleUser = "roleUser";
////    final String countryCode = "countryCode";
////    final String countryIos = "countryIos";
////    RequestRegisterUserModel model;
////    private EditText tvCountry;
//
//    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);
//        model=new RequestRegisterUserModel();
//
////        activity level nightmode apply
//        preferences=new Preferences(GetStartedA.this);
//        if (preferences.getKeyIsNightMode())
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        else
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//
//
//        setLocale(preferences.getKeyLocale());
//
//        tvCountry = findViewById(R.id.tv_country);
//        tvCountry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        METHOD_init_views();
//
//

    }


    
//
////        activity level locale apply
//    public void setLocale(String lang) {
//        Locale myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = new Configuration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//        onConfigurationChanged(conf);
//        //this.recreate();
//    }
//
//    private void METHOD_init_views(){
//        findViewById(R.id.tv_get_started).setOnClickListener(this);
//    }
//
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.tv_get_started:
//            {
//                model.setCountryId( ""+ countryId);
//                model.setCountryCode(""+ countryCode);
//                model.setCountryIos(""+ countryIos);
//                model.setDeviceToken(""+ deviceToken);
//                RegisterPhoneNoF frg_ment = new RegisterPhoneNoF();
//                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
//                Bundle args = new Bundle();
//                args.putSerializable("UserData",model);
//                frg_ment.setArguments(args);
//                transaction.addToBackStack("RegisterPhoneNo_A");
//                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//                transaction.replace(R.id.fl_id, frg_ment,"RegisterPhoneNo_A").commit();
//
////                LoginF frg_ment = new LoginF();
////                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
////                Bundle args = new Bundle();
////                frg_ment.setArguments(args);
////                transaction.addToBackStack("Login");
////                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
////                transaction.replace(R.id.fl_id, frg_ment,"Login").commit();
//            }
//            break;
//        }
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Functions.RegisterConnectivity(this, new InternetCheckCallback() {
//            @Override
//            public void GetResponse(String requestType, String response) {
//                if(response.equalsIgnoreCase("disconnected")) {
//                    startActivity(new Intent(GetStartedA.this, NoInternetA.class));
//                    overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Functions.unRegisterConnectivity(GetStartedA.this);
//    }

}
