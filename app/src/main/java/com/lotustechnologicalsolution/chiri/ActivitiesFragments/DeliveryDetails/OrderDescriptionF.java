package com.lotustechnologicalsolution.chiri.ActivitiesFragments.DeliveryDetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.lotustechnologicalsolution.R;
import com.lotustechnologicalsolution.chiri.HelpingClasses.PermissionManager;
import com.lotustechnologicalsolution.chiri.HelpingClasses.RootFragment;
import com.lotustechnologicalsolution.chiri.Interfaces.FragmentClickCallback;
import com.lotustechnologicalsolution.chiri.Interfaces.PermissionManagerListener;
import com.lotustechnologicalsolution.chiri.ModelClasses.DeliveryDetails;
import com.lotustechnologicalsolution.databinding.FragmentOrderDescriptionBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.lotustechnologicalsolution.chiri.HelpingClasses.Variables.CAMERA_PHOTO_REQUEST_CODE;
import static com.lotustechnologicalsolution.chiri.HelpingClasses.Variables.GALLERY_PHOTO_REQUEST_CODE;
import static com.lotustechnologicalsolution.chiri.HelpingClasses.Variables.LOCAL_MEDIA_IMAGE_DIR;

public class OrderDescriptionF extends RootFragment
        implements View.OnClickListener, PermissionManagerListener {

    public static FragmentClickCallback mfFragmentClickCallback;
    public DeliveryDetails deliveryDetails;
    PermissionManager permissionManager;
    String mainImageFilepath = "";
    MEDIA_MODE mode;
    FragmentOrderDescriptionBinding binding;

    private Context context;
    private PackageManager packageManager;
    private FragmentManager fragmentManager;

    public OrderDescriptionF() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_description, container, false);

        context = getActivity();

        packageManager = Objects.requireNonNull(context).getPackageManager();
        permissionManager = new PermissionManager(context, this);
        fragmentManager = getActivity().getSupportFragmentManager();

        Bundle bundle = getArguments();
        if (bundle != null) {
            deliveryDetails = bundle.getParcelable("deliveryDetails");
        }

        if (deliveryDetails == null) {
            Toast.makeText(context, "Kindly choose pickup and delivery location.", Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStack();
        }

        binding.rlNext.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);
        binding.attachPhoto.setOnClickListener(this);

        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_next:
                deliveryDetails.setOrderDescription(binding.etOrderDescription.getText().toString());
                ReceiverDetailF fragments = new ReceiverDetailF();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.addToBackStack("ReceiverDetail_F");
                Bundle arguments = new Bundle();
                arguments.putParcelable("deliveryDetails", deliveryDetails);
                fragments.setArguments(arguments);
                ft.replace(R.id.fl_id, fragments, "ReceiverDetail_F").commit();
                break;

            case R.id.attach_photo:
                openImageOptionsDialog();
                break;

            case R.id.iv_back:
                fragmentManager.popBackStack();
                break;
        }
    }

    private void openImageOptionsDialog() {

        Dialog dialogFileOptions = new Dialog(getActivity());
        dialogFileOptions.setCancelable(true);
        dialogFileOptions.setContentView(R.layout.select_profile_picture_list_view);
        dialogFileOptions.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txt_take_photo = dialogFileOptions.findViewById(R.id.txt_take_photo);
        TextView txt_gallery = dialogFileOptions.findViewById(R.id.txt_gallery);
        TextView txt_cancel = dialogFileOptions.findViewById(R.id.txt_cancel);

        txt_take_photo.setOnClickListener(view -> {
            mode = MEDIA_MODE.CAMERA_MODE;
            checkPermission();
            dialogFileOptions.dismiss();
        });
        txt_gallery.setOnClickListener(view -> {
            mode = MEDIA_MODE.GALLERY_MODE;
            checkPermission();
            dialogFileOptions.dismiss();
        });
        txt_cancel.setOnClickListener(v -> dialogFileOptions.dismiss());

        dialogFileOptions.show();

        /*
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setItems(items, (DialogInterface dialog, int position) -> {

            switch (position) {

                case 0:
                    mode = MEDIA_MODE.CAMERA_MODE;
                    checkPermission();
                    break;

                case 1:
                    mode = MEDIA_MODE.GALLERY_MODE;
                    checkPermission();
                    break;

                case 2:
                    mode = MEDIA_MODE.NO_MODE;
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();*/
    }

    public void checkPermission() {
        permissionManager.setMultiplePermission(new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

    @Override
    public void onSinglePermissionGranted(String permission) {
    }

    @Override
    public void onMultiplePermissionGranted(ArrayList<String> permissions) {

        if (permissions.size() > 0) {

            if (mode == MEDIA_MODE.CAMERA_MODE) {

                if (permissions.contains(Manifest.permission.CAMERA)) {

                    Intent capturePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (getActivity() != null) {

                        if (capturePhotoIntent.resolveActivity(packageManager) != null) {

                            File imageFile = getImageFile(LOCAL_MEDIA_IMAGE_DIR, "jpg");

                            if (imageFile != null) {
                                Uri uri = FileProvider.getUriForFile(binding.getRoot().getContext(),
                                        binding.getRoot().getContext().getPackageName() + ".fileprovider", imageFile);
                                capturePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(capturePhotoIntent, CAMERA_PHOTO_REQUEST_CODE);
                            }
                        }
                    }
                } else
                    Toast.makeText(context, "Camera permission needed.", Toast.LENGTH_SHORT).show();

            } else if (mode == MEDIA_MODE.GALLERY_MODE) {

                if (permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Intent galleryPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (galleryPhotoIntent.resolveActivity(packageManager) != null)
                        startActivityForResult(galleryPhotoIntent, GALLERY_PHOTO_REQUEST_CODE);
                }
            }
        } else
            Toast.makeText(context, "permissions not granted.", Toast.LENGTH_SHORT).show();
    }

    public File getImageFile(String dirname, String extension) {

        File tmpFile = null;

        try {

            String fileName = new SimpleDateFormat("ddMMyyyyHHmmssSSS", Locale.US).format(new Date()) + "." + extension;

            //File tmpDir = new File(Environment.getExternalStorageDirectory() + dirname);  // getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File tmpDir = binding.getRoot().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }

            tmpFile = new File(tmpDir, fileName);
            mainImageFilepath = tmpFile.getAbsolutePath();

            deliveryDetails.setParcelImagePath(mainImageFilepath);
            deliveryDetails.setParcelImageName(fileName);

            return tmpFile;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case CAMERA_PHOTO_REQUEST_CODE:

                    previewImageInImageView();
                    break;

                case GALLERY_PHOTO_REQUEST_CODE:
                    selectedGalleryImageResult(data);
                    break;
            }
        }
    }

    private void selectedGalleryImageResult(Intent data) {

        try {

            Uri targetUri;

            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                targetUri = clipData.getItemAt(0).getUri();
            } else {
                targetUri = data.getData();
            }

            if (targetUri != null) {

                String imagePath;
                String imageName = "";

                Cursor cursor;

                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = getActivity().getContentResolver().query(targetUri, projection, null, null, null);

                if (cursor == null) {

                    imagePath = targetUri.getPath();

                } else {

                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imagePath = cursor.getString(idx);
                    cursor.close();
                }

                if (imagePath != null && imagePath.length() > 0) {

                    mainImageFilepath = imagePath;
                    previewImageInImageView();

                    int endIndex = imagePath.lastIndexOf("/");

                    if (endIndex != -1) {
                        imageName = imagePath.substring(endIndex + 1);
                    }

                    deliveryDetails.setParcelImagePath(mainImageFilepath);
                    deliveryDetails.setParcelImageName(imageName);
                }

            } else
                Toast.makeText(context, "Error occurred while fetching image.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void previewImageInImageView() {
        binding.imgPreview.setVisibility(View.VISIBLE);
        Glide.with(Objects.requireNonNull(getActivity())).load(mainImageFilepath).into(binding.imgPreview);
    }

    private enum MEDIA_MODE {
        CAMERA_MODE,
        GALLERY_MODE,
        NO_MODE
    }
}
