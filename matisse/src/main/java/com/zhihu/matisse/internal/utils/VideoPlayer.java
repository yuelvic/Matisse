package com.zhihu.matisse.internal.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by emman on 8/22/17.
 */

public class VideoPlayer {

    private Context context;

    private Handler handler = new Handler();
    private BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
    private TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
    private SimpleExoPlayer simpleExoPlayer;

    private Listener listener;

    public VideoPlayer(Context context) {
        this.context = context;
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public SimpleExoPlayer getPlayer() {
        return simpleExoPlayer;
    }

    public void prepare(Uri uri) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Matisse"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource, 1000);
        simpleExoPlayer.prepare(loopingSource);
        simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        listener.onPlayerReady(simpleExoPlayer);
                        break;
                    case Player.STATE_ENDED:
                        restart();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    public void play() {
        simpleExoPlayer.setPlayWhenReady(true);
    }

    public void restart() {
        simpleExoPlayer.seekTo(0);
    }

    public void stop() {
        simpleExoPlayer.setPlayWhenReady(false);
    }

    public void release() {
        simpleExoPlayer.release();
    }

    public interface Listener {
        void onPlayerReady(SimpleExoPlayer simpleExoPlayer);
    }

}
