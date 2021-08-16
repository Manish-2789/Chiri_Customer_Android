package com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lotustechnologicalsolution.GetStarted;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.SplashScreenA;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;


public class ProfileFragment extends Fragment {

    private ImageView ivLogout;
    private TextView tvUserName;
    private TextView tvPhone;
    private RelativeLayout rlRelativeLayout;
    private Preferences preferences;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_profile2, container, false);

        preferences = new Preferences(getActivity());
        ivLogout = rootView.findViewById(R.id.iv_logout);
        tvUserName = rootView.findViewById(R.id.tv_username);
        tvPhone = rootView.findViewById(R.id.tv_mobile_number);

        tvUserName.setText(preferences.getKeyUserFirstName()+" "+preferences.getKeyUserLastName());
        tvPhone.setText(preferences.getKeyUserPhone());

        rlRelativeLayout = rootView.findViewById(R.id.rl_relative_layout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               preferences.setKeyIsLogin(false);
               preferences.clearSharedPreferences();
                startActivity(new Intent(getActivity(), GetStarted.class));
                getActivity().finish();
            }
        });

        rlRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ProfileVerificationA.class));
            }
        });
        return rootView;
    }
}