package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails.CheckoutNewF;
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
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.chiri.ModelClasses.ModesTypesModel;
import com.lotustechnologicalsolution.chiri.ModelClasses.PackagesSizeSelectionModel;
import com.lotustechnologicalsolution.chiri.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ConfirmDeliveryAddressA extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback,
        LocationUpdateServiceCallback, BottomNavigationView.OnNavigationItemSelectedListener, FragmentClickCallback, DirectionCallback {


    public static String whichScreenOpen = "";
    public static FragmentClickCallback mfFragmentClickCallback;
    private final ArrayList<PackagesSizeSelectionModel> packagesList = new ArrayList<>();
    private final double EARTHRADIUS = 6366198;
    View noLocationLayoutView;
    RelativeLayout accessLocationLayoutPinview;
    CoordinatorLayout accessLocationLayoutView;
    boolean isLatLngBound = false;
    Preferences preferences;
    double totalCost = 0;
    String fromTitle, toTitle;
    String fromAddress, toAddress;
    Double fromLat, toLat;
    Double fromLng, toLng;
    boolean isDrawPath;
    private Marker pickupMarker;
    private Marker dropoffMarker;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private ModesTypesModel selectedModel;
    private MapView mapFragment;
    private GoogleMap mMap;
    private LatLng latLng, pickupLatlong, dropoffLatlng;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private MapWorker mapWorker;
    private LinearLayout linearFromLocation, linearToLocation;
    private View dottedView;
    private TextView tvFromCity, tvFromAddress;
    private TextView tvToCity, tvToAddress;
    //private View view;
    private Button btnConfirm;
    private boolean isConfirm, isFromLocationClick, isToLocationClick;
    private LatLng origin, destination;
    private ImageView ivMarkerPickup, ivMarkerDrop, ivBack, ivFromIndicator, ivToIndicator;
    private Bitmap srcMarkerBitmap, destMarkerBitmap, truckMarkerBitmap;
    private FragmentManager fragmentManager;
    private Utility objUtility;

    private DeliveryDetails deliveryDetails;

    public ConfirmDeliveryAddressA() {
    }


    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    @SuppressLint("SetTextI18n")
    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                         Bundle savedInstanceState) {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //view = inflater.inflate(R.layout.fragment_confirm_delivery_address, container, false);
        setContentView(R.layout.fragment_confirm_delivery_address);

        context = this;
        objUtility = new Utility();

        mfFragmentClickCallback = this;

        mapFragment = findViewById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.onCreate(savedInstanceState);

        Functions.HideSoftKeyboard(this);

        fragmentManager = Objects.requireNonNull(this).getSupportFragmentManager();

        ivBack = findViewById(R.id.iv_back);
        tvFromCity = findViewById(R.id.tv_from_location_title);
        tvFromAddress = findViewById(R.id.tv_from_location_address);
        tvToCity = findViewById(R.id.tv_to_location_title);
        tvToAddress = findViewById(R.id.tv_to_location_address);
        btnConfirm = findViewById(R.id.btn_confirm);
        ivMarkerPickup = findViewById(R.id.iv_marker_pickup);
        ivMarkerDrop = findViewById(R.id.iv_marker_drop);
        linearFromLocation = findViewById(R.id.ll_from_location);
        linearToLocation = findViewById(R.id.ll_to_location);
        dottedView = findViewById(R.id.img_verical_line);
        ivFromIndicator = findViewById(R.id.ivFromIndicator);
        ivToIndicator = findViewById(R.id.ivToIndicator);

        deliveryDetails = new DeliveryDetails();

        dottedView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //Default Value
        isFromLocationClick = true;
        ivMarkerPickup.setVisibility(View.VISIBLE);

        //Bundle bundle = getArguments();
        Intent bundle = getIntent();
        if (bundle != null) {

            toTitle = bundle.getStringExtra("toTitle");
            toAddress = bundle.getStringExtra("toAddress");
            toLat = bundle.getDoubleExtra("toLat",0);
            toLng = bundle.getDoubleExtra("toLng",0);

            fromTitle = bundle.getStringExtra("fromTitle");
            fromAddress = bundle.getStringExtra("fromAddress");
            fromLat = bundle.getDoubleExtra("fromLat",0);
            fromLng = bundle.getDoubleExtra("fromLng",0);

            isDrawPath = bundle.getBooleanExtra("isDrawPath",false);
        }

        origin = new LatLng(fromLat, fromLng);
        destination = new LatLng(toLat, toLng);

        tvFromCity.setText("From: ");
        tvFromAddress.setText(fromAddress);

        tvToCity.setText("To: ");
        tvToAddress.setText(toAddress);

        int markerSize = 60;

        srcMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_black);
        srcMarkerBitmap = scaleBitmap(srcMarkerBitmap, markerSize, markerSize);

        destMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_black);
        destMarkerBitmap = scaleBitmap(destMarkerBitmap, markerSize, markerSize);

        //METHOD_setupMap();
        METHOD_init_views();

        if (isDrawPath) {
            isConfirm = true;
            btnConfirm.setText("Delivery Details");
            requestDirection();
        } else {
            btnConfirm.setText("Confirm");
            //if(pickupLatlong!=null&&dropoffLatlng!=null)
            //{
                confirmProcess();
            //}
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    return;
                }
            }
        });

        btnConfirm.setOnClickListener(v -> {

            confirmProcess();

        });

        linearFromLocation.setOnClickListener(view -> {
            isFromLocationClick = true;
            isToLocationClick = false;
            ivMarkerPickup.setVisibility(View.VISIBLE);
            ivMarkerDrop.setVisibility(View.GONE);
            ivFromIndicator.setVisibility(View.VISIBLE);
            ivToIndicator.setVisibility(View.GONE);
            MoveToSpecifiedLocation();
        });

        linearToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isToLocationClick = true;
                isFromLocationClick = false;
                ivMarkerPickup.setVisibility(View.GONE);
                ivMarkerDrop.setVisibility(View.VISIBLE);
                ivFromIndicator.setVisibility(View.GONE);
                ivToIndicator.setVisibility(View.VISIBLE);
                MoveToSpecifiedLocation();
            }
        });
       // return view;
    }

    private void confirmProcess()
    {
        if (fromAddress != null) {

            if (toAddress != null) {

                if ((fromLat.equals(toLat) && fromLng.equals(toLng)) || (fromAddress.equalsIgnoreCase(toAddress))) {
                    Toast.makeText(context, "From and To address can't be same.", Toast.LENGTH_SHORT).show();
                    return;
                }

                deliveryDetails.setFromLatitude(fromLat);
                deliveryDetails.setFromLongitude(fromLng);
                deliveryDetails.setFromAddress(fromAddress);

                deliveryDetails.setToLatitude(toLat);
                deliveryDetails.setToLongitude(toLng);
                deliveryDetails.setToAddress(toAddress);

                CheckoutNewF fragments = new CheckoutNewF();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle arguments = new Bundle();
                arguments.putParcelable("deliveryDetails", deliveryDetails);
                fragments.setArguments(arguments);
                ft.add(R.id.fl_id, fragments, "CheckoutNewF").addToBackStack("CheckoutNewF").commit();

            } else {
                Toast.makeText(this, "Please set to location.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please set from location.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapFragment.onResume();
        mMap = googleMap;
        mapWorker = new MapWorker(context, mMap);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            boolean success;

            if (preferences.getKeyIsNightMode()) {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.dark_map));
            } else {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.gray_map));
            }

            if (!success) {
                Log.e(Variables.tag, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(Variables.tag, "Can't find style. Error: ", e);
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng centerOfMapPos = mMap.getCameraPosition().target;

                if (centerOfMapPos != null) {
                    addMarker(centerOfMapPos, dropoffMarker);

                    if (!isConfirm) {

                        if (isFromLocationClick)//From Location New Condition
                        {
                            String frmAdd = objUtility.getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);
                            String frmTitle = objUtility.getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);

                            fromLat = centerOfMapPos.latitude;
                            fromLng = centerOfMapPos.longitude;
                            fromAddress = objUtility.getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);

                            if (!frmAdd.equalsIgnoreCase("")) {
                                tvFromCity.setText("From: ");
                            }

                            if (!frmTitle.equalsIgnoreCase("")) {
                                tvFromAddress.setText(frmAdd);
                            }
                            if (centerOfMapPos != null)
                                origin = new LatLng(centerOfMapPos.latitude, centerOfMapPos.longitude);

                        } else if (isToLocationClick) { //New code Dt-01-06-2021

                            String toAdd = objUtility.getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);
                            String toTitle = objUtility.getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);

                            toLat = centerOfMapPos.latitude;
                            toLng = centerOfMapPos.longitude;
                            toAddress = objUtility.getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);

                            if (!toAdd.equalsIgnoreCase("")) {
                                tvToCity.setText("To: ");
                            }

                            if (!toTitle.equalsIgnoreCase("")) {
                                tvToAddress.setText(toAdd);
                            }
                            if (centerOfMapPos != null)
                                destination = new LatLng(centerOfMapPos.latitude, centerOfMapPos.longitude);
                        }
                    }
                }
            }
        });

        mMap.setOnCameraMoveStartedListener(i -> {
            //mMap.clear();
            //LatLng centerOfMapPos = mMap.getCameraPosition().target;
            //isClickAddressItem = true;
        });

        if (ActivityCompat.checkSelfPermission(ConfirmDeliveryAddressA.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ConfirmDeliveryAddressA.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        Zoom_to_Current_location();
    }

    private void configureCameraIdle() {

        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCameraIdle() {


            }
        };
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
            if (ActivityCompat.checkSelfPermission(ConfirmDeliveryAddressA.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ConfirmDeliveryAddressA.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        mMap.setMyLocationEnabled(true);

        if (data == null)
            Zoom_to_Current_location();
    }

    private void Zoom_to_Current_location() {

        if (isFromLocationClick) {
            latLng = origin;
            if (latLng != null && latLng.latitude != 0.0) {

                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            } else {
                double lat = Double.parseDouble(preferences.getKeyUserLat());
                double lng = Double.parseDouble(preferences.getKeyUserLng());
                latLng = new LatLng(lat, lng);
                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }

        /*else if (!isToLocationClick) {
            latLng = destination;

            if (latLng != null) {
                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }

        } else {
            if ((lat != 0.0 && lng != 0.0)) {
                latLng = new LatLng(lat, lng);
                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }*/

        /*double lat = Double.parseDouble(preferences.getKeyUserLat());
        double lng = Double.parseDouble(preferences.getKeyUserLng());
        if ((lat != 0.0 && lng != 0.0)) {
            latLng = new LatLng(lat, lng);
            pickupLatlong = latLng;
            dropoffLatlng = latLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        }*/
    }

    private void MoveToSpecifiedLocation() {
        if (isFromLocationClick) {
            latLng = origin;
            if (latLng != null && latLng.latitude != 0.0) {

                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            } else {
                double lat = Double.parseDouble(preferences.getKeyUserLat());
                double lng = Double.parseDouble(preferences.getKeyUserLng());
                latLng = new LatLng(lat, lng);
                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }

        } else if (isToLocationClick) {
            latLng = destination;
            if (latLng != null && latLng.latitude != 0.0) {
                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            } else {
                latLng = origin;
                pickupLatlong = latLng;
                dropoffLatlng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }

        }
    }

    private void METHOD_init_views() {

        preferences = new Preferences(ConfirmDeliveryAddressA.this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_logout: {
                final Dialog dialog = new Dialog(context);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.show_defult_two_btn_alert_popup_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                final TextView txt_yes, txt_no, txt_title, txt_message;
                txt_title = dialog.findViewById(R.id.defult_alert_txt_title);
                txt_message = dialog.findViewById(R.id.defult_alert_txt_message);
                txt_no = dialog.findViewById(R.id.defult_alert_btn_cancel_no);
                txt_yes = dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

                txt_title.setText("" + getString(R.string.logout_status));
                txt_message.setText("" + getString(R.string.are_you_sure));
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

        Functions.ShowProgressLoader(ConfirmDeliveryAddressA.this, false, false);
        ApiRequest.CallApi(ConfirmDeliveryAddressA.this, ApiUrl.logout, sendobj, new Callback() {
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
                        Intent logout_intent = new Intent(ConfirmDeliveryAddressA.this, GetStartedA.class);
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
        //f.show(getChildFragmentManager(), "AddPackageSizeSelection_BottomSheet");
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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            new GpsUtils(ConfirmDeliveryAddressA.this).turnGPSOn(isGPSEnable -> {

                if (ContextCompat.checkSelfPermission(ConfirmDeliveryAddressA.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                Variables.permissionLocation);
                    }
                }

            });
        } else if (ContextCompat.checkSelfPermission(ConfirmDeliveryAddressA.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SetStatusBarColor(ContextCompat.getColor(ConfirmDeliveryAddressA.this, R.color.newColorWhite));
            accessLocationLayoutPinview.setVisibility(View.VISIBLE);
            accessLocationLayoutView.setVisibility(View.VISIBLE);
            GetCurrentLocation();

        } else {
            SetStatusBarColor(ContextCompat.getColor(ConfirmDeliveryAddressA.this, R.color.newColorPrimary));
            noLocationLayoutView.setVisibility(View.VISIBLE);
            accessLocationLayoutPinview.setVisibility(View.GONE);
            accessLocationLayoutView.setVisibility(View.GONE);
        }
    }

    //    phone defult navigation bar color handle
    private void SetStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(color);
        }
    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

            mFusedLocationClient.getLastLocation().addOnSuccessListener(ConfirmDeliveryAddressA.this, new OnSuccessListener<Location>() {
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
                        mapFragment.getMapAsync(ConfirmDeliveryAddressA.this);
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
                SetStatusBarColor(ContextCompat.getColor(ConfirmDeliveryAddressA.this, R.color.newColorWhite));
                noLocationLayoutView.setVisibility(View.GONE);
                accessLocationLayoutPinview.setVisibility(View.VISIBLE);
                accessLocationLayoutView.setVisibility(View.VISIBLE);
                enable_permission();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                SetStatusBarColor(ContextCompat.getColor(ConfirmDeliveryAddressA.this, R.color.newColorPrimary));
                noLocationLayoutView.setVisibility(View.VISIBLE);
                accessLocationLayoutPinview.setVisibility(View.GONE);
                accessLocationLayoutView.setVisibility(View.GONE);
            }
        }
    }

    //    init map by custom classes smoothly
    private void METHOD_setupMap() {

        MapsInitializer.initialize(context);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

        configureCameraIdle();

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
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.fl_id, frag, "Profile_F").addToBackStack("Profile_F").commit();
                break;

            case R.id.delivery:
                MainF fragm = new MainF();
                FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
                ftt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ftt.replace(R.id.fl_id, fragm, "Main_F").addToBackStack("Main_F").commit();
                break;

            case R.id.history:
                HistoryF fragments = new HistoryF();
                FragmentTransaction fttt = getSupportFragmentManager().beginTransaction();
                fttt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fttt.replace(R.id.fl_id, fragments, "History_F").addToBackStack("History_F").commit();
                break;
        }
        return true;
    }

    @Override
    public void OnItemClick(int postion, Bundle bundle) {

    }

    public void requestDirection() {

        btnConfirm.setText("Delivery Details");
        ivMarkerPickup.setVisibility(View.GONE);
        ivMarkerDrop.setVisibility(View.GONE);

        //  float totalDistance = distance(origin.latitude, origin.longitude, destination.latitude, destination.longitude);

        if (destination != null) {
            GoogleDirection.withServerKey(getString(R.string.map_direction_key)).from(origin).to(destination).transportMode(TransportMode.DRIVING).execute(this);
        }


    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {


            Route route = direction.getRouteList().get(0);

            Marker srcMarker = mMap.addMarker(new MarkerOptions().position(origin).flat(true).icon(BitmapDescriptorFactory.fromBitmap(srcMarkerBitmap)));
            srcMarker.setAnchor(0.5f, 0.5f);

            Marker destiMarker = mMap.addMarker(new MarkerOptions().position(destination).flat(true).icon(BitmapDescriptorFactory.fromBitmap(destMarkerBitmap)));
            destiMarker.setAnchor(0.5f, 0.5f);

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();


            mMap.addPolyline(DirectionConverter.createPolyline(Objects.requireNonNull(ConfirmDeliveryAddressA.this), directionPositionList, 5, Color.parseColor("#c20caa")));

            setCameraWithCoordinationBounds(route);

        }
    }

    private void setCameraWithCoordinationBounds(Route route) {

        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}