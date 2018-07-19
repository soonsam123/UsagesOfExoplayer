package com.soon.karat.exoplayer.complex_examples;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.soon.karat.exoplayer.R;

/**
 * 1. Where to get the AdTagUri's ?
 * https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
 * Click in any of the items and copy the link in the browser. Paste in {@link #getAdTagUri()} method
 * to change the ad. You should put your own ads from your campaign.
 * </p>
 * 2. Options for playing ads
 * https://github.com/google/ExoPlayer/issues/4025
 * Andrew Lewis suggested some ways to play ads with exoPlayer. Maybe you want to play
 * your own ads from your own advertises, so you will need to customize to play this.
 */
public class AdsActivity extends AppCompatActivity {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private SimpleExoPlayer player;
    private PlayerView mPlayerView;
    private DefaultTrackSelector trackSelector;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    // Ads
    private ImaAdsLoader imaAdsLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mPlayerView = findViewById(R.id.player_view);
        imaAdsLoader = new ImaAdsLoader(this, getAdTagUri());

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
        imaAdsLoader.release();
        super.onDestroy();
    }

    private void initializePlayer() {

        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        mPlayerView.setPlayer(player);
        MediaSource mediaSourceWithAds = buildMediaSourceWithAds(Uri.parse(getString(R.string.video_mp4_sintel)));
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSourceWithAds, false, true);

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            playWhenReady = player.getPlayWhenReady();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private MediaSource buildMediaSourceWithAds(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), BANDWIDTH_METER);
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        return new AdsMediaSource(
                videoSource,
                dataSourceFactory,
                imaAdsLoader,
                mPlayerView.getOverlayFrameLayout());
    }

    private Uri getAdTagUri() {
        return Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=");
    }
}
