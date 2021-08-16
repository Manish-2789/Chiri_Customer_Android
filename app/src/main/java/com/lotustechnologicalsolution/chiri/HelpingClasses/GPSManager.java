package com.lotustechnologicalsolution.chiri.HelpingClasses;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.lotustechnologicalsolution.chiri.Interfaces.GPSLocationListener;

import static com.lotustechnologicalsolution.chiri.HelpingClasses.Variables.REQUEST_CODE_GPS_ENABLE;

public class GPSManager {

    private static final String TAG = GPSManager.class.getSimpleName();

    private Activity activity;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    private LocationListener locationListener;

    public GPSManager(Activity activity) {
        this.activity = activity;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        mSettingsClient = LocationServices.getSettingsClient(activity);
    }

    public void start(GPSLocationListener gpsLocationListener) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000 / 2);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        settingsBuilder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(task -> {

            try {

                LocationSettingsResponse response = task.getResult(ApiException.class);

                response.getLocationSettingsStates();

                if (gpsLocationListener != null)
                    gpsLocationListener.onGPSAlreadyEnabled();

            } catch (ApiException e) {

                switch (e.getStatusCode()) {

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(activity, REQUEST_CODE_GPS_ENABLE);
                        } catch (IntentSender.SendIntentException ex) {
                            Log.e(TAG, "InternalCatch: " + ex.getLocalizedMessage());
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
                Log.e(TAG, "ExternalCache: " + e.getLocalizedMessage());
            }
        });
    }

    /*public void start(onGpsListener onGpsListener) {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (onGpsListener != null) {
                onGpsListener.gpsStatus(true);
            }
        } else {
            mSettingsClient
                    .checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            if (onGpsListener != null) {
                                onGpsListener.gpsStatus(true);
                            }
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            int statusCode = ((ApiException) e).getStatusCode();

                            switch (statusCode) {

                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(activity, REQUEST_CODE_GPS_ENABLE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i(TAG, "PendingIntent unable to execute request.");
                                    }
                                    break;

                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                    Log.e(TAG, errorMessage);
                                    Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }*/
}