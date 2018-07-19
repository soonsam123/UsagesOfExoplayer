# UsagesOfExoplayer
These are some usages of ExoPlayer.

## Simple Examples

**MainActivity.java**
In MainActivity you can choose what you want to do. These are some more simple options.

1. Play a single video; 
2. Play a playlist;
3. Clipping a video, this will play only 5 seconds of the video;
4. Loop a video 5 times.

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/simple_page.png" width="280" height="442"/>

First, select your option. Then click in the Button "Pick a Video" and you will be redirected to your gallery. The code will handle the request permissions for WRITE_EXTERNAL_STORAGE in the following cases:

- When the user deny it: The app will display a snackbar showing the user he needs to give permissions to access the gallery.
- When the user check never ask again and deny: The app will display a dialog which the user can navigate to settings.
- When the user allow it: Navigates to the gallery.

### Play a Single Video

```
MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
```


### Play a Playlist

```
// Load each video into a separated MediaSource.
        MediaSource firstSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUriList.get(0));
        MediaSource secondSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUriList.get(1));
        MediaSource thirdSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUriList.get(2));

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

1. Play a single video: 
  - Play a mp4 or DASH video from the internet with fully player layout customization; 
  - Custom buttons in the player; 
  - Shows when the video is buffering or playing (listen to the player states);
  - Do not stop the video when rotating;
  - Change to full screen when in landscape mode just like youtube app;
  - Works in multi-window mode; (API 24 or greater)
  - Come back to same position when leaving the app and coming back again.

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/complex_page.png" width="280" height="449"/>

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/video_buffering.png" width="280" height="478"/>

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/video_portrait_mode.png" width="280" height="484"/>

<img src="https://github.com/soonsam123/UsagesOfExoplayer/blob/master/pictures/video_landscape_mode.png" width="480" height="268"/>  

2. Play a video with Ads;

