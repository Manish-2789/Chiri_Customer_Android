package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile.ProfileF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderHistory.HistoryF;
import com.lotustechnologicalsolution.chiri.BottomSheetDialogsFragments.AddPackageSizeSelectionBottomSheet;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.Callback;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.LocationUpdateServiceCallback;
import com.lotustechnologicalsolution.chiri.MapClasses.GpsUtils;
import com.lotustechnologicalsolution.chiri.MapClasses.MapWorker;
import com.lotustechnologicalsolution.chiri.ModelClasses.ModesTypesModel;
import com.lotustechnologicalsolution.chiri.ModelClasses.PackagesSizeSelectionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DrawMapPathF extends RootFragment implements View.OnClickListener,
        OnMapReadyCallback,
        LocationUpdateServiceCallback, BottomNavigationView.OnNavigationItemSelectedListener, FragmentClickCallback {


    public static String whichScreenOpen = "";
    private final ArrayList<PackagesSizeSelectionModel> packagesList = new ArrayList<>();
    private final double EARTHRADIUS = 6366198;
    View noLocationLayoutView;
    private Marker pickupMarker;
    private Marker dropoffMarker;
    RelativeLayout accessLocationLayoutPinview;
    CoordinatorLayout accessLocationLayoutView;
    boolean isLatLngBound = false;
    Preferences preferences;

    double totalCost = 0;

    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;

    private ModesTypesModel selectedModel;
    private MapView mapFragment;
    private GoogleMap mMap;
    private LatLng latLng, pickupLatlong, dropoffLatlng;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private MapWorker mapWorker;

    String  fromTitle,toTitle;
    String  fromAddress,toAddress;
    String  fromLat,toLat;
    String  fromLng,toLng;

    private TextView tvFromCity,tvFromAddress;
    private TextView tvToCity,tvToAddress;

    private View view;

    public static FragmentClickCallback mfFragmentClickCallback;

    public DrawMapPathF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_path_draw, container, false);

        context = getActivity();

        mfFragmentClickCallback = this;

        mapFragment = view.findViewById(R.id.map);

        mapFragment.getMapAsync(this);

        mapFragment.onCreate(savedInstanceState);


        Bundle bundle = getArguments();
        if (bundle != null) {

            fromTitle = bundle.getString("title");
            fromAddress = bundle.getString("address");
            fromLat = String.valueOf(bundle.getDouble("lat"));
            fromLng = String.valueOf(bundle.getDouble("lng"));

            toTitle = bundle.getString("pickupTitle");
            toAddress = bundle.getString("pickupAddress");
            toLat = bundle.getString("pickupLat");
            toLng = bundle.getString("pickupLng");



        }

        tvFromCity = view.findViewById(R.id.tv_from_location_title);
        tvFromAddress = view.findViewById(R.id.tv_from_location_address);
        tvToCity = view.findViewById(R.id.tv_to_location_title);
        tvToAddress = view.findViewById(R.id.tv_to_location_address);

        tvFromCity.setText("From: "+fromTitle);
        tvFromAddress.setText(fromAddress);

        tvToCity.setText("To: "+toTitle);
        tvToAddress.setText(toAddress);

        //METHOD_setupMap();
        METHOD_init_views();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap = googleMap;
        mapWorker = new MapWorker(context, mMap);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            boolean success = false;

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

        if (onCameraIdleListener != null)
            mMap.setOnCameraIdleListener(onCameraIdleListener);

        mMap.setOnCameraMoveStartedListener(i -> {
            //mMap.clear();
            LatLng centerOfMapPos = mMap.getCameraPosition().target;
            if (centerOfMapPos != null) {
                addMarker(centerOfMapPos, dropoffMarker);
                //getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);
            }
            //isClickAddressItem = true;
        });

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        Zoom_to_Current_location();
    }

    public void addMarker(LatLng latLng, Marker marker) {
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("")
                    //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .draggable(false));

            marker.setPosition(latLng);
            marker.setVisible(false);
        }
    }

   /* public void requestDirection() {

        //  float totalDistance = distance(origin.latitude, origin.longitude, destination.latitude, destination.longitude);

        if (destination != null) {
            GoogleDirection.withServerKey(getString(R.string.map_direction_key)).from(origin).to(destination).transportMode(TransportMode.DRIVING).execute(this);
        }

    }*/

    private void ShowLatLngBoundZoom() {
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        latlngBuilder.include(pickupMarker.getPosition());
        latlngBuilder.include(dropoffMarker.getPosition());
        LatLngBounds bounds = latlngBuilder.build();

        LatLng center = bounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);
        latlngBuilder.include(southWest);
        latlngBuilder.include(northEast);
        if (areBoundsTooSmall(bounds, 300)) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }
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
        double lat = Double.parseDouble(preferences.getKeyUserLat());
        double lng = Double.parseDouble(preferences.getKeyUserLng());
        if ((lat != 0.0 && lng != 0.0)) {
            latLng = new LatLng(lat, lng);
            pickupLatlong = latLng;
            dropoffLatlng = latLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        }
    }

    private void METHOD_init_views() {

        preferences = new Preferences(view.getContext());

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_logout: {
                final Dialog dialog = new Dialog(context);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.show_defult_two_btn_alert_popup_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final TextView txt_yes, txt_no, txt_title, txt_message;
                txt_title = dialog.findViewById(R.id.defult_alert_txt_title);
                txt_message = dialog.findViewById(R.id.defult_alert_txt_message);
                txt_no = dialog.findViewById(R.id.defult_alert_btn_cancel_no);
                txt_yes = dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

                txt_title.setText("" + view.getContext().getString(R.string.logout_status));
                txt_message.setText("" + view.getContext().getString(R.string.are_you_sure));
                txt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        LogoutByThisUserId();
                    }
                });

                txt_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
            break;
        }
    }


    private void LogoutByThisUserId() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", "" + preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.ShowProgressLoader(view.getContext(), false, false);
        ApiRequest.CallApi(view.getContext(), ApiUrl.logout, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.CancelProgressLoader();
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        boolean isnightmode = preferences.getKeyIsNightMode();
                        String language = preferences.getKeyLocale();
                        preferences.clearSharedPreferences();
                        preferences.setKeyIsNightMode(isnightmode);
                        preferences.setKeyLocale(language);
                        Intent logout_intent = new Intent(getActivity(), GetStartedA.class);
                        logout_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout_intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    private LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }



    private void METHOD_openAdd_Packages_Size_Selection_F() {
        AddPackageSizeSelectionBottomSheet f = new AddPackageSizeSelectionBottomSheet(packagesList, totalCost, new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                PackagesSizeSelectionModel model = (PackagesSizeSelectionModel) bundle.getSerializable("DataModel");

            }
        });
        f.show(getChildFragmentManager(), "AddPackageSizeSelection_BottomSheet");
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
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorWhite));
            accessLocationLayoutPinview.setVisibility(View.VISIBLE);
            accessLocationLayoutView.setVisibility(View.VISIBLE);
            GetCurrentLocation();

        } else {
            SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorPrimary));
            noLocationLayoutView.setVisibility(View.VISIBLE);
            accessLocationLayoutPinview.setVisibility(View.GONE);
            accessLocationLayoutView.setVisibility(View.GONE);
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


    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
                        mapFragment.getMapAsync(DrawMapPathF.this);
                        METHOD_setupMap();
                    }
                }
            });


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == Variables.permissionLocation) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorWhite));
                noLocationLayoutView.setVisibility(View.GONE);
                accessLocationLayoutPinview.setVisibility(View.VISIBLE);
                accessLocationLayoutView.setVisibility(View.VISIBLE);
                enable_permission();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorPrimary));
                noLocationLayoutView.setVisibility(View.VISIBLE);
                accessLocationLayoutPinview.setVisibility(View.GONE);
                accessLocationLayoutView.setVisibility(View.GONE);
            }
        }
    }

    //    init map by custom classes smothly
    private void METHOD_setupMap() {

        MapsInitializer.initialize(context);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

//        drop_oof_marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_dropoff);
//        pick_up_marker = BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_pickup);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.profile:
                ProfileF frag = new ProfileF();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack("Main_F");
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.fl_id, frag).commit();


                break;

            case R.id.delivery:
                MainF fragm = new MainF();
                FragmentTransaction ftt = getActivity().getSupportFragmentManager().beginTransaction();
                ftt.addToBackStack("Main_F");
                ftt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ftt.replace(R.id.fl_id, fragm, "Main_F").commit();
                break;

            case R.id.history:
                HistoryF fragments = new HistoryF();
                FragmentTransaction fttt = getActivity().getSupportFragmentManager().beginTransaction();
                fttt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fttt.replace(R.id.fl_id, fragments, "History_F").commit();
                break;
        }
        return true;
    }


    @Override
    public void OnItemClick(int postion, Bundle bundle) {

    }
}