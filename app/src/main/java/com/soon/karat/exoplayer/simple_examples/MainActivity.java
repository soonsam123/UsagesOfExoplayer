package com.soon.karat.exoplayer.simple_examples;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;


import com.soon.karat.exoplayer.complex_examples.ComplexActivity;
import com.soon.karat.exoplayer.R;

/**
 * These activity is where you choose what kind of ExoPlayer samples you want to play,
 * you have the following options:
 * <u>
 *     <li>Play a single video         (VideoPlayerActivity)   </li>
 *     <li>Play a playlist of videos   (VideoPlaylistActivity) </li>
 *     <li>Play just a part of a video (VideoClippingActivity) </li>
 *     <li>Play a video multiple times (VideoLoopingActivity)  </li>
 * </u>
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRadioGroup = findViewById(R.id.radio_group);

        AppCompatButton mPlayVideo = findViewById(R.id.button_play_video);
        mPlayVideo.setOnClickListener(this);

        setupToolbar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_simple:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                startActivity(mainActivityIntent);
                break;
            case R.id.menu_complex:
                Intent complexActivityIntent = new Intent(this, ComplexActivity.class);
                startActivity(complexActivityIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_play_video) {
            switch (mRadioGroup.getCheckedRadioButtonId()) {
                case R.id.radio_button_single_video: // 1. Play a single video;
                    Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
                    startActivity(videoPlayerIntent);
                    break;
                case R.id.radio_button_playlist: // 2. Play a play list of videos;
                    Intent videoPlaylistIntent = new Intent(this, VideoPlaylistActivity.class);
                    startActivity(videoPlaylistIntent);
                    break;
                case R.id.radio_button_clipping: // 3. Play just a part of a video;
                    Intent videoClippingIntent = new Intent(this, VideoClippingActivity.class);
                    startActivity(videoClippingIntent);
                    break;
                case R.id.radio_button_looping: // 4. Play the same video several times.
                    Intent videoLoopingIntent = new Intent(this, VideoLoopingActivity.class);
                    startActivity(videoLoopingIntent);
                    break;
            }
        }
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }



}
