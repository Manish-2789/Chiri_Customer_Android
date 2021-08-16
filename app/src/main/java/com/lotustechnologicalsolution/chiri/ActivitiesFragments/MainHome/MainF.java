package com.lotustechnologicalsolution.chiri.ActivitiesFragments.MainHome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
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
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.EditProfile.UpdatePasswordF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderCreate.OrderCreateF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.PaymentMethod.PaymentMethodF;
import com.lotustechnologicalsolution.chiri.ActivitiesFragments.Setting.SettingF;
import com.lotustechnologicalsolution.chiri.BottomSheetDialogsFragments.AddAddressDetailBottomSheet;
import com.lotustechnologicalsolution.chiri.BottomSheetDialogsFragments.AddPackageSizeSelectionBottomSheet;
import com.lotustechnologicalsolution.chiri.BottomSheetDialogsFragments.DeliveryTypesBottomSheet;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiRequest;
import com.lotustechnologicalsolution.chiri.HelpingClasses.ApiUrl;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Functions;
import com.lotustechnologicalsolution.chiri.HelpingClasses.GPSManager;
import com.lotustechnologicalsolution.chiri.HelpingClasses.MyGeocoder;
import com.lotustechnologicalsolution.chiri.HelpingClasses.PermissionManager;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Preferences;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.HelpingClasses.Variables;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.GPSLocationListener;
import com.lotustechnologicalsolution.chiri.Interfaces.LocationUpdateServiceCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.PermissionManagerListener;
import com.lotustechnologicalsolution.chiri.MapClasses.GpsUtils;
import com.lotustechnologicalsolution.chiri.MapClasses.MapWorker;
import com.lotustechnologicalsolution.chiri.ModelClasses.ModesTypesModel;
import com.lotustechnologicalsolution.chiri.ModelClasses.PackagesSizeSelectionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.lotustechnologicalsolution.chiri.HelpingClasses.Variables.REQUEST_CODE_GPS_ENABLE;

