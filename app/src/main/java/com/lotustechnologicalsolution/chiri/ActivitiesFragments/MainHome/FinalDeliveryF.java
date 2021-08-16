package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile.ProfileF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile.ProfileVerificationA;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderHistory.HistoryF;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.Interfaces.LocationUpdateServiceCallback;
import com.lotustechnologicalsolution.chiri.MapClasses.GpsUtils;
import com.lotustechnologicalsolution.databinding.FragmentFinalDeliveryBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FinalDeliveryF extends RootFragment implements
        OnMapReadyCallback,
        LocationUpdateServiceCallback, BottomNavigationView.OnNavigationItemSelectedListener {

    FragmentFinalDeliveryBinding binding;

    private final double EARTHRADIUS = 6366198;

    Preferences preferences;

    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private MapView mapFragment;
    private GoogleMap mMap;
    private View view;
    private LatLng latLng;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private RelativeLayout rlArriving;
    private String driverName = "";

    private ImageView ivRipple;

    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    PowerManager pm;



    public FinalDeliveryF() {
    }

    @Override
    public boolean onBackPressed() {
        Log.i("test","onbackpress");
        //return super.onBackPressed();
        return true;
    }

    @Override
    public void onStop() {
        Log.i("test","onStop");
        //startActivity(new Intent(getActivity(), MainA.class));
        //WelcomeFragment fragment = new WelcomeFragment();
        //getActivity().finish();

        super.onStop();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance("https://shaped-goal-91009.firebaseio.com");
        myRef  = database.getReference("CurrentRides");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_final_delivery, container, false);

        view = binding.getRoot();

        context = getActivity();

        preferences = new Preferences(getActivity());

        mapFragment = view.findViewById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.onCreate(savedInstanceState);

        METHOD_init_views();

        animateRiplle();

        pm = (PowerManager)getActivity().getSystemService(getActivity().POWER_SERVICE);

        if(mTimer!=null)
        {
            mTimer.cancel();
        }
        else
        {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(),0,1000*60);

        /*binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    return;
                }
            }
        });*/

        binding.icArriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });


        return view;
    }

    class  TimeDisplayTimerTask extends TimerTask
    {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        getStatusFromDatabase();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getStatusFromDatabase()
    {
        myRef.child(preferences.getKeyUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String key = null;
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    key = ds.getKey();
                    break;
                }
                if(key!=null)
                {
                    myRef.child(preferences.getKeyUserId()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            try {
                                String driverID = snapshot.child("driver_id").getValue().toString();
                                String status = snapshot.child("status").getValue().toString();
                                //Toast.makeText(getActivity(),driverID,Toast.LENGTH_LONG).show();
                                if(!(status.equals("0"))) {
                                    binding.tvWaitingDriver.setVisibility(View.GONE);
                                    binding.ivRipple.setVisibility(View.GONE);

                                    if (status.equals("1")&&(!(driverID.equals("0")))) {
                                        binding.rlArriving.setVisibility(View.VISIBLE);
                                        binding.rlDriverControl.setVisibility(View.VISIBLE);
                                        showSourceToDestinationRoute(driverID);
                                        if(driverName.equals(""))
                                        {
                                            getUserDetail(driverID);
                                        }
                                    }
                                    else if (status.equals("5")&&(!(driverID.equals("0")))) {
                                        binding.rlArriving.setVisibility(View.GONE);
                                        binding.rlDriverControl.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(),"Delivery Completed",Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),"No driver found",Toast.LENGTH_LONG).show();
                                        getActivity().finish();
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void animateRiplle()
    {
        LayerDrawable layerDrawable = (LayerDrawable) binding.ivRipple.getDrawable();
        final GradientDrawable outerCircle = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.circle_outer);
        final GradientDrawable innerCircle = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.circle_inner);
        final GradientDrawable innerCircle1 = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.circle_inner_1);
        final GradientDrawable innerCircle2 = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.circle_inner_2);
        final GradientDrawable innerCircle3 = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.circle_inner_3);

        /*binding.ivRipple.setImageDrawable(animationDrawable);
        animationDrawable.setOneShot(false);
        animationDrawable.start();*/
        new Thread()
        {
            public void run()
            {
                while(binding.ivRipple.getVisibility() == View.VISIBLE)
                {
                    try {
                        outerCircle.setAlpha(0);
                        innerCircle.setAlpha(0);
                        innerCircle1.setAlpha(0);
                        innerCircle2.setAlpha(0);
                        innerCircle3.setAlpha(0);
                        sleep(200);
                        innerCircle.setAlpha(255);
                        sleep(200);
                        innerCircle1.setAlpha(255);
                        sleep(200);
                        innerCircle2.setAlpha(255);
                        sleep(200);
                        innerCircle3. setAlpha(255);
                        sleep(200);
                        outerCircle.setAlpha(255);
                        sleep(200);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void showSourceToDestinationRoute(String driverID) {
        mMap.clear();
        /*try {
            if (viewRideInfo_response.getDetails().getDrop_long().equals("")) {
                try {
                    mDatabaseReference.child("" + viewRideInfo_response.getDetails().getDriver_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DriverLocation driverLocation22 = dataSnapshot.getValue(DriverLocation.class);
                            try {
                                //mm = MapUtils.setDrivermarker(TrackRideAactiviySamir.this, mGoogleMap, new LatLng(Double.parseDouble("" + driverLocation22.driver_current_latitude), Double.parseDouble("" + driverLocation22.driver_current_longitude)), viewRideInfo_response.getDetails().getDriver_name());
                                mm = MapUtils.setDrivermarker(TrackRideAactiviySamir.this, mGoogleMap, new LatLng(Double.parseDouble("" + driverLocation22.driver_current_latitude), Double.parseDouble("" + driverLocation22.driver_current_longitude)), viewRideInfo_response.getDetails().getCar_number());
                                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(Double.parseDouble("" + driverLocation22.driver_current_latitude), Double.parseDouble("" + driverLocation22.driver_current_longitude))).zoom(Config.MapDefaultZoom).build()));

                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            debug_firebase_message.setText("Fetching error " + databaseError.getMessage());
                        }
                    });
                } catch (Exception e) {
                }
            } else {
                try {
                    LatLng source = new LatLng(Double.parseDouble("" + viewRideInfo_response.getDetails().getPickup_lat()), Double.parseDouble("" + viewRideInfo_response.getDetails().getPickup_long()));
                    LatLng Destination = new LatLng(Double.parseDouble("" + viewRideInfo_response.getDetails().getDrop_lat()), Double.parseDouble("" + viewRideInfo_response.getDetails().getDrop_long()));
                    MapUtils.setGreedmarker(this, mGoogleMap, source);
                    MapUtils.setRedmarker(this, mGoogleMap, Destination);
                    DrawRouteMaps.getInstance(TrackRideAactiviySamir.this, TrackRideAactiviySamir.this, sessionManager, null, false).draw(source, Destination, mGoogleMap);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            Toast.makeText(trackRideActivity, "Unable To Extract Route Info. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        DatabaseReference driverRef = database.getReference("User");
        driverRef.child(driverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    mMap.clear();

                    Marker markerDriver = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Driver").icon(BitmapDescriptorFactory.fromBitmap(Functions.createDrawableMarker(getActivity()))));
                    //Marker markerCurrent = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(preferences.getKeyUserLat()),Double.parseDouble(preferences.getKeyUserLng()))).title("My Location"));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(markerDriver.getPosition());
                    //builder.include(new LatLng(Double.parseDouble(preferences.getKeyUserLat()),Double.parseDouble(preferences.getKeyUserLng())));
                    //builder.include(markerCurrent.getPosition());

                    LatLngBounds latLngBounds = builder.build();
                    //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds,100);
                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(latitude,longitude),10,50,50));
                    mMap.moveCamera(cu);

                    LatLng origin = new LatLng(latitude,longitude);
                    LatLng dest = new LatLng(Double.parseDouble(preferences.getKeyUserLat()),Double.parseDouble(preferences.getKeyUserLng()));

                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.fragment_bottomsheet_final_delivery);

        TextView tvOtp = bottomSheetDialog.findViewById(R.id.tvOtp);
        TextView tvUserName = bottomSheetDialog.findViewById(R.id.tvUserName);
        TextView tvRatingStar = bottomSheetDialog.findViewById(R.id.tvRatingStar);
        ImageView ivPhoneCall = bottomSheetDialog.findViewById(R.id.ivPhoneCall);
        LinearLayout linearEdit = bottomSheetDialog.findViewById(R.id.linearEdit);
        LinearLayout linearChat = bottomSheetDialog.findViewById(R.id.linearChat);
        LinearLayout linearShare = bottomSheetDialog.findViewById(R.id.linearShare);


        ivPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Phone Call", Toast.LENGTH_SHORT).show();
            }
        });

        linearEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
            }
        });

        linearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Chat", Toast.LENGTH_SHORT).show();
            }
        });
        linearShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Share", Toast.LENGTH_SHORT).show();
            }
        });


        bottomSheetDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapFragment.onResume();
        mMap = googleMap;
        //mapWorker = new MapWorker(context, mMap);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success;

            if (preferences.getKeyIsNightMode()) {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.dark_map));
            } else {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.gray_map));
            }

            if (!success) {
                Log.e(Variables.tag, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(Variables.tag, "Can't find style. Error: ", e);
        }

        /*mMap.setOnCameraMoveStartedListener(i ->
        {
            mMap.clear();
            LatLng centerOfMapPos = mMap.getCameraPosition().target;
            //isClickAddressItem = true;
        });*/

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //googleMap.setMyLocationEnabled(true);
      /*  googleMap.getUiSettings().

                setZoomControlsEnabled(true);*/
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);


        double lat = Double.parseDouble(preferences.getKeyUserLat());
        double lng = Double.parseDouble(preferences.getKeyUserLng());
        if ((lat != 0.0 && lng != 0.0)) {
            latLng = new LatLng(lat, lng);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        }
        //Zoom_to_Current_location();
    }

    private void configureCameraIdle() {

        /*onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCameraIdle() {


            }
        };*/
    }

    @Override
    public void onDataReceived(LatLng data) {
        if (mMap != null)
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        mMap.setMyLocationEnabled(true);

        if (data == null)
            Zoom_to_Current_location();
    }

    private void Zoom_to_Current_location() {
        /*double lat = Double.parseDouble(preferences.getKeyUserLat());
        double lng = Double.parseDouble(preferences.getKeyUserLng());
        if ((lat != 0.0 && lng != 0.0)) {
            latLng = new LatLng(lat, lng);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        }*/
    }

    private void METHOD_init_views() {

        preferences = new Preferences(view.getContext());

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapFragment.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    private void enable_permission() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            new GpsUtils(getContext()).turnGPSOn(isGPSEnable -> {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            Variables.permissionLocation);
                }

            });
        } else if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorWhite));
            GetCurrentLocation();

        } else {
            SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorPrimary));
        }
    }

    //    phone defult navigation bar color handle
    private void SetStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(color);
        }
    }


    //    get current location by using phone GPS
    private void GetCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(Variables.tag, "GetCurrentlocation: inside Not Permissioned");
            enable_permission();
            return;
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        double lat = (latLng.latitude);
                        double lon = (latLng.longitude);

                        preferences.setKeyUserLat("" + lat);
                        preferences.setKeyUserLng("" + lon);

                        mapFragment.onResume();
                        mapFragment.getMapAsync(FinalDeliveryF.this);
                        METHOD_setupMap();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Variables.permissionLocation) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorWhite));
                enable_permission();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorPrimary));
            }
        }
    }

    //    init map by custom classes smoothly
    private void METHOD_setupMap() {

        MapsInitializer.initialize(context);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

        configureCameraIdle();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.profile:
                ProfileF frag = new ProfileF();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.fl_id, frag, "Profile_F").addToBackStack("Profile_F").commit();
                break;

            case R.id.delivery:
                MainF fragm = new MainF();
                FragmentTransaction ftt = getActivity().getSupportFragmentManager().beginTransaction();
                ftt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ftt.replace(R.id.fl_id, fragm, "Main_F").addToBackStack("Main_F").commit();
                break;

            case R.id.history:
                HistoryF fragments = new HistoryF();
                FragmentTransaction fttt = getActivity().getSupportFragmentManager().beginTransaction();
                fttt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fttt.replace(R.id.fl_id, fragments, "History_F").addToBackStack("History_F").commit();
                break;
        }
        return true;
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&key="+getResources().getString(R.string.google_api_key);

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                //Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                //lineOptions.color(Color.RED);
            }

            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
            binding.tvDuration.setText(duration);

            // Drawing polyline in the Google Map for the i-th route
            //map.addPolyline(lineOptions);
        }
    }


    class DirectionsJSONParser {

        /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
        public List<List<HashMap<String,String>>> parse(JSONObject jObject){

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            JSONObject jDistance = null;
            JSONObject jDuration = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for(int i=0;i<jRoutes.length();i++){
                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");

                    List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for(int j=0;j<jLegs.length();j++){

                        /** Getting distance from the json data */
                        jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                        HashMap<String, String> hmDistance = new HashMap<String, String>();
                        hmDistance.put("distance", jDistance.getString("text"));

                        /** Getting duration from the json data */
                        jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                        HashMap<String, String> hmDuration = new HashMap<String, String>();
                        hmDuration.put("duration", jDuration.getString("text"));

                        /** Adding distance object to the path */
                        path.add(hmDistance);

                        /** Adding duration object to the path */
                        path.add(hmDuration);

                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for(int k=0;k<jSteps.length();k++){
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                path.add(hm);
                            }
                        }
                    }
                    routes.add(path);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
            }
            return routes;
        }

        /**
         * Method to decode polyline points
         * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         * */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }


    private void getUserDetail(String driverID) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", driverID);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(getActivity(), false, false);
        ApiRequest.CallApi(getActivity(), ApiUrl.URL_GET_USER_DETAILS, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                try {
                    JSONObject mainObject = new JSONObject(resp);
                    if (mainObject.getString("code").equals("200")) {
                        JSONObject userObject = mainObject.getJSONObject("msg").getJSONObject("User");
                        String name = userObject.getString("first_name") + " " + userObject.getString("last_name");
                        binding.tvDriverName.setText(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}