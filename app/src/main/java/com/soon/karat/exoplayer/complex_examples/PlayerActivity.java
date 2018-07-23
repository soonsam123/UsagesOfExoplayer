package com.soon.karat.exoplayer.complex_examples;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.soon.karat.exoplayer.R;
import com.soon.karat.exoplayer.ThumbNailPlayerView;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlayerActivity";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final String TYPE_DASH = "dash";
    private static final String TYPE_OTHER = "other";

    private SimpleExoPlayer player;
    private ThumbNailPlayerView mPlayerView;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private TrackGroupArray lastSeenTrackGroupArray;

    private ImageButton mBack;
    private ImageButton mLike;
    private ImageButton mShare;
    private ImageButton mFullscreen;

    private LinearLayout mPlayPauseLayout;
    private ProgressBar mProgressBar;

    private LinearLayout debugRootView;

    private long startPosition;
    private int startWindow;
    private boolean startAutoPlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_complex);

        setupWidgets();
        setupClickListeners();
        setPlayerViewDimensions();

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent: Running onNewIntent");
        releasePlayer();
        clearStartPosition();
        setIntent(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
        Log.i(TAG, "onSaveInstanceState: Saving AUTO_PLAY: " + startAutoPlay);
        Log.i(TAG, "onSaveInstanceState: Saving WINDOW: " + startWindow);
        Log.i(TAG, "onSaveInstanceState: Saving POSITION: " + startPosition);
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
        if (v.getParent() == debugRootView) {
            MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) { Log.i("Debugging", "onClick: mappedTrackInfo: " + mappedTrackInfo.toString());
                CharSequence title = ((Button) v).getText(); Log.i("Debugging", "onClick: title: " + title);
                int rendererIndex = (int) v.getTag(); Log.i("Debugging", "onClick: rendererIndex: " + rendererIndex);
                int rendererType = mappedTrackInfo.getRendererType(rendererIndex); Log.i("Debugging", "onClick: rendererType: " + rendererType);
                boolean allowAdaptiveSelections =
                        rendererType == C.TRACK_TYPE_VIDEO ||
                                (rendererType == C.TRACK_TYPE_AUDIO
                                        && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                                        == MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);
                Log.i("Debugging", "onClick: allowAdaptiveSelections: " + allowAdaptiveSelections);
                Pair<AlertDialog, TrackSelectionView> dialogPair =
                        TrackSelectionView.getDialog(this, title, trackSelector, rendererIndex);
                dialogPair.second.setShowDisableOption(true);
                dialogPair.second.setAllowAdaptiveSelections(allowAdaptiveSelections);
                dialogPair.first.show();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "onConfigurationChanged: Configuration is changing to LandScape");
            setPlayerViewDimensionsForLandScapeMode();

        } else {
            setPlayerViewDimensionsForPortraitMode();
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

        debugRootView = findViewById(R.id.controls_root);
    }

    private void setupClickListeners() {
        mBack.setOnClickListener(this);
        mLike.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mFullscreen.setOnClickListener(this);
    }

    private void setPlayerViewDimensionsForLandScapeMode() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        hideSystemUi();
        mPlayerView.setDimensions(width, height);
    }

    private void setPlayerViewDimensionsForPortraitMode() {
        // 1 (width) : 1/1.5 (height) --> Height is 66% of the width when in Portrait mode.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Double heightDouble = width / 1.5;
        Integer height = heightDouble.intValue();

        mPlayerView.setDimensions(width, height);
    }

    private void setPlayerViewDimensions() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setPlayerViewDimensionsForLandScapeMode();
        } else {
            setPlayerViewDimensionsForPortraitMode();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);
            lastSeenTrackGroupArray = null;
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            player.addListener(new PlayerEventListener());
            mPlayerView.setPlayer(player);
            player.setPlayWhenReady(startAutoPlay);
        }
        MediaSource videoSource = buildMediaSource(TYPE_DASH, Uri.parse("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-manifest.mpd"));
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        player.prepare(videoSource, !haveStartPosition, true);
        updateButtonVisibilities();
    }

    private void releasePlayer() {
        if (player != null) {
            Log.i(TAG, "releasePlayer: Releasing Player");
            updateStartPosition();
            player.removeListener(new PlayerEventListener());
            player.addVideoListener(null); // Is it necessary to remove these listeners? afraid of memory leak. OOM
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private MediaSource buildMediaSource(String type, Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), BANDWIDTH_METER);
        switch (type) {
            case TYPE_DASH:
                DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(dataSourceFactory);
                return new DashMediaSource.Factory(dashChunkSourceFactory, dataSourceFactory).createMediaSource(uri);
            case TYPE_OTHER:
                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default:{
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
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

    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();
        if (player == null) {
            Log.i("Debugging", "updateButtonVisibilities: player == null");
            return;
        }

        MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            Log.i("Debugging", "updateButtonVisibilities: mappedTrackInfo == null");
            return;
        }

        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            Log.i("Debugging", "updateButtonVisibilities: Iterating: " + i);
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                Log.i("Debugging", "updateButtonVisibilities: trackGroups: " + trackGroups.toString() + " - trackGroups.length: " + trackGroups.length);
                Button button = new Button(this);
                int label;
                switch (player.getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:
                        Log.i("Debugging", "updateButtonVisibilities: Type Audio");
                        label = R.string.track_selection_audio;
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        Log.i("Debugging", "updateButtonVisibilities: Type Video");
                        label = R.string.track_selection_video;
                        break;
                    case C.TRACK_TYPE_TEXT:
                        Log.i("Debugging", "updateButtonVisibilities: Type Text");
                        label = R.string.track_selection_text;
                        break;
                    default:
                        continue;
                }
                button.setText(label);
                button.setTag(i);
                button.setOnClickListener(this);
                debugRootView.addView(button);
                Log.i("Debugging", "updateButtonVisibilities: debutRootView: " + debugRootView.toString());
                Log.i("Debugging", "updateButtonVisibilities: debutRootView Size: " + debugRootView.getChildCount());
            }
        }
    }

    private class PlayerEventListener extends Player.DefaultEventListener{
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
            Log.i("Debugging", "onPlayerStateChanged: Changed to State: " + stateString + " - startAutoPlay: " + playWhenReady);
            updateButtonVisibilities();
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            super.onPositionDiscontinuity(reason);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            super.onPlayerError(error);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i("Debugging", "onTracksChanged: Track is changing");
            updateButtonVisibilities();
            // It seems it handle errors.
            if (trackGroups != lastSeenTrackGroupArray) {
                MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(PlayerActivity.this, getString(R.string.error_unsupported_video), Toast.LENGTH_SHORT).show();
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        Toast.makeText(PlayerActivity.this, getString(R.string.error_unsupported_audio), Toast.LENGTH_SHORT).show();
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }
}
