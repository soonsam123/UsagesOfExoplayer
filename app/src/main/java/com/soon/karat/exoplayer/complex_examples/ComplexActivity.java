package com.soon.karat.exoplayer.complex_examples;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.soon.karat.exoplayer.R;
import com.soon.karat.exoplayer.simple_examples.MainActivity;

public class ComplexActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex);

        setupToolbar();

        mRadioGroup = findViewById(R.id.radio_group);

        AppCompatButton mPlay = findViewById(R.id.button_play);
        mPlay.setOnClickListener(this);

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
        if (v.getId() == R.id.button_play) {
            switch (mRadioGroup.getCheckedRadioButtonId()) {
                // 1. ________________ Play a Single Video ________________
                case R.id.radio_button_single_video:
                    Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
                    startActivity(videoPlayerIntent);
                    break;
            }
        }
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }
}