public class MainF extends RootFragment implements View.OnClickListener,
        OnMapReadyCallback,
        LocationUpdateServiceCallback, BottomNavigationView.OnNavigationItemSelectedListener,
        FragmentClickCallback, PermissionManagerListener, GPSLocationListener {

    public static DrawerLayout drawer;
    public static String whichScreenOpen = "";
    public static FragmentClickCallback mfFragmentClickCallback;
    private final ArrayList<PackagesSizeSelectionModel> packagesList = new ArrayList<>();
    private final double EARTHRADIUS = 6366198;
    View noLocationLayoutView;
    RelativeLayout accessLocationLayoutPinview;
    CoordinatorLayout accessLocationLayoutView;
    RelativeLayout tabAddDropoff, rl_bottom, rl_current_location;
    boolean isLatLngBound = false;
    Preferences preferences;
    int costPerMile = 0;
    double totalCost = 0;
    double totalDistence = 0;
    BottomNavigationView bottomNavigationView;
    View.OnClickListener nav_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawer.openDrawer(GravityCompat.START);
        }
    };
    //     when map moving stop it pick address and pick latlng from that address smothly
    String locality = "";
    String locality_two = "";
    Boolean flag;
    String[] permissions = new String[2];
    private Context context;
    private FrameLayout fl;
    private RelativeLayout llMenu, llBack, new_delivery;
    private Button confirm_new_delivery;
    private FusedLocationProviderClient mFusedLocationClient;
    private RelativeLayout llDropoffAddress;
    private View viewLlDropoffAddress, sideviewLlDropoffAddress;
    private ImageView dropoffCircleId;
    private CardView cvSelectDelivery;
    private TextView tvDeliveryType, tvSelectDropoff, fname;
    private ImageView ivCurrentLoc;
    private LinearLayout llPickupLoc, llDropoffLoc;
    private TextView tvPickupAddressTitle, tvPickupAddress, tvDropoffAddress,
            tvDropoffAddressTitle, tv_pickup_address_title_top;
    private LinearLayout btnConfirmPickup, btnConfirmDropoff, btnStarted, btnCancel;
    private TextView txtConfirmDropoff, tv_dropoff_address_title_f, tv_pickup_address_title_f;
    private List<ModesTypesModel> deliveryTypesList;
    private ModesTypesModel selectedModel;
    private MapView mapFragment;
    private GoogleMap mMap;
    private LatLng latLng, pickupLatlong, dropoffLatlng;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private LocationCallback locationCallback;
    private MapWorker mapWorker;
    private Marker pickupMarker;
    private Marker dropoffMarker;
    private Bitmap pickUpMarker, dropOofMarker;
    private ImageView ivPickupMarker, ivDeliveryType;
    //Drawer Views
    private RelativeLayout llHome, llHistory, llPay, llProfile, llChangePassword, llSettings, llLogout;
    private TextView tvUsername, txtVersionCode;
    private CircleImageView ivProfile;
    private View view;
    private GPSManager gpsManager;

    public MainF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        context = getActivity();

        mfFragmentClickCallback = this;

        mapFragment = view.findViewById(R.id.map);

        METHOD_init_views();

        gpsManager = new GPSManager(Objects.requireNonNull(getActivity()));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        checkPermission();

        mapFragment.onCreate(savedInstanceState);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        //*************set delivery selected*****************
        bottomNavigationView.setSelectedItemId(R.id.delivery);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //METHOD_setupMap();

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

            boolean success;

            if (preferences.getKeyIsNightMode()) {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                Objects.requireNonNull(getContext()), R.raw.dark_map));
            } else {
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                Objects.requireNonNull(getContext()), R.raw.gray_map));
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
            mMap.clear();
            LatLng centerOfMapPos = mMap.getCameraPosition().target;
            addMarker(centerOfMapPos, pickupMarker);
            //getCompleteAddressString(centerOfMapPos.latitude, centerOfMapPos.longitude);
            //isClickAddressItem = true;
        });

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            Objects.requireNonNull(marker).setPosition(latLng);
            marker.setVisible(false);
        }
    }

    @Override
    public void onDataReceived(LatLng data) {
        if (mMap != null)
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        Objects.requireNonNull(mMap).setMyLocationEnabled(true);

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

    @SuppressLint("SetTextI18n")
    private void METHOD_init_views() {

        preferences = new Preferences(view.getContext());
        fl = view.findViewById(R.id.fl_id);
        fname = view.findViewById(R.id.name);

        fname.setText(preferences.getKeyUserFirstName());
        ivDeliveryType = view.findViewById(R.id.iv_delivery_type);
        noLocationLayoutView = view.findViewById(R.id.no_location_layout_view);
        accessLocationLayoutView = view.findViewById(R.id.Access_location_layout_view);
        accessLocationLayoutPinview = view.findViewById(R.id.Access_location_layout_pinview);
        llMenu = view.findViewById(R.id.ll_menu);
        btnStarted = view.findViewById(R.id.btn_Started);
        btnStarted.setOnClickListener(this);
        view.findViewById(R.id.iv_menu).setOnClickListener(nav_click_listener);
        new_delivery = view.findViewById(R.id.new_delivery);
        confirm_new_delivery = view.findViewById(R.id.confirm_new_delivery);
        confirm_new_delivery.setOnClickListener(this);
        llBack = view.findViewById(R.id.ll_back);
        llBack.setOnClickListener(this);
        llPickupLoc = view.findViewById(R.id.ll_pickup_loc);
        llPickupLoc.setOnClickListener(this);
        llDropoffLoc = view.findViewById(R.id.ll_dropoff_loc);
        llDropoffLoc.setOnClickListener(this);

        //view.findViewById(R.id.allow_access_location).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        llChangePassword = view.findViewById(R.id.ll_change_password);
        llChangePassword.setOnClickListener(this);

        llLogout = view.findViewById(R.id.ll_logout);
        llLogout.setOnClickListener(this);

        tvSelectDropoff = view.findViewById(R.id.tv_select_dropoff);

        txtVersionCode = view.findViewById(R.id.txt_main_home_version_code);
        cvSelectDelivery = view.findViewById(R.id.cv_select_delivery);
        cvSelectDelivery.setOnClickListener(this);
        cvSelectDelivery.setVisibility(View.VISIBLE);

        tvDeliveryType = view.findViewById(R.id.tv_delivery_type);
        ivCurrentLoc = view.findViewById(R.id.iv_current_location);
        ivCurrentLoc.setOnClickListener(this);

        deliveryTypesList = new ArrayList<>();
        CallApi_showDeliveryTypes();
        CallApi_showPackagesSizeSelection();

        tvPickupAddressTitle = view.findViewById(R.id.tv_pickup_address_title);
        tvPickupAddress = view.findViewById(R.id.tv_pickup_address);

        tv_pickup_address_title_top = view.findViewById(R.id.tv_pickup_address_title_top);
        rl_current_location = view.findViewById(R.id.rl_current_location);
        //tabAddDropoff = view.findViewById(R.id.tab_add_dropoff);
        tvDropoffAddressTitle = view.findViewById(R.id.tv_dropoff_address_title);
        tvDropoffAddress = view.findViewById(R.id.tv_dropoff_address);

        llDropoffAddress = view.findViewById(R.id.ll_dropoff_address);
        rl_bottom = view.findViewById(R.id.rl_bottom);
        viewLlDropoffAddress = view.findViewById(R.id.view_ll_dropoff_address);
        sideviewLlDropoffAddress = view.findViewById(R.id.sideview_ll_dropoff_address);
        llDropoffAddress.setVisibility(View.GONE);
        sideviewLlDropoffAddress.setVisibility(View.GONE);
        viewLlDropoffAddress.setVisibility(View.GONE);

        dropoffCircleId = view.findViewById(R.id.dropoff_circle_id);
        //dropoffCircleId.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_circle));
        //dropoffCircleId.setVisibility(View.INVISIBLE);
        btnConfirmPickup = view.findViewById(R.id.btn_confirm_pickup);
        btnConfirmPickup.setOnClickListener(this);
        btnConfirmDropoff = view.findViewById(R.id.btn_confirm_dropoff);
        txtConfirmDropoff = view.findViewById(R.id.txt_confirm_dropoff);
        tv_dropoff_address_title_f = view.findViewById(R.id.tv_dropoff_address_title_f);
        tv_pickup_address_title_f = view.findViewById(R.id.tv_pickup_address_title_f);
        btnConfirmDropoff.setOnClickListener(this);
        btnConfirmDropoff.setVisibility(View.GONE);
        ivPickupMarker = view.findViewById(R.id.iv_pickup_marker);
        ivPickupMarker.setVisibility(View.VISIBLE);

        ImageView iv_home = view.findViewById(R.id.iv_home);
        iv_home.setImageResource(R.drawable.ic_home);

        //Drawer Views
        drawer = view.findViewById(R.id.drawer);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Uri uri = Uri.parse(ApiUrl.baseUrl + preferences.getKeyUserImage());
                Glide.with(view.getContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_profile_gray)
                        .error(R.drawable.ic_profile_gray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(ivProfile);
                drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        view.findViewById(R.id.tab_profile).setOnClickListener(this);
        llHome = view.findViewById(R.id.ll_home);
        llHome.setOnClickListener(this);
//        llHistory = view.findViewById(R.id.ll_history);
//        llHistory.setOnClickListener(this);
        llPay = view.findViewById(R.id.ll_pay);
        llPay.setOnClickListener(this);
//        llProfile = view.findViewById(R.id.ll_profile);
//        llProfile.setOnClickListener(this);

        llSettings = view.findViewById(R.id.ll_settings);
        llSettings.setOnClickListener(this);

        view.findViewById(R.id.ll_logout).setOnClickListener(this);

        tvUsername = view.findViewById(R.id.tv_username);

        tvUsername.setText(preferences.getKeyUserName());

        ivProfile = view.findViewById(R.id.iv_profile);
        Uri uri = Uri.parse(ApiUrl.baseUrl + preferences.getKeyUserImage());
        Glide.with(view.getContext())
                .load(uri)
                .placeholder(R.drawable.ic_profile_gray)
                .error(R.drawable.ic_profile_gray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(ivProfile);
        if (preferences.getKeySocialType().equalsIgnoreCase("google") ||
                preferences.getKeySocialType().equalsIgnoreCase("facebook")) {
            llChangePassword.setVisibility(View.GONE);
        } else {
            llChangePassword.setVisibility(View.VISIBLE);
        }

        txtVersionCode.setText("" + getphoneVersion());
    }

    private String getphoneVersion() {
        try {
            return "v" + context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return "";
        }
    }

    private void CallApi_showDeliveryTypes() {
        ApiRequest.CallApi(getContext(), ApiUrl.showVehicleTypes, new JSONObject(), resp -> {
            if (resp != null) {
                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        JSONArray msgarray = respobj.getJSONArray("msg");
                        Log.d(Variables.tag, "Msg Array: " + msgarray);
                        METHOD_gettingDeliveryTypeslist(msgarray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CallApi_showPackagesSizeSelection() {
        ApiRequest.CallApi(getContext(), ApiUrl.showPackageSize, new JSONObject(), resp -> {
            if (resp != null) {
                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        JSONArray msgarray = respobj.getJSONArray("msg");
                        Log.d(Variables.tag, "Msg Array: " + msgarray);
                        METHOD_gettingPackagesSizeSelectionList(msgarray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void METHOD_gettingPackagesSizeSelectionList(JSONArray msgarray) {
        packagesList.clear();
        for (int i = 0; i < msgarray.length(); i++) {

            try {
                JSONObject obj = msgarray.getJSONObject(i).getJSONObject("PackageSize");

                PackagesSizeSelectionModel model = new PackagesSizeSelectionModel();
                model.setId(obj.optString("id"));
                model.setTitle(obj.optString("title"));
                model.setDescription(obj.optString("description"));
                model.setImage("" + ApiUrl.baseUrl + obj.optString("image"));
                model.setPrice(obj.optString("price"));
                model.setSelected(i == 0);
                packagesList.add(model);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void METHOD_gettingDeliveryTypeslist(JSONArray msgarray) {
        deliveryTypesList.clear();
        for (int i = 0; i < msgarray.length(); i++) {

            try {
                JSONObject obj = msgarray.getJSONObject(i).getJSONObject("VehicleType");

                ModesTypesModel model = new ModesTypesModel();
                model.setId(obj.optString("id"));
                model.setVehicleName(obj.optString("name"));
                model.setVehicleDesc(obj.optString("description"));
                model.setChargesPerKM(obj.optString("per_km_mile_charge"));
                model.setVehicleImage(ApiUrl.baseUrl + obj.optString("image"));

                deliveryTypesList.add(model);

                if (i == 0) {
                    tvDeliveryType.setText("" + model.getVehicleName());

                    costPerMile = Integer.parseInt(model.getChargesPerKM());
                    selectedModel = model;

                    Glide.with(view.getContext())
                            .load("" + model.getVehicleImage())
                            .placeholder(R.drawable.ic_delivery_car)
                            .error(R.drawable.ic_delivery_car)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(ivDeliveryType);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_back:
                if (btnConfirmDropoff.getVisibility() == View.VISIBLE)
                    METHOD_backfromConfirm_Dropoff();
                else if (btnStarted.getVisibility() == View.VISIBLE)
                    METHOD_backfromStarted();
                break;

            case R.id.ll_pickup_loc:
                METHOD_selectLocation(view.getContext().getString(R.string.pickup_location), tvPickupAddressTitle.getText().toString(), tvPickupAddressTitle);
                break;

            case R.id.ll_dropoff_loc:
                METHOD_selectLocation(view.getContext().getString(R.string.dropoff_location), tvDropoffAddressTitle.getText().toString(), tvPickupAddressTitle);
                break;

            case R.id.cv_select_delivery:
              //  METHOD_showDeliveryTypes_Bts();
                break;

            case R.id.btn_confirm_pickup:
                METHOD_openAdd_Address_Detail_Bts_F(true);
                break;

            case R.id.btn_confirm_dropoff:
                METHOD_openAdd_Address_Detail_Bts_F(false);
                break;

            case R.id.confirm_new_delivery:
                //METHOD_openAdd_Address_Detail_Bts_F(false);
                break;

            case R.id.btn_Started:
                METHOD_openAdd_Packages_Size_Selection_F();
                break;

            case R.id.iv_current_location:
                if (latLng == null) {
                    GetCurrentLocation();
                    Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.need_to_select_your_pickup_poin));
                    return;
                }
                if (isLatLngBound)
                    ShowLatLngBoundZoom();
                else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                break;

            case R.id.btn_cancel:
                checkPermission();
                break;

//                no location layout
            /*case R.id.allow_access_location: {
                if (statusCheck()) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            Variables.permissionLocation);
                } else
                    enable_permission();
            }
            break;*/
//            Drawer View
//            case R.id.ll_history:
//                drawer.closeDrawer(GravityCompat.START);
//                Functions.OpenNavFragment(new HistoryF(nav_click_listener), getChildFragmentManager(),"History_F", fl);
//                break;
            case R.id.ll_home:
                drawer.closeDrawer(GravityCompat.START);
                Functions.clearFragmentByTag(getChildFragmentManager());
                break;

            case R.id.ll_pay:
                drawer.closeDrawer(GravityCompat.START);
                Functions.OpenNavFragment(new PaymentMethodF(nav_click_listener), getChildFragmentManager(), "PaymentMethod_F", fl);
                break;

//            case R.id.ll_profile:
//            {
//                drawer.closeDrawer(GravityCompat.START);
//                Functions.OpenNavFragment(new ProfileF(nav_click_listener,new FragmentClickCallback() {
//                    @Override
//                    public void OnItemClick(int postion, Bundle bundle) {
//                        tvUsername.setText(preferences.getKeyUserName());
//
//                        Uri uri = Uri.parse(ApiUrl.baseUrl +preferences.getKeyUserImage());
//                        Glide.with(view.getContext())
//                                .load(uri)
//                                .placeholder(R.drawable.ic_profile_gray)
//                                .error(R.drawable.ic_profile_gray)
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .dontAnimate()
//                                .into(ivProfile);
//                    }
//                }), getChildFragmentManager(),"Profile_F", fl);
//
//            }
//            break;

            case R.id.tab_profile: {
                drawer.closeDrawer(GravityCompat.START);
                Functions.OpenNavFragment(new ProfileF(nav_click_listener, (position, bundle) -> {
                    tvUsername.setText(preferences.getKeyUserName());

                    Uri uri = Uri.parse(ApiUrl.baseUrl + preferences.getKeyUserImage());
                    Glide.with(view.getContext())
                            .load(uri)
                            .placeholder(R.drawable.ic_profile_gray)
                            .error(R.drawable.ic_profile_gray)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(ivProfile);
                }), getChildFragmentManager(), "Profile_F", fl);

            }
            break;

            case R.id.ll_change_password:
                drawer.closeDrawer(GravityCompat.START);
                Functions.OpenNavFragment(new UpdatePasswordF(nav_click_listener), getChildFragmentManager(), "ChangePass_F", fl);
                break;

            case R.id.ll_settings:
                drawer.closeDrawer(GravityCompat.START);
                Functions.OpenNavFragment(new SettingF(nav_click_listener), getChildFragmentManager(), "Setting_F", fl);
                break;

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

                txt_title.setText("" + view.getContext().getString(R.string.logout_status));
                txt_message.setText("" + view.getContext().getString(R.string.are_you_sure));
                txt_yes.setOnClickListener(view -> {
                    dialog.dismiss();
                    LogoutByThisUserId();
                });
                txt_no.setOnClickListener(view -> dialog.dismiss());
                dialog.show();
            }
            break;
        }
    }

    private void METHOD_selectLocation(String title, String query, TextView tvPickupAddressTitle) {

        if (ivPickupMarker.getVisibility() == View.VISIBLE) {

            /*Fragment f = new SearchLocationA(query, title, tvPickupAddressTitle, (position, bundle) -> {

                if (title.equalsIgnoreCase(view.getContext().getString(R.string.pickup_location))) {

                    pickupLatlong = new LatLng(bundle.getDouble("lat", 0.0), bundle.getDouble("lng", 0.0));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatlong, 16));
                    //WhatIsYourEmail fragment = new WhatIsYourEmail();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack("WhatsIsYourEmail");
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    transaction.replace(R.id.fl_id, fragment, "WhatsIsYourEmail").commit();
                } else {

                    dropoffLatlng = new LatLng(bundle.getDouble("lat", 0.0), bundle.getDouble("lng", 0.0));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dropoffLatlng, 16));

                }
            });

            f.setArguments(arguments);*/ //commented after make activity

            try {
                /*SearchLocationA searchLocationF = new SearchLocationA();
                Bundle arguments = new Bundle();
                arguments.putParcelable("pickup", pickupLatlong);
                arguments.putString("pickupaddress", getCompleteAddressString(pickupLatlong.latitude, pickupLatlong.longitude));
                arguments.putString("pickuptitle", getCompleteAddressString(pickupLatlong.latitude, pickupLatlong.longitude));
                searchLocationF.setArguments(arguments);

                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.addToBackStack("SearchLocation_F");
                ft.replace(R.id.fl_id, searchLocationF, "SearchLocation_F").addToBackStack("SearchLocation_F").commit();*/
                Intent intent = new Intent(getActivity(),SearchLocationA.class);
                intent.putExtra("pickup", pickupLatlong);
                intent.putExtra("pickupaddress", getCompleteAddressString(pickupLatlong.latitude, pickupLatlong.longitude));
                intent.putExtra("pickuptitle", getCompleteAddressString(pickupLatlong.latitude, pickupLatlong.longitude));
                startActivity(intent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

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
        ApiRequest.CallApi(view.getContext(), ApiUrl.logout, sendobj, resp -> {
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
        });
    }

    //    apply zoom on map according to pin bounds of map
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

    //Show custom pin on map
    private Bitmap getMarkerPickupPinView() {
        @SuppressLint("InflateParams") View customMarkerView = ((LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_pickup_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerDropOffPinView() {
        @SuppressLint("InflateParams") View customMarkerView = ((LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_dropoff_marker, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.profile_image);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private void METHOD_showDeliveryTypes_Bts() {
        DeliveryTypesBottomSheet f = new DeliveryTypesBottomSheet(deliveryTypesList, "",
                (position, bundle) -> {
                    ModesTypesModel model = (ModesTypesModel)
                            bundle.getSerializable("obj");

                    tvDeliveryType.setText(model.getVehicleName());
                    costPerMile = Integer.parseInt(model.getChargesPerKM());
                    selectedModel = model;

                    Glide.with(view.getContext())
                            .load("" + model.getVehicleImage())
                            .placeholder(R.drawable.ic_delivery_car)
                            .error(R.drawable.ic_delivery_car)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(ivDeliveryType);
                });
        f.show(getChildFragmentManager(), "");
    }

    private void METHOD_openAdd_Packages_Size_Selection_F() {
        AddPackageSizeSelectionBottomSheet f = new AddPackageSizeSelectionBottomSheet(packagesList, totalCost, (postion, bundle) -> {
            PackagesSizeSelectionModel model = (PackagesSizeSelectionModel) bundle.getSerializable("DataModel");
            METHOD_openFragment(model);
        });
        f.show(getChildFragmentManager(), "AddPackageSizeSelection_BottomSheet");
    }

    public void METHOD_openAdd_Address_Detail_Bts_F(Boolean is_pickup_details) {
        AddAddressDetailBottomSheet f = new AddAddressDetailBottomSheet(is_pickup_details, (position, bundle) -> {

            String address_details;

            if (is_pickup_details) {
                whichScreenOpen = "dropoff";

                ivPickupMarker.setVisibility(View.VISIBLE);
                viewLlDropoffAddress.setVisibility(View.INVISIBLE);
                sideviewLlDropoffAddress.setVisibility(View.INVISIBLE);
                llDropoffAddress.setVisibility(View.VISIBLE);
                cvSelectDelivery.setVisibility(View.GONE);

                tvSelectDropoff.setText(getResources().getString(R.string.select_dropoff_location));
                //dropoffCircleId.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_circle_fill));
                //dropoffCircleId.setVisibility(View.INVISIBLE);

                ivCurrentLoc.setVisibility(View.VISIBLE);
                btnConfirmPickup.setVisibility(View.GONE);
                btnConfirmDropoff.setVisibility(View.GONE);

                llMenu.setVisibility(View.VISIBLE);
                llBack.setVisibility(View.GONE);

                if (bundle != null) {
                    address_details = "" + bundle.getString("details");
                    preferences.setKeyPickupDetail(address_details);
                }

                if (pickupMarker == null) {
                    if (pickupLatlong == null || pickUpMarker == null) {
                        return;
                    }
                    //pickupMarker = mapWorker.add_marker(pickupLatlong, pickUpMarker);
                } else {
                    pickupMarker.remove();
                    pickupMarker = mapWorker.add_marker(pickupLatlong, pickUpMarker);
                }

            } else {
                if (dropoffMarker == null) {
                    if (dropoffLatlng == null || dropOofMarker == null) {
                        return;
                    }
                } else {
                    dropoffMarker.remove();
                }
                dropoffMarker = mapWorker.add_marker(dropoffLatlng, dropOofMarker);

                btnStarted.setEnabled(false);
                btnStarted.setAlpha(0.8f);
                drawRoute(pickupLatlong, dropoffLatlng, (position1, bundle1) -> {
                    if (bundle1.getBoolean("isDone", false)) {
                        btnStarted.setEnabled(true);
                        btnStarted.setAlpha(1f);
                        totalDistence = bundle1.getDouble("distance", 0.0);

                        totalCost = costPerMile * totalDistence;

                        preferences.setKeyPickupLat("" + pickupLatlong.latitude);
                        preferences.setKeyPickupLng("" + pickupLatlong.longitude);
                        preferences.setKeyDropoffLat("" + dropoffLatlng.latitude);
                        preferences.setKeyDropoffLng("" + dropoffLatlng.longitude);

                        if (totalDistence == 0) {
                            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.pickup_dropoff_location_must_be_different));
                            return;
                        }
                    }
                });

                whichScreenOpen = "started";

                ivCurrentLoc.setVisibility(View.VISIBLE);
                //ivDropoffMarker.setVisibility(View.GONE);
                btnConfirmDropoff.setVisibility(View.GONE);

                tvSelectDropoff.setText(getResources().getString(R.string.get_started));
                btnStarted.setVisibility(View.VISIBLE);
                //btnStarted.performClick();

                if (bundle != null) {
                    address_details = "" + bundle.getString("details");
                    preferences.setKeyDropoffDetail("" + address_details);
                }
            }
        });
        f.show(getChildFragmentManager(), "");
    }

    private void METHOD_openFragment(PackagesSizeSelectionModel model) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("Delivery_model", selectedModel);
        bundle.putSerializable("Delivery_Type_List", (Serializable) deliveryTypesList);
        bundle.putSerializable("package_model", model);
        bundle.putDouble("total_cost", totalCost);
        bundle.putDouble("total_distence", totalDistence);
        OrderCreateF f = new OrderCreateF();
        f.setArguments(bundle);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.addToBackStack("OrderCreate_F");
        ft.replace(R.id.fl_id, f, "OrderCreate_F").addToBackStack(null).commit();
    }

    private void configureCameraIdle() {

        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                LatLng latLng = mMap.getCameraPosition().target;

                tv_pickup_address_title_top.setText(MainF.this.getCompleteAddressString(latLng.latitude, latLng.longitude));

                if (btnConfirmDropoff.getVisibility() == View.GONE && btnStarted.getVisibility() == View.GONE) {
                    flag = false;
                    Log.d(Variables.tag, "Pickup");
                    pickupLatlong = latLng;

                } else {

                    Log.d(Variables.tag, "Dropoff");
                    dropoffLatlng = latLng;

                    txtConfirmDropoff.setText(R.string.confirm_dropoff);

                    //tabAddDropoff.setVisibility(View.GONE);

                    tvDropoffAddressTitle.setVisibility(View.VISIBLE);
                    tvDropoffAddress.setVisibility(View.VISIBLE);
                }

                String fullAddress = MyGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (btnConfirmPickup.getVisibility() == View.VISIBLE) {
                    tv_pickup_address_title_f.setText(preferences.getKeyPickupAdress());
                    new_delivery.setVisibility(View.GONE);
                    tv_pickup_address_title_top.setText(fullAddress);
                    tvPickupAddressTitle.setText(locality_two);
                    tvPickupAddress.setText(fullAddress);
                    preferences.setKeyPickupAdress(fullAddress);
                    btnConfirmPickup.setVisibility(View.GONE);
                    MainF.this.METHOD_openAdd_Address_Detail_Bts_F(true);
                } else if (btnConfirmDropoff.getVisibility() == View.VISIBLE) {
                    tvDropoffAddressTitle.setText(locality_two);
                    tvDropoffAddress.setText(fullAddress);
                    preferences.setKeyDropoffAdress(fullAddress);

                        /*btnConfirmDropoff.setVisibility(View.GONE);
                        tv_dropoff_address_title_f.setText(preferences.getKeyDropoffAdress());
                        new_delivery.setVisibility(View.VISIBLE);
                        tvSelectDropoff.setText(getResources().getString(R.string.select_dropoff_location));
                        llBack.setVisibility(View.VISIBLE);
                        rl_current_location.setVisibility(View.GONE);
                        rl_bottom.setVisibility(View.GONE);
                        tvDropoffAddressTitle.setText(locality_two);
                        tvDropoffAddress.setText(locality);
                        preferences.setKeyDropoffAdress(locality);*/
                }
            }
        };
    }

    //    draw poliline route between points
    private void drawRoute(LatLng pickup, LatLng dropoff, FragmentClickCallback fragment_clickCallback) {

        if (pickup != null && dropoff != null) {
            mapWorker.DrawRoute(view.getContext(), pickup, dropoff, mMap, (position, bundle) -> {
                if (bundle.getBoolean("isDone", false)) {
                    fragment_clickCallback.OnItemClick(position, bundle);
                }
            });
            ShowLatLngBoundZoom();
            isLatLngBound = true;

        } else {
            Log.d(Variables.tag, "Latlng Missing .. !!");
        }
    }

    public void METHOD_backfromConfirm_Dropoff() {
        ivPickupMarker.setVisibility(View.VISIBLE);
        llDropoffAddress.setVisibility(View.GONE);
        viewLlDropoffAddress.setVisibility(View.GONE);
        sideviewLlDropoffAddress.setVisibility(View.GONE);
        btnConfirmDropoff.setVisibility(View.GONE);
        cvSelectDelivery.setVisibility(View.VISIBLE);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatlong, 16));

        btnConfirmPickup.setVisibility(View.VISIBLE);

        llMenu.setVisibility(View.VISIBLE);
        llBack.setVisibility(View.GONE);

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
    }

    public void METHOD_backfromStarted() {
        whichScreenOpen = "dropoff";
        //ivDropoffMarker.setVisibility(View.VISIBLE);

        tvSelectDropoff.setText(getResources().getString(R.string.select_dropoff_location));
        mapWorker.Remove_polyline_with_animation();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dropoffLatlng, 16));

        btnStarted.setVisibility(View.GONE);
        btnConfirmDropoff.setVisibility(View.VISIBLE);

        if (dropoffMarker != null) {
            isLatLngBound = false;
            dropoffMarker.remove();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // enable_permission();
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
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {
            new GpsUtils(Objects.requireNonNull(getContext())).turnGPSOn(isGPSEnable -> {

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
            noLocationLayoutView.setVisibility(View.GONE);
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

    //    phone default navigation bar color handle
    private void SetStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(color);
        }
    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //   get current location by using phone GPS
    @SuppressLint("MissingPermission")
    private void GetCurrentLocation() {
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(Objects.requireNonNull(getActivity()), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(@NonNull Location location) {
                    // Got last known location. In some rare situations this can be null.
                    try {
                        if (location != null) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            double lat = (latLng.latitude);
                            double lon = (latLng.longitude);

                            preferences.setKeyUserLat("" + lat);
                            preferences.setKeyUserLng("" + lon);
                        }

                        mapFragment.onResume();
                        mapFragment.getMapAsync(MainF.this);
                        MainF.this.METHOD_setupMap();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
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

    //    init map by custom classes smoothly
    private void METHOD_setupMap() {

        MapsInitializer.initialize(context);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

        pickUpMarker = getMarkerPickupPinView();

        pickUpMarker = Bitmap.createScaledBitmap(((BitmapDrawable) context.getResources()
                        .getDrawable(R.drawable.ic_marker_pin)).getBitmap(),
                Functions.convertDpToPx(context, 40),
                Functions.convertDpToPx(context, 40), false);

        configureCameraIdle();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.profile:
                /*ProfileF frag = new ProfileF();
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.addToBackStack("Profile_F");
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.fl_id, frag, "Profile_F").commit();*/

               break;
            case R.id.delivery:
                MainF fragm = new MainF();
                FragmentTransaction ftt = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ftt.addToBackStack("Main_F");
                ftt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ftt.replace(R.id.fl_id, fragm, "Main_F").commit();
                break;

            case R.id.history:
                /*HistoryF fragments = new HistoryF();
                FragmentTransaction fttt = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                fttt.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                fttt.addToBackStack("History_F");
                fttt.replace(R.id.fl_id, fragments, "History_F").commit();*/
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case REQUEST_CODE_GPS_ENABLE:

                switch (resultCode) {

                    case RESULT_OK:
                        SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorWhite));
                        noLocationLayoutView.setVisibility(View.GONE);
                        accessLocationLayoutPinview.setVisibility(View.VISIBLE);
                        accessLocationLayoutView.setVisibility(View.VISIBLE);
                        GetCurrentLocation();
                        break;

                    case RESULT_CANCELED://Ahi aavya kare che cancel ke ok ma
                        gpsManager.start(this);
                        break;
                }
                break;
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnItemClick(int position, Bundle bundle) {
    }

    @Override
    public void onSinglePermissionGranted(String permission) {

    }

    @Override
    public void onMultiplePermissionGranted(ArrayList<String> permissions) {
        if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) || permissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            gpsManager.start(this);
        }
    }

    @Override
    public void onGPSAlreadyEnabled() {
        SetStatusBarColor(ContextCompat.getColor(view.getContext(), R.color.newColorWhite));
        noLocationLayoutView.setVisibility(View.GONE);
        accessLocationLayoutPinview.setVisibility(View.VISIBLE);
        accessLocationLayoutView.setVisibility(View.VISIBLE);
        GetCurrentLocation();
    }

    private void checkPermission() {
        permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;

        PermissionManager permissionManager = new PermissionManager(getActivity(), this);
        permissionManager.setMultiplePermission(permissions);
    }
}