package com.soon.karat.exoplayer.simple_examples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
import com.soon.karat.exoplayer.R;

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
 *
 * <h3>Android Developers Guide - Building a video player activity</h3>
 *
 * https://developer.android.com/guide/topics/media-apps/video-app/building-a-video-player-activity
 * When an app is closed, the activity receives the onPause() and onStop() callbacks in succession.
 * If the player is playing, you must stop it before its activity goes away. The choice of which
 * callback to use depends on what Android version you're running.
 * </p>
 * In Android 6.0 (API level 23) and earlier there is no guarantee of when {@link #onStop()} method
 * is called; it could get called 5 seconds after your activity disappears. Therefore, in Android
 * versions earlier than 7.0, your app should stop playback in {@link #onPause()}. In Android 7.0
 * and beyond, the system calls {@link #onStop()} as soon as the activity becomes not visible,
 * so this is not a problem.
 * </p>
 * To summarize:
 * --> In Android version 6.0 and earlier, stop the player in the {@link #onPause()} callback.
 * --> In Android version 7.0 and later, stop the player in the {@link #onStop()} callback.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private Uri videoUri;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // 1. Get the video URI from MainActivity.
        Intent intent = getIntent();
        videoUri = Uri.parse(intent.getStringExtra("videoUri"));


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    /**
     * Loads a single video Uri into a MediaSource and play this video using SimpleExoPlayer.
     */
    private void initializePlayer() {

        // 1. Create a default TrackSelector
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
                Util.getUserAgent(this, getString(R.string.app_name)), defaultBandwidthMeter);

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
