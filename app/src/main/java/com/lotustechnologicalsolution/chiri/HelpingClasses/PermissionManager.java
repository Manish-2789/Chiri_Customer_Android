package com.lotustechnologicalsolution.chiri.HelpingClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lotustechnologicalsolution.chiri.Interfaces.PermissionManagerListener;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private Context mContext;
    private PermissionManagerListener listener;

    public PermissionManager(Context mContext) {
        this.mContext = mContext;
    }

    public PermissionManager(Context mContext, PermissionManagerListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public void setSinglePermission(String permission) {

        Dexter.withActivity((Activity) mContext)
                .withPermission(permission)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        if (listener == null) {
                            listener = (PermissionManagerListener) mContext;
                        }
                        listener.onSinglePermissionGranted(response.getPermissionName());
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> {
                })
                .check();
    }

    private void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            System.exit(0);
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        ((Activity) mContext).startActivityForResult(intent, 101);
    }

    public void setMultiplePermission(String[] permissions) {

        Dexter.withActivity((Activity) mContext)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            ArrayList<String> grantedPermission = new ArrayList<>();

                            for (int i = 0; i < report.getGrantedPermissionResponses().size(); i++)
                                grantedPermission.add(report.getGrantedPermissionResponses().get(i).getPermissionName());

                            if (listener == null)
                                listener = (PermissionManagerListener) mContext;

                            listener.onMultiplePermissionGranted(grantedPermission);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> {
                })
                .onSameThread()
                .check();
    }
}
