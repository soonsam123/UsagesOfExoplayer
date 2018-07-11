package com.soon.karat.exoplayer;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Android 7.0 (Nougat) and higher support multi-window mode. When the activity
 * enters in multi-window mode and the focus is in other application, our
 * application will call {@link #onPause()} method.
 * </p>
 * <h4>Descriptions</h4>
 * Utils.SDK_INT > 23 : API level 24/Android 7.0/Nougat or higher.
 * Utils.SDK_INT <= 23 : API level 23/Android 6.0/Marshmallow or lower.
 *
 * <h3>The activity is FINISHING</h3>
 *
 * Case 1. Utils.SDK_INT > 23: we should NOT release the player when it enters
 * in {@link #onPause()} as the user may want to watch the video in multi-window mode.
 * Then, we let to release it only in {@link #onStop()} method.
 * </p>
 * Case 2. Utils.SDK_INT <= 23: we should release the player when it enters in
 * {@link #onPause()} method.
 * </p>
 *
 * <h3>The activity is STARTING/h3>
 *
 * Case 1. Utils.SDK_INT > 23: We should initialize the player in {@link #onStart()}
 * method, as when the app calls {@link #onResume()} the player will be already there
 * since it was not released in the {@link #onPause()} method.
 * </p>
 * Case 2. Utils.SDK_INT <= 23: We should initialize the player in {@link #onResume()}
 * method, since it was released in {@link #onPause()} method.
 * </p>
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private static final String TAG = "VideoPlayerActivity";
    
    private static final String APPLICATION_NAME = "Exoplayer";

    private Uri videoUri;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Log.i(TAG, "onCreate: CREATING");

        // 1. Get the video URI from MainActivity.
        Intent intent = getIntent();
        videoUri = Uri.parse(intent.getStringExtra("videoUri"));


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: STARTING");
        if (Util.SDK_INT > 23) {
            Log.i(TAG, "onStart: SDK_INT: " + Util.SDK_INT + " initializing in onStart");
            initializePlayer(videoUri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: RESUMING");
        // Version 23 or lower OR player have not being initialized yet.
        if (Util.SDK_INT <= 23 || player == null) {
            Log.i(TAG, "onResume: SDK_INT: " + Util.SDK_INT + " initializing in onResume");
            initializePlayer(videoUri);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: PAUSING");
        if (Util.SDK_INT <= 23) {
            Log.i(TAG, "onPause: SDK_INT: " + Util.SDK_INT + " releasing in onPause");
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: STOPPING");
        if (Util.SDK_INT > 23) {
            Log.i(TAG, "onStop: SDK_INT: " + Util.SDK_INT + " releasing in onStop");
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: DESTROYING");
    }

    /**
     * Loads a video uri into a PlayerView usign MediaSource and SimpleExoPlayer.
     * @param videoUri the uri of the video that will be loaded.
     */
    private void initializePlayer(Uri videoUri) {

        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        // 3. Attach the player to a view
        PlayerView mPlayerView = findViewById(R.id.player_view);
        mPlayerView.setPlayer(player);

        // 4. Preparing the player
        // 4.1. Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        // 4.2. Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, APPLICATION_NAME), defaultBandwidthMeter);

        // 4.3. This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        // 4.4. Prepare the player with the source
        player.prepare(videoSource);
        player.setPlayWhenReady(true);

    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            trackSelector = null;
        }
    }
}
