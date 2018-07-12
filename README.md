# UsagesOfExoplayer
These are some usages of ExoPlayer.

In MainActivity you can choose what you want to do. These are the options.

1. Play a single video; 
2. Play a playlist;
3. Clipping a video, this will play only 5 seconds of the video;
4. Loop a video 5 times.

Select your options, then when you click in the Button "Pick a Video" you will redirected to your gallery, the code will handle the request permissions for WRITE_EXTERNAL_STORARGE in the following cases:

- When the user deny it: The app will display a snackbar showing the user he needs to give permissions to access the gallery.
- When the user check never ask again and deny: The app will display a dialog which the user can navigate to settings.
- When the user allow it: Navigates to the gallery.

## Play a Single Video

```
MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
```


## Play a Playlist

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


## Clipping a Video

```
MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        // Take only from 00:10 to 00:15 seconds of the video (5 seconds total).
        ClippingMediaSource clippingSource = new ClippingMediaSource(
                videoSource,
                10_000_000,
                15_000_000);
```


## Looping a Video

```
MediaSource videoSource = 
        new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        // Play this video 5 times.
        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(videoSource, 5);
```
