package com.lotustechnologicalsolution;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.MainA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome.MainF;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;

public class WelcomeFragment extends Fragment implements View.OnClickListener {
    TextView tv_welcome;
    Preferences preferences;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_welcome, container, false);
        ActionControl();
        return  view;
    }

    private void ActionControl() {
        preferences = new Preferences(view.getContext());
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_make_a_first_delivery).setOnClickListener(this);
        tv_welcome = view.findViewById(R.id.tv_welcome);
        tv_welcome.setText("Welcome, "+preferences.getKeyUserFirstName()+"!");
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;


            case R.id.btn_make_a_first_delivery:

                //TODO yaha se no_location_fragment pe jana hai but abhi ke liye maine main pe le ja rha

//                MainF fragment = new MainF();
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.addToBackStack("MainF");
//                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
//                transaction.replace(R.id.fl_id, fragment, "MainF").commit();
                startActivity(new Intent(getActivity(), MainA.class));
                //WelcomeFragment fragment = new WelcomeFragment();
                getActivity().finish();

                break;
            default:
                break;
        }
    }

}