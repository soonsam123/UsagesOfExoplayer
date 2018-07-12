# UsagesOfExoplayer
These are some usages of ExoPlayer.

In the MainActivity there is a button for you to pick a video from the phone's gallery.

When you click the button the following code will be runned, the code will handle the request permissions for WRITE_EXTERNAL_STORARGE in the following cases:

1. When the user deny it: The app will display a snackbar showing the user he needs to give permissions to access the gallery.
2. When the user check never ask again and deny: The app will display a dialog which the user can navigate to settings.
3. When the user allow it: Navigates to the gallery.

In the button click


'''
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
'''
