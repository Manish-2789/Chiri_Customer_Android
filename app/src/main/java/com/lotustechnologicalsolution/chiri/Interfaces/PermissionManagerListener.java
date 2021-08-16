package com.lotustechnologicalsolution.chiri.Interfaces;

import java.util.ArrayList;

public interface PermissionManagerListener {
    void onSinglePermissionGranted(String permission);

    void onMultiplePermissionGranted(ArrayList<String> permissions);
}