package com.lotustechnologicalsolution.chiri.ActivitiesFragments.SignUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.Interfaces.AdapterClickListenerCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.ModelClasses.CountryCodeModel;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.CountryCodeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CountryCodeA extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout backBtn;
    private TextView tvTbTitle, tvApply;
    private EditText etSearch;
    private LinearLayout tabSearchCountry;
    private RecyclerView recyclerView;
    private List<CountryCodeModel> list;

    private CountryCodeAdapter adp;

    public static String selectedCountry = "", selectedCountryId, selectedCountryCode = "";

    private ProgressBar progressBar;
    private View view;
    private String selectedCountryIso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);

        methodInitViews();
        methodSetadapter();
    }

    private void methodInitViews() {

        backBtn = view.findViewById(R.id.back_ll_id);
        backBtn.setOnClickListener(this);
        tabSearchCountry = view.findViewById(R.id.tab_search_country);
        tvTbTitle = view.findViewById(R.id.tv_goods_title);
        progressBar = view.findViewById(R.id.progress_Bar);
        tvApply = view.findViewById(R.id.tv_apply);
        tvApply.setOnClickListener(this);
        etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO IMPLEMENTATION
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO IMPLEMENTATION
            }

            @Override
            public void afterTextChanged(Editable s) {
                adp.getFilter().filter(s.toString());
            }
        });


        recyclerView = view.findViewById(R.id.rv);
        list = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(CountryCodeA.this));
        recyclerView.setHasFixedSize(false);

        tabSearchCountry.setVisibility(View.VISIBLE);
        tvTbTitle.setText(R.string.select_country);
        progressBar.setVisibility(View.VISIBLE);
        callApiShowCountries();
    }


    private void callApiShowCountries() {
        ApiRequest.CallApi(CountryCodeA.this, ApiUrl.URL_SHOW_COUNTRIES, new JSONObject(), new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);
                        progressBar.setVisibility(View.GONE);
                        if (respobj.getString("code").equals("200")) {
                            JSONArray msgarray = respobj.getJSONArray("msg");
                            methodGettingcountrieslist(msgarray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private void methodGettingcountrieslist(JSONArray msgarray) {
        try {
            list.clear();
            for (int i = 0; i < msgarray.length(); i++) {
                JSONObject countriesobj = msgarray.getJSONObject(i).getJSONObject("Country");

                CountryCodeModel model = new CountryCodeModel();
                model.country_id = "" + countriesobj.optString("id");
                model.phonecode = "" + countriesobj.optString("country_code");
                model.country_name = "" + countriesobj.optString("name");
                model.country_ios = "" + countriesobj.optString("iso");
                list.add(model);
                adp.notifyDataSetChanged();
                methodSetadapter();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void methodSetadapter() {

        adp = new CountryCodeAdapter(CountryCodeA.this, list, new AdapterClickListenerCallback() {
            @Override
            public void OnItemClick(int postion, Object modelObj, View view) {
                CountryCodeModel model = (CountryCodeModel) modelObj;

                selectedCountryId = model.country_id;
                selectedCountryCode = model.phonecode;
                selectedCountryIso = model.country_ios;
                selectedCountry = model.country_name;

                list.remove(postion);
                model.is_tick_visible = true;
                list.add(postion, model);

                adp.notifyItemChanged(postion);
                adp.notifyDataSetChanged();


                //if (callBack != null)
                    passData();

            }

            @Override
            public void OnItemLongClick(int postion, Object modelObj, View view) {

            }
        });

        recyclerView.setAdapter(adp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ll_id:
                Functions.hideSoftKeyboard(CountryCodeA.this);
                getFragmentManager().popBackStackImmediate();
                break;

            default:
                break;

        }
    }

    public void passData() {
        Functions.hideSoftKeyboard(CountryCodeA.this);
        Bundle bundle = new Bundle();

        bundle.putString("selected_country_id", selectedCountryId);
        bundle.putString("selected_country_code", selectedCountryCode);
        bundle.putString("selected_country_iso", selectedCountryIso);
        bundle.putString("selected_country", selectedCountry);

        //callBack.onResponce(bundle);
        //getFragmentManager().popBackStackImmediate();
        Intent intent = new Intent();
        intent.putExtra("result",bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
}