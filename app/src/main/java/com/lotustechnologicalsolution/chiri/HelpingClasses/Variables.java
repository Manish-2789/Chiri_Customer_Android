package com.lotustechnologicalsolution.chiri.HelpingClasses;

import android.content.SharedPreferences;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {

    public static String tag = "parcel_log";
    public static float mapZoomLevel =17;
    public static final int permissionLocation =790;
    public static final int permissionCameraCode =786;
    public static final int permissionWriteData =788;
    public static final int permissionReadData =789;
    public static final int permissionGalleryCode =787;
    public static final int permissionRecordingAudio =790;
    public static final boolean isToastShow =true;

    public static final String privacyPolicy = "https://vssquareweb.firebaseapp.com/privacy.html";
    public static SharedPreferences downloadSharedPreferences;
    public static String downloadPref = "download_pref";
    public static String openedChatId = "null";
    public static String folderGoDelivery = Environment.getExternalStorageDirectory() + "/GoDeliver/";
    public static String folderGoDeliveryDCIM = Environment.getExternalStorageDirectory() + "/DCIM/GoDeliver/";
    public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.getDefault());
    public static SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.getDefault());

    public static final int CAMERA_PHOTO_REQUEST_CODE = 10050;
    public static final int GALLERY_PHOTO_REQUEST_CODE = 10051;
    public static final int REQUEST_CODE_GPS_ENABLE = 2001;

    //region Local Image Store Directory Path
    private static final String HOME_DIR = "/.GoDeliver";
    public static final String LOCAL_MEDIA_IMAGE_DIR = HOME_DIR + "/.Media/Images";
    public static final String LOCAL_MEDIA_VIDEO_DIR = HOME_DIR + "/.Media/Videos";
    //endregion

}
