package com.lotustechnologicalsolution;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;

public class TermsAndConditionsFragment extends Fragment implements View.OnClickListener {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);
        ActionControl();
        return view;
    }

    private void ActionControl() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.btn_agree_yes).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;

            case R.id.btn_agree_yes:
                Functions.HideSoftKeyboard(getActivity());
                WelcomeFragment fragment = new WelcomeFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack("Welcome");
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.replace(R.id.fl_id, fragment, "Welcome").commit();
                break;


            default:
                break;
        }

    }
}