package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile.ProfileF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile.ProfileFragment;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderHistory.HistoryF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderTracking.OrderTrackingA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.Setting.NoInternetA;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.Interfaces.InternetCheckCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.lotustechnologicalsolution.chiri.HelpingClasses.Variables.REQUEST_CODE_GPS_ENABLE;

public class MainA extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    long mBackPressed = 0;
    Preferences preferences;
    DatabaseReference rootref;
    BottomNavigationView bottomNavigationView;
    private MainF f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(MainA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale("" + preferences.getKeyLocale());

        setContentView(R.layout.activity_main);

        // For FileURIExposedException Handling
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // To Prevent NetworkOnMainThread Exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        //*************set delivery selected*****************

        bottomNavigationView.setSelectedItemId(R.id.delivery);


        SendDeviceData();
        SendCurrencyGetAPi();

//        init fressco image loader
        Fresco.initialize(this);

        Variables.downloadSharedPreferences = getSharedPreferences(Variables.downloadPref, MODE_PRIVATE);
        rootref = FirebaseDatabase.getInstance().getReference();

        if (getIntent().getExtras() != null) {
            //Get the notification data information when user click on Notification &
            //App also in background


            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getIntent().hasExtra("type") && getIntent().hasExtra("order_id") && getIntent().hasExtra("rider_id")) {

                        if (getIntent().getStringExtra("order_id") != null && !(getIntent().getStringExtra("order_id").equalsIgnoreCase("null"))
                                && getIntent().getStringExtra("rider_id") != null && !(getIntent().getStringExtra("rider_id").equalsIgnoreCase("null"))
                                && getIntent().getStringExtra("type") != null && !(getIntent().getStringExtra("type").equalsIgnoreCase("null"))) {
                            Intent intent = new Intent(MainA.this, OrderTrackingA.class);
                            intent.putExtra("order_id", "" + getIntent().getStringExtra("order_id"));
                            intent.putExtra("rider_id", "" + getIntent().getStringExtra("rider_id"));
                            intent.putExtra("type", "" + getIntent().getStringExtra("type"));
                            startActivity(intent);
                        }

                    }
                }
            }, 200);
        }

        f = new MainF();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.fl_id, f, "Main_F").commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);


    }


    private void SendCurrencyGetAPi() {
        ApiRequest.CallApi(MainA.this, ApiUrl.showDefaultCurrency, new JSONObject(), new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        preferences.setKeyCurrencyName(respobj.getJSONObject("msg").getJSONObject("Country").optString("currency_code"));
                        preferences.setKeyCurrencySymbol(respobj.getJSONObject("msg").getJSONObject("Country").optString("currency_symbol"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Functions.RegisterConnectivity(this, new InternetCheckCallback() {
            @Override
            public void GetResponse(String requestType, String response) {
                if (response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(MainA.this, NoInternetA.class));
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Functions.unRegisterConnectivity(MainA.this);
    }

    private void SendDeviceData() {
        CallApi_GetIpTypes();
    }

    private void CallApi_GetIpTypes() {

        ApiRequest.CallApi(MainA.this, ApiUrl.apiForIP, new JSONObject(), new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        CallApi_SendDeviceData("" + respobj.optString("ip"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, Request.Method.GET);
    }

    private void CallApi_SendDeviceData(String ip) {

        JSONObject sendobj = new JSONObject();

        try {
            FirebaseApp.initializeApp(getApplicationContext());

            String version = "v" + MainA.this.getPackageManager()
                    .getPackageInfo(MainA.this.getPackageName(), 0).versionName;

            sendobj.put("user_id", "" + preferences.getKeyUserId());
            sendobj.put("device", "android");
            sendobj.put("version", version);
            sendobj.put("ip", ip);
            sendobj.put("device_token", "" + FirebaseInstanceId.getInstance().getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.CallApi(MainA.this, ApiUrl.addDeviceData, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            JSONObject userobj = respobj.getJSONObject("msg").getJSONObject("User");
                            JSONObject countryobj = respobj.getJSONObject("msg").getJSONObject("Country");
                            String id = "" + userobj.optString("id");
                            String fname = "" + userobj.optString("first_name");
                            String lname = "" + userobj.optString("last_name");
                            String email = "" + userobj.optString("email");
                            String image = "" + userobj.optString("image");
                            String phone = "" + userobj.optString("phone");
                            String username = "" + userobj.optString("username");
                            String dob = "" + userobj.optString("dob");
                            String social_type = "" + userobj.optString("social");
                            String country = "" + countryobj.optString("name");
                            String country_id = "" + countryobj.optString("id");
                            String country_Ios = "" + countryobj.optString("iso");
                            String country_Code = "" + countryobj.optString("country_code");

                            preferences.setKeyUserId(id);
                            preferences.setKeyUserFirstName(fname);
                            preferences.setKeyUserLastName(lname);
                            preferences.setKeyUserEmail(email);
                            preferences.setKeyUserImage(image);
                            preferences.setKeyUserPhone(phone);
                            preferences.setKeyUserName(username);
                            preferences.setKeyUserDateOfBirth(dob);
                            preferences.setKeySocialType(social_type);
                            preferences.setKeyUserCountry(country);
                            preferences.setKeyUserCountryId(country_id);
                            preferences.setKeyUserCountryIOS(country_Ios);
                            preferences.setKeyCountryCode(country_Code);
                            preferences.setKeyIsLogin(true);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {

//        if (MainF.drawer.isDrawerOpen(GravityCompat.START)) {
//            MainF.drawer.closeDrawers();
//            return;
//        }

//        if (bottomNavigationView.getSelectedItemId() == R.id.delivery) {
//            super.onBackPressed();
////            finish();
//        } else {
//            bottomNavigationView.setSelectedItemId(R.id.delivery);
//        }


//        if (!f.onBackPressed()) {
//            int count = this.getSupportFragmentManager().getBackStackEntryCount();
//            if (count == 0) {
//                if (f.whichScreenOpen.equals("started"))
//                    f.METHOD_backfromStarted();
//                else if (f.whichScreenOpen.equals("dropoff")) {
//                    f.METHOD_backfromConfirm_Dropoff();
//                    f.whichScreenOpen = "";
//                } else if (mBackPressed + 2000 > System.currentTimeMillis()) {
//                    super.onBackPressed();
//                    return;
//                } else {
//                    if (MainF.drawer.isDrawerOpen(GravityCompat.START)) {
//                        MainF.drawer.closeDrawers();
//                        return;
//                    }
//
//                    Functions.ShowToast(MainA.this, MainA.this.getString(R.string.tap_again_to_exit));
//                    mBackPressed = System.currentTimeMillis();
//                }
//            }
//        }

        //Working Code : 27-05-2021
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Main_F");

        if (fragment instanceof MainF && fragment.isVisible()) {

            if (getSupportFragmentManager().getBackStackEntryCount() != 0)
                getSupportFragmentManager().popBackStack();
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainA.this);
                builder.setMessage("Do you want to exit ?");
                builder.setTitle("Exit");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        finish();
                    } finally {
                        finish();
                    }
                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0)
                getSupportFragmentManager().popBackStack();
        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.profile:
                /*ProfileF frag = new ProfileF();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.addToBackStack("Profile_F");
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.fl_id, frag).commit();*/

                ProfileFragment frag = new ProfileFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.addToBackStack("Profile_Fragment");
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.add(R.id.fl_id, frag).commit();

                break;

            case R.id.delivery:
                MainF fragm = new MainF();
                FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
                ftt.addToBackStack("Main_F");
                ftt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ftt.replace(R.id.fl_id, fragm, "Main_F").commit();
                break;

            case R.id.history:
                /*HistoryF fragments = new HistoryF();
                FragmentTransaction fttt = getSupportFragmentManager().beginTransaction();
                fttt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fttt.addToBackStack("History_F");
                fttt.replace(R.id.fl_id, fragments, "History_F").commit();*/
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case REQUEST_CODE_GPS_ENABLE:
                Fragment mainF = getSupportFragmentManager().findFragmentById(R.id.fl_id);
                if (mainF instanceof MainF) {
                    mainF.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }
}
