package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryTypeModel;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.DeliveryTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class VehicleListF extends Fragment implements DeliveryTypeAdapter.DeliveryTypeItemSelectListener {

    private ArrayList<DeliveryTypeModel> deliveryTypeModelArrayList;
    private DeliveryTypeAdapter deliveryTypeAdapter;
    private RecyclerView rvDeliveryType;
    private DeliveryTypeAdapter.DeliveryTypeItemSelectListener parentListner;
    private Preferences preferences;
    private TextView tvNotFound;
    private DeliveryDetails deliveryDetails;
    private String modeId = "";
    public VehicleListF() {
        // Required empty public constructor
    }
    public VehicleListF(DeliveryTypeAdapter.DeliveryTypeItemSelectListener parentListner) {
        this.parentListner = parentListner;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        preferences = new Preferences(getActivity());

        rvDeliveryType = rootView.findViewById(R.id.rvDeliveryType);
        tvNotFound = rootView.findViewById(R.id.tv_not_found);

        modeId = getArguments().getString("mode_id");
        deliveryDetails = (DeliveryDetails) getArguments().getSerializable("delivery_detail_model");
        getDeliverTypeList(modeId);
        return rootView;
    }

    public void getDeliverTypeList(String modeID) {

        JSONObject request = new JSONObject();

        try {

            request.put("mode_id", modeID);
            //request.put("sender_lat", preferences.getKeyUserLat());
            //request.put("sender_lon",preferences.getKeyUserLng());
            request.put("sender_lat", deliveryDetails.getFromLatitude());
            request.put("sender_lon",deliveryDetails.getFromLongitude());
            request.put("receiver_lat",deliveryDetails.getToLatitude());
            request.put("receiver_lon",deliveryDetails.getToLongitude());

            ApiRequest.CallApi(getActivity(), ApiUrl.getDeliveryModeList, request, resp -> {

                if (resp != null) {

                    try {

                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {

                            rvDeliveryType.setVisibility(View.VISIBLE);
                            tvNotFound.setVisibility(View.GONE);
                            JSONArray jsonArray = respobj.getJSONArray("msg");

                            if (jsonArray.length() > 0) {

                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<DeliveryTypeModel>>() {
                                }.getType();
                                deliveryTypeModelArrayList = gson.fromJson(jsonArray.toString(), type);

                                deliveryTypeAdapter = new DeliveryTypeAdapter(deliveryTypeModelArrayList, VehicleListF.this);
                                rvDeliveryType.setAdapter(deliveryTypeAdapter);
                                rvDeliveryType.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                                //runItemAnimation(binding.rvDeliveryType);
                            }
                        }
                        else
                        {
                            rvDeliveryType.setVisibility(View.GONE);
                            tvNotFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTypeSelect(int itemPosition, Object object) {
        DeliveryTypeModel deliveryTypeModelToParent = null;
          for (int i = 0; i < deliveryTypeModelArrayList.size(); i++) {
            DeliveryTypeModel deliveryTypeModel = deliveryTypeModelArrayList.get(i);
            if (i == itemPosition) {
                deliveryTypeModelToParent = deliveryTypeModel;
                deliveryTypeModelToParent.setModeID(modeId);
            }
            deliveryTypeModel.setSelected(i == itemPosition);
            deliveryTypeModelArrayList.set(i, deliveryTypeModel);
        }

        deliveryTypeAdapter.notifyDataSetChanged();
        parentListner.onTypeSelect(itemPosition,deliveryTypeModelToParent);
    }
}