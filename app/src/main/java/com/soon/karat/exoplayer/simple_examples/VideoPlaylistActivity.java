package com.soon.karat.exoplayer.simple_examples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
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

import java.util.ArrayList;

public class VideoPlaylistActivity extends AppCompatActivity {

    private ArrayList<String> videoUriToStringList;
    private ArrayList<Uri> videoUriList; // This will store the three videos Uris.

    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playlist);

        videoUriToStringList = new ArrayList<>();
        videoUriList = new ArrayList<>();

        Intent intent = getIntent();
        // This is the list of Uris, but they are stored as String in order to transport in the Intent.
        videoUriToStringList = intent.getStringArrayListExtra("videoUriList");

        // Convert the list of Strings to list of Uri.
        for (String singleVideo : videoUriToStringList) {
            videoUriList.add(Uri.parse(singleVideo));
        }


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
     * Loads three videos in a ConcatenatingMediaSource and play them in sequence
     * using SimpleExoPlayer and PlayerView.
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

        // Load each video into a separated MediaSource.
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUriList.get(0));
        MediaSource secondSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUriList.get(1));
        MediaSource thirdSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUriList.get(2));

        // Concatenate the three videos to play them in sequence. When first video finish, second will play and so on.
        ConcatenatingMediaSource playlistSource = new ConcatenatingMediaSource(firstSource, secondSource, thirdSource);

        player.prepare(playlistSource);
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
