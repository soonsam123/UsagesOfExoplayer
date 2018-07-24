# UsagesOfExoplayer
These are some usages of ExoPlayer.

## Simple Examples

**MainActivity.java**
In MainActivity you can choose what you want to do. These are some more simple options.

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/simple_page.png" width="280" height="442"/>

1. Play a single video; 
2. Play a playlist;
3. Clipping a video, this will play only 5 seconds of the video;
4. Loop a video 5 times.

### Play a Single Video

```
MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
```


### Play a Playlist

```
// Load each video into a separated MediaSource.
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri1);
        MediaSource secondSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri2);
        MediaSource thirdSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri3);

        // Concatenate the three videos to play them in sequence. 
        // When first video finish, second will play and so on.
        ConcatenatingMediaSource playlistSource = 
                new ConcatenatingMediaSource(firstSource, secondSource, thirdSource);
```


### Clipping a Video

```
MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        // Take only from 00:10 to 00:15 seconds of the video (5 seconds total).
        ClippingMediaSource clippingSource = new ClippingMediaSource(
                videoSource,
                10_000_000,
                15_000_000);
```


### Looping a Video

```
MediaSource videoSource = 
        new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        // Play this video 5 times.
        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(videoSource, 5);
```


## Complex Examples

**ComplexActivity.java**
In ComplexActivity you can choose what you want to do. These are some more complex options.

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/complex_page.png" width="280" height="449"/>

1. Play a single video: 
  - Play a mp4 or DASH video from the internet with fully player layout customization; 
  - Custom buttons in the player; 
  - Shows when the video is buffering or playing (listen to the player states);
  - The video keeps playing while you rotate the device;
  - Change to full screen when in landscape mode just like youtube app;
  - Works in multi-window mode; (API 24 or greater)
  - Come back to same video position when leaving the app and coming back again.
  - You can choose Video and Audio qualities.

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/video_buffering.png" width="280" height="478"/>

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/video_portrait_mode.png" width="280" height="484"/>

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/video_landscape_mode.png" width="480" height="268"/>  

### Playing DASH file


```
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
```

### Customize the player layout

To customize the player layout you just need to:
1. Copy the file [exo_playback_control_view.xml](https://github.com/google/ExoPlayer/blob/release-v2/library/ui/src/main/res/layout/exo_playback_control_view.xml) into your app's layout folder;
2. Customize the way you want;
3. DO NOT change the items id, otherwise your app will not work.

Look at my [exo_playback_control_view.xml](https://github.com/soonsam123/UsagesOfExoplayer/blob/master/app/src/main/res/layout/exo_playback_control_view.xml) to see how I customized it.


### Listen to the player states (Show the ProgressBar when buffering)

When there is no media yet (STATE_IDLE): 
  - Show the progressBar;
  - Hide the player controllers.
When video is buffering (STATE_BUFFERING):
  - Show the progressBar;
  - Hide the play/pause button. (because this button is in the middle in the same place where the progressBar is placed)
When video is ready to go (STATE_READY):
  - Dismiss the progressBar;
  - Show the play/pause button.

```
private class PlayerEventListener extends Player.DefaultEventListener{
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE: // The player does not have any media to play.
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPlayerView.hideController();
                    break;
                case Player.STATE_BUFFERING: // The player needs to load media before playing.
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPlayPauseLayout.setVisibility(View.GONE);
                    break;
                case Player.STATE_READY: // The player is able to immediately play from its current position.
                    mProgressBar.setVisibility(View.GONE);
                    mPlayPauseLayout.setVisibility(View.VISIBLE);
                    break;
                case Player.STATE_ENDED: // The player has finished playing the media.
                    break;
                default:
                    break;
            }
        }
```

2. Play a video with Ads;

**First**, Create a ImaAdsLoader instance in onCreate method.


```
@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        
        ...
        imaAdsLoader = new ImaAdsLoader(this, getAdTagUri());
    }
    
    private Uri getAdTagUri() {
        return Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=");
    }
```

You can take more AdTagUris in [here](https://developers.google.com/interactive-media-ads/docs/sdks/android/tags).

**Second**, build an AdsMediaSource with your VideoMediaSource.

```
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
```
