package com.lotustechnologicalsolution.chiri.Interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface LocationUpdateServiceCallback {

    void onDataReceived(LatLng data);
}
