package com.soon.karat.exoplayer.complex_examples;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.soon.karat.exoplayer.R;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "VideoPlayerActivity";
    
    private SimpleExoPlayer player;
    private PlayerView mPlayerView;
    private DefaultTrackSelector trackSelector;
    private ComponentListener componentListener;

    private ImageButton mBack;
    private ImageButton mLike;
    private ImageButton mShare;
    private ImageButton mFullscreen;

    private LinearLayout mPlayPauseLayout;
    private ProgressBar mProgressBar;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Creating");
        setContentView(R.layout.activity_video_player);

        componentListener = new ComponentListener();
        setupWidgets();
        setupClickListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: Starting");
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: Resuming");
        hideSystemUi();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: Pausing");
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: Stopping");
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: Destroying");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_button_back:
                finish();
                break;
            case R.id.image_button_like:
                Toast.makeText(this, "Like", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_button_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_button_full_screen:
                Toast.makeText(this, "Fullscreen", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupWidgets() {

        mPlayerView = findViewById(R.id.player_view);

        mBack = findViewById(R.id.image_button_back);
        mLike = findViewById(R.id.image_button_like);
        mShare = findViewById(R.id.image_button_share);
        mFullscreen = findViewById(R.id.image_button_full_screen);

        mPlayPauseLayout = findViewById(R.id.linear_layout_play_pause);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        mBack.setOnClickListener(this);
        mLike.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mFullscreen.setOnClickListener(this);
    }

    private void initializePlayer() {
        if (player == null) {
            Log.i(TAG, "initializePlayer: Initializing Player == null");
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            player.addListener(componentListener);
            player.addVideoDebugListener(componentListener);
            player.addAudioDebugListener(componentListener);
            mPlayerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            Log.i(TAG, "initializePlayer: Seeking to: " + currentWindow + " - " + playbackPosition);
            player.seekTo(currentWindow, playbackPosition); // This make the playback come back to the previous position.
        }
        Log.i(TAG, "initializePlayer: Initializing Player != null");
        MediaSource videoSource = buildMediaSource(Uri.parse(getString(R.string.video_mp4)));
        player.prepare(videoSource, false, true);
    }

    private void releasePlayer() {
        if (player != null) {
            Log.i(TAG, "releasePlayer: Releasing Player");
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.addVideoListener(null);
            player.addVideoDebugListener(null);
            player.addAudioDebugListener(null);
            player.release();
            player = null;
            trackSelector = null;
            Log.i(TAG, "releasePlayer: playbackPosition: " + playbackPosition);
            Log.i(TAG, "releasePlayer: currentWindow: " + currentWindow);
            Log.i(TAG, "releasePlayer: playWhenReady: " + playWhenReady);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), BANDWIDTH_METER);
        return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    @SuppressLint("InlinedApi") // View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY min API is 19, current min is 18.
    private void hideSystemUi() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }



    private class ComponentListener implements Player.EventListener, VideoRendererEventListener, AudioRendererEventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE: // The player does not have any media to play.
                    stateString = "Player.STATE_IDLE";
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPlayerView.hideController();
                    break;
                case Player.STATE_BUFFERING: // The player needs to load media before playing.
                    stateString = "Player.STATE_BUFFERING";
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPlayPauseLayout.setVisibility(View.GONE);
                    break;
                case Player.STATE_READY: // The player is able to immediately play from its current position.
                    stateString = "Player.STATE_READY";
                    mProgressBar.setVisibility(View.GONE);
                    mPlayPauseLayout.setVisibility(View.VISIBLE);
                    break;
                case Player.STATE_ENDED: // The player has finished playing the media.
                    stateString = "Player.STATE_ENDED";
                    break;
                default:
                    stateString = "UNKNOWN_STATE";
                    break;
            }
            Log.i(TAG, "onPlayerStateChanged: Changed to State: " + stateString + " - playWhenReady: " + playWhenReady);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }
    }
}
