package com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileVerificationA extends AppCompatActivity {
    private TextView tvUserName;
    private TextView tvHeadingPhone;
    private TextView tvEmailVerify;
    private TextView tvEmail;
    private TextView tvPhone;
    private ImageView ivBack;

    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_verification);

        preferences = new Preferences(ProfileVerificationA.this);

        ivBack = findViewById(R.id.iv_back);
        tvUserName = findViewById(R.id.tv_username);
        tvHeadingPhone = findViewById(R.id.tv_heading_phone);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvEmailVerify = findViewById(R.id.tv_verify_email);

        tvUserName.setText(preferences.getKeyUserFirstName()+" "+preferences.getKeyUserLastName());
        tvHeadingPhone.setText(preferences.getKeyUserPhone());
        tvEmail.setText(preferences.getKeyUserEmail());
        tvPhone.setText(preferences.getKeyUserPhone());
        getUserDetail();

        tvEmailVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvEmailVerify.getText().equals("Verify"))
                {
                    callApiVerifyEmail();
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserDetail()
    {

    JSONObject sendobj = new JSONObject();

        try {
        sendobj.put("user_id", preferences.getKeyUserId());
    } catch (
    JSONException e) {
        e.printStackTrace();
    }

        Functions.ShowProgressLoader(ProfileVerificationA.this,false,false);
        ApiRequest.CallApi(ProfileVerificationA.this, ApiUrl.URL_GET_USER_DETAILS, sendobj, new Callback() {
        @Override
        public void Responce(String resp) {
            Functions.CancelProgressLoader();
            try {
                JSONObject mainObject = new JSONObject(resp);
                if(mainObject.getString("code").equals("200"))
                {
                    JSONObject userObject = mainObject.getJSONObject("msg").getJSONObject("User");
                    if(userObject.getString("is_email_verified").equals("0"))
                    {
                        tvEmailVerify.setText("Verify");
                        tvEmailVerify.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    });

}
    private void callApiVerifyEmail()
    {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(ProfileVerificationA.this,false,false);
        ApiRequest.CallApi(ProfileVerificationA.this, ApiUrl.URL_EMAIL_VERIFY_REQUEST, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                try {
                    JSONObject mainObject = new JSONObject(resp);
                    if(mainObject.getString("code").equals("200"))
                    {
                        Toast.makeText(ProfileVerificationA.this,mainObject.getString("msg"),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }
}