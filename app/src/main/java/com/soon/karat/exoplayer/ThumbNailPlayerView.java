package com.soon.karat.exoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.exoplayer2.ui.PlayerView;

public class ThumbNailPlayerView extends PlayerView {

    private int mForceHeight = 0;
    private int mForceWidth = 0;

    public ThumbNailPlayerView(Context context) {
        super(context);
    }

    public ThumbNailPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbNailPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("Measure", "onMeasure: Width: " + widthMeasureSpec + " - height: " + heightMeasureSpec);

        setMeasuredDimension(mForceWidth, mForceHeight);

    }
}
