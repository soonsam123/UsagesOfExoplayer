package com.soon.karat.exoplayer.simple_examples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.soon.karat.exoplayer.R;

public class VideoLoopingActivity extends AppCompatActivity {

    private Uri videoUri;

    private SimpleExoPlayer player;
    private TrackSelector trackSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_looping);

        // 1. Get the Uri of the video from MainActivity.
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
     * Load a single video into a LoopingMediaSource and play it 5 times using
     * SimpleExoPlayer and PlayerView.
     */
    private void initializePlayer() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        PlayerView mPlayerView = findViewById(R.id.player_view);
        mPlayerView.setPlayer(player);

        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), defaultBandwidthMeter);

        // Create the MediaSource of the video we choose.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        // Play this video 5 times.
        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(videoSource, 5);

        player.prepare(loopingMediaSource);
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
