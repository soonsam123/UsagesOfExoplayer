package com.soon.karat.exoplayer.utils;

import android.content.Context;
import android.net.Uri;

public class Helpers {

    /**
     * Get the video Uri by parsing its file id in raw folder.
     * @param file the file id that is in the raw folder.
     * @return the uri of the video.
     */
    public static Uri getVideoUri(Context context, int file) {
        // getPackageName(): com.soon.karat.exoplayer
        String videoPath = "android.resource://" + context.getPackageName() + "/" + file;
        return Uri.parse(videoPath);
    }

}
