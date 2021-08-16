package com.lotustechnologicalsolution.chiri.utils;

import android.util.Log;

import com.lotustechnologicalsolution.chiri.HelpingClasses.MyGeocoder;

public class Utility {
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
}
