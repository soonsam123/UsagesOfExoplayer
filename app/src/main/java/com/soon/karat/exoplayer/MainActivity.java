package com.soon.karat.exoplayer;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;


import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE = 98;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL = 107;
    private static final int SETTINGS_REQUEST_CODE = 82;

    private ConstraintLayout mContainer;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(Environment.DIRECTORY_MOVIES, GALLERY_REQUEST_CODE);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    displaySnackBarToVerifyPermissions();
                } else {
                    displayDialogToGoToSettings();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // -------------------------------------------------------------------
        //                  1. Coming back from the Gallery
        // -------------------------------------------------------------------
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Uri videoUri = data.getData();

                if (videoUri != null) {
                    Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
                    videoPlayerIntent.putExtra("videoUri", videoUri.toString());
                    startActivity(videoPlayerIntent);
                } else {
                    Toast.makeText(this, "Video not found", Toast.LENGTH_LONG).show();
                }
            }
        }

        // -------------------------------------------------------------------
        //                    2. Coming back from Settings
        // -------------------------------------------------------------------
        if (requestCode == SETTINGS_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery(Environment.DIRECTORY_MOVIES, GALLERY_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = findViewById(R.id.constraint_layout_container);

        AppCompatButton mPickVideo = findViewById(R.id.button_pick_video);
        mPickVideo.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_pick_video:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openGallery(Environment.DIRECTORY_MOVIES, GALLERY_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                }
                break;
        }
    }


    /**
     * Open the Gallery.
     * @param directory the directory we want to open.
     * @param imageGalleryRequest the REQUEST_CODE when returning back from the gallery to the activity.
     */
    public void openGallery(String directory, int imageGalleryRequest) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(directory);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri pictureData = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(pictureData, "video/*");

        startActivityForResult(photoPickerIntent, imageGalleryRequest);
    }

    private void displaySnackBarToVerifyPermissions() {
        Snackbar snackbar = Snackbar.make(mContainer,
                R.string.msg_allow_permission_to_gallery, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_verify, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL);
            }
        }).show();
    }

    private void displayDialogToGoToSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_allow_permission_in_settings)
                .setPositiveButton(R.string.preferences_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        settingsIntent.setData(uri);
                        startActivityForResult(settingsIntent, SETTINGS_REQUEST_CODE);
                    }
                })
                .setNegativeButton(R.string.action_not_now, null);
        Dialog dialog = builder.create();
        dialog.show();
    }

}
