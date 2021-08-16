

package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.geometry.Point;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderCreate.OrderCreateF;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.AdapterClickListenerCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.ModelClasses.NearbyLocationModel;
import com.lotustechnologicalsolution.chiri.RecyclerViewAdapters.NearbyLocationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class SearchLocationA extends AppCompatActivity implements View.OnClickListener {

    private final ArrayList<NearbyLocationModel> predictedLocationList = new ArrayList<>();
    private final List<NearbyLocationModel> nearbyList = new ArrayList<>();
    FragmentClickCallback fragmentCallBack;
    Bundle callBackBundle = new Bundle();
    ProgressBar nearByLocationProgress;
    ImageView ivBack;
    String title;
    TextView tvSearchTitle;
    Preferences preferences;
    int tvCount = 0;
    long delay = 1000;
    Timer timer;
    String initQuery;
    TextView tvPickupAddressTitle;

    Context context = this;
    LinearLayout btn_confirm_dropoff_sr;
    LatLng pickUpLocation, dropOffLocation;
    String pickupAddress;
    String pickuptitle;
    String deliverAddress;
    String deliverTitle;

    boolean isFromAddress;//New Code 01-06-2021
    //private View view;
    //private RecyclerView rcNearbyLocation;
   private ListView rcNearbyLocation;
    private NearbyLocationAdapter nearbyAdapter;
    TextInputEditText edToLocation, edFromLocation;
    private Button btn_continue;
    private RelativeLayout btnSetMap;
    View dottedView;

    public SearchLocationA() {
    }

    public SearchLocationA(String query, String title, TextView tvPickupAddressTitle, FragmentClickCallback fragmentCallBack) {
        this.initQuery = query;
        this.title = title;
        this.fragmentCallBack = fragmentCallBack;
        this.tvPickupAddressTitle = tvPickupAddressTitle;
    }

    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_location_search);
        //view = inflater.inflate(R.layout.fragment_location_search, container, false);

        InitControl();

        //Bundle bundle = getArguments();
        Intent bundle = getIntent();
        if (bundle != null) {

            //et_text_watcher_cr.setText(bundle.getString("pickupaddress")); Old Running
            edFromLocation.setText(bundle.getStringExtra("pickupaddress")); //New code Dt- 01-06-2021
            pickUpLocation = bundle.getParcelableExtra("pickup");
            pickupAddress = bundle.getStringExtra("pickupaddress");
            pickuptitle = bundle.getStringExtra("pickuptitle");
        }

        ActionControl();

        //To Address
        edToLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCount = charSequence.toString().length();
                if (tvCount > 0) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            predictedLocationList.clear();
                            autoCompleteSearch(charSequence.toString(), false);
                            isFromAddress = false;//New Code 01-06-2021
                        }
                    }, delay);
                } else {
                    if (timer != null) {
                        timer.cancel();
                    }
                    /*predictedLocationList.clear();
                    predictedLocationList.addAll(nearbyList);
                    nearbyAdapter.notifyDataSetChanged();*/ //Comment it due to exception
                    //nearbyAdapter.updateList(predictedLocationList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //From Address
        edFromLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCount = charSequence.toString().length();
                if (tvCount > 0) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    predictedLocationList.clear();
                    nearbyAdapter.notifyDataSetChanged();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //predictedLocationList.clear();
                            autoCompleteSearch(charSequence.toString(), true);
                            isFromAddress = true;//New Code 01-06-2021
                        }
                    }, delay);
                } else {
                    if (timer != null) {
                        timer.cancel();
                    }
                    /*predictedLocationList.clear();
                    predictedLocationList.addAll(nearbyList);
                    //nearbyAdapter.updateList(predictedLocationList);
                    nearbyAdapter.notifyDataSetChanged();*/ //Comment it due to exception
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //return view;
    }

    private LatLng getCoordinate(double lat0,double lng0,long dy,long dx)
    {
        double lat = lat0 + (180 / Math.PI) * (dy/6378137);
        double lng = lng0 + (180 / Math.PI) * (dx/6328137)/Math.cos(lat0);
        return new LatLng(lat,lng);
    }

    private void autoCompleteSearch(String query, boolean isFromAddress) {
        predictedLocationList.clear();


        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.places_api_key), Locale.US);
        }
        //  Places.initialize(view.getContext(), view.getContext().getString(R.string.places_api_key));
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        //LocationBias locationBias = RectangularBounds.
        FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest.builder();
                // Call either setLocationBias() OR setLocationRestriction().
                requestBuilder.setTypeFilter(TypeFilter.GEOCODE);
                if(isFromAddress==false&&pickUpLocation!=null)
                {
                    LocationBias locationBias = RectangularBounds.newInstance(getCoordinate(pickUpLocation.latitude,pickUpLocation.longitude,-25000,-25000),
                            getCoordinate(pickUpLocation.latitude,pickUpLocation.longitude,25000,25000));
                    requestBuilder.setLocationBias(locationBias);
                }
                //   .setTypeFilter(TypeFilter.ADDRESS)
                //requestBuilder.setCountry("IN");
                try {
                    RectangularBounds bounds = RectangularBounds.newInstance(toBounds(new LatLng(Double.parseDouble(preferences.getKeyUserLat()),Double.parseDouble(preferences.getKeyUserLng())),100));
                    requestBuilder.setLocationBias(bounds);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                requestBuilder.setSessionToken(token);
                requestBuilder.setQuery(query);

        FindAutocompletePredictionsRequest request = requestBuilder.build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(@NonNull FindAutocompletePredictionsResponse response) {
                if(response.getAutocompletePredictions().size() == 0)
                {
                    predictedLocationList.removeAll(predictedLocationList);
                }
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    Log.i(Variables.tag, prediction.getPlaceId());

                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                    FetchPlaceRequest place_request = FetchPlaceRequest.builder(prediction.getPlaceId(), fields).build();
                    placesClient.fetchPlace(place_request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                            Place place = fetchPlaceResponse.getPlace();
                            Log.d(Variables.tag, "Test : " + place);
                            Log.d(Variables.tag, "size : " + predictedLocationList.size());

                            NearbyLocationModel model = new NearbyLocationModel();

                            model.setTitle(place.getName());
                            model.setAddress(place.getAddress());
                            model.setLat(place.getLatLng().latitude);
                            model.setLng(place.getLatLng().longitude);
                            model.setPlaceId(place.getId());
                            try {
                                predictedLocationList.add(model);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            nearbyAdapter.notifyDataSetChanged();
                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(Variables.tag, "Place not found: " + apiException.getStatusCode());
                }
            }
        });

    }

    LatLngBounds toBounds(LatLng center,double radius)
    {
        double distanceFromCenterToCorner = radius*Math.sqrt(2.0);
        LatLng southWestCorner = SphericalUtil.computeOffset(center,distanceFromCenterToCorner,225.0);
        LatLng northEastCorner = SphericalUtil.computeOffset(center,distanceFromCenterToCorner,45.0);
        return new LatLngBounds(southWestCorner,northEastCorner);
    }

    private void NearBySearch() {
        nearbyList.clear();
        predictedLocationList.clear();
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.places_api_key), Locale.US);
        }
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(initQuery)
                .build();
        nearByLocationProgress.setVisibility(View.VISIBLE);
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            nearByLocationProgress.setVisibility(View.GONE);
            if(response.getAutocompletePredictions().size() == 0)
            {
                predictedLocationList.removeAll(predictedLocationList);
            }
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(Variables.tag, prediction.getPlaceId());

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                FetchPlaceRequest place_request = FetchPlaceRequest.builder(prediction.getPlaceId(), fields).build();
                placesClient.fetchPlace(place_request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.d(Variables.tag, "Test : " + place);

                        NearbyLocationModel model = new NearbyLocationModel();

                        model.setTitle(place.getName());
                        model.setAddress(place.getAddress());
                        model.setLat(place.getLatLng().latitude);
                        model.setLng(place.getLatLng().longitude);
                        model.setPlaceId(place.getId());
                        predictedLocationList.add(model);
                        nearbyList.add(model);
                        try {
                            nearbyAdapter.notifyDataSetChanged();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).addOnFailureListener((exception) -> {
            nearByLocationProgress.setVisibility(View.GONE);
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(Variables.tag, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    public void InitControl() {
        preferences = new Preferences(this);
        ivBack = findViewById(R.id.iv_back);
        rcNearbyLocation = findViewById(R.id.rc_Nearby_location);
        nearByLocationProgress = findViewById(R.id.near_by_location_progress);
        edFromLocation = findViewById(R.id.edFromLocation);
        edToLocation = findViewById(R.id.edToLocation);
        edFromLocation.setText(preferences.getKeyPickupAdress());//New Code Dt-01-06-2021
        btnSetMap = findViewById(R.id.relativeSetOnMap);
        dottedView = findViewById(R.id.img_verical_line);

        dottedView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        SetUpScreenData();
    }


    private void SetUpScreenData() {
        //tvSearchTitle.setText("Choose Destination");
        METHODSetAdapter();
        //NearBySearch();
    }

    private void METHODSetAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
       // rcNearbyLocation.setLayoutManager(layoutManager);
        nearbyAdapter = new NearbyLocationAdapter(SearchLocationA.this,predictedLocationList, new AdapterClickListenerCallback() {
            @Override
            public void OnItemClick(int postion, Object object, View view) {
                NearbyLocationModel model = (NearbyLocationModel) object;

                switch (view.getId()) {
                    default: {
                        try {
                            OrderCreateF.pre_lat = "" + model.getLat();
                            OrderCreateF.pre_lng = "" + model.getLng();
                            OrderCreateF.pre_address = "" + model.getAddress();
                        } catch (Exception e) {
                        }

                        if (isFromAddress) {
                            //From Address
                            LatLng latLng = new LatLng(model.getLat(), model.getLng());

                            pickUpLocation = latLng;
                            pickupAddress = model.getAddress();
                            pickuptitle = model.getTitle();

                            edFromLocation.setText(model.getAddress());
                        } else {

                            LatLng latLng = new LatLng(model.getLat(), model.getLng());
                            dropOffLocation = latLng;
                            deliverAddress = model.getAddress();
                            deliverTitle = model.getTitle();

                            callBackBundle.putString("toTitle", model.getTitle());
                            callBackBundle.putString("toAddress", model.getAddress());
                            callBackBundle.putDouble("toLat", model.getLat());
                            callBackBundle.putDouble("toLng", model.getLng());

                            edToLocation.setText(model.getAddress());
                            try {

                                btnSetMap.performClick();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        //getActivity().onBackPressed();
                    }
                }
            }

            @Override
            public void OnItemLongClick(int position, Object Model, View view) {
            }
        });
        rcNearbyLocation.setAdapter(nearbyAdapter);
    }

    public void ActionControl() {
        ivBack.setOnClickListener(this);
        //  btn_continue.setOnClickListener(this);
        btnSetMap.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                Functions.HideSoftKeyboard(this);
                finish();
                /*MainF fragm = new MainF();
                FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
                ftt.addToBackStack("Main_F");
                ftt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ftt.replace(R.id.fl_id, fragm, "Main_F").commit();*/
                //getActivity().onBackPressed();
                break;

            /*case R.id.btn_confirm_dropoff_sr1:
                Log.d("CallBackBundle", callBackBundle.toString());
                ConfirmDeliveryAddressF confirmDeliveryAddressF = new ConfirmDeliveryAddressF();

                callBackBundle.putString("fromAddress", pickupAddress);
                callBackBundle.putString("fromTitle", pickuptitle);
                callBackBundle.putDouble("fromLat", pickUpLocation.latitude);
                callBackBundle.putDouble("fromLng", pickUpLocation.longitude);
                callBackBundle.putBoolean("isDrawPath", true);

                confirmDeliveryAddressF.setArguments(callBackBundle);
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.fl_id, confirmDeliveryAddressF, "ConfirmDeliveryAddress_F").addToBackStack("ConfirmDeliveryAddress_F").commit();
                break;*/

            case R.id.relativeSetOnMap:
                //ConfirmDeliveryAddressA confirmDeliveryAddress2F = new ConfirmDeliveryAddressA();
                Intent callBackBundle = new Intent(SearchLocationA.this,ConfirmDeliveryAddressA.class);

                //From Address
                callBackBundle.putExtra("fromAddress", pickupAddress);
                callBackBundle.putExtra("fromTitle", pickuptitle);
                callBackBundle.putExtra("fromLat", pickUpLocation.latitude);
                callBackBundle.putExtra("fromLng", pickUpLocation.longitude);
                callBackBundle.putExtra("toAddress", deliverAddress);
                callBackBundle.putExtra("toTitle", deliverTitle);
                if (dropOffLocation != null) {
                    callBackBundle.putExtra("toLat", dropOffLocation.latitude);
                    callBackBundle.putExtra("toLng", dropOffLocation.longitude);
                }
                startActivity(callBackBundle);
                finish();
                /*confirmDeliveryAddress2F.setArguments(callBackBundle);

                FragmentTransaction ft2 = getChildFragmentManager().beginTransaction();
                ft2.add(R.id.fl_id, confirmDeliveryAddress2F, "ConfirmDeliveryAddress_F").addToBackStack("ConfirmDeliveryAddress_F").commit();*/
                break;
        }
    }
}