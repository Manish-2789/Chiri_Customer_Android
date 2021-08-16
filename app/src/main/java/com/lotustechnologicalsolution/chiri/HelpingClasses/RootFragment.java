package com.lotustechnologicalsolution.chiri.HelpingClasses;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.lotustechnologicalsolution.chiri.Interfaces.OnBackPressListenerCallback;

import java.util.List;
import java.util.Locale;

public class RootFragment extends Fragment implements OnBackPressListenerCallback {

    @Override
    public boolean onBackPressed() {
        return new BackPressImplimentationCallback(this).onBackPressed();
    }

    public String getCompleteAddressString(double latitude, double longitude) {

        String strAdd = "";

        try {

            return MyGeocoder.getFromLocation(latitude, longitude, 1);

//            if (addresses != null && addresses.size() > 0) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder();
//
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                strAdd = strReturnedAddress.toString();
//
//                //address1 = strAdd;
//                //address2 = returnedAddress.getAdminArea() + ", " + returnedAddress.getCountryName();
//
//            } else {
//                Log.w("location address", "No Address returned!");
//            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Location address", "Cannot get Address!");
        }

        return strAdd;
    }

    public String getCityTitle(double latitude, double longitude) {

        String strTitle = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();


                strTitle = returnedAddress.getAdminArea();

                //address1 = strAdd;
                //address2 = returnedAddress.getAdminArea() + ", " + returnedAddress.getCountryName();

            } else {
                Log.w("location address", "No Address returned!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Location address", "Cannot get Address!");
        }

        return strTitle;
    }

}