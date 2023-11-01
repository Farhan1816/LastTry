package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import com.google.api.services.youtube.model.SearchResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.api.services.youtube.model.Thumbnail;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.checkerframework.checker.units.qual.A;
public class youtubePlayer extends AppCompatActivity implements Playable{
    private YouTubePlayerView youTubePlayerView;
    private static YouTubePlayer youTubePlayer;
    private YouTubePlayerListener listener;
    private String videoId, Title, Artist;
    private PlayerConstants.PlayerState currentState;
    private YouTubePlayerTracker youTubePlayerTracker;
    private SearchResult searchResult;
    boolean isPlaying;
    private BroadcastReceiver broadcastReceiver;
    static boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debug", "creating");
        setContentView(R.layout.youtubeplayer);
        super.onCreate(savedInstanceState);
        youTubePlayerTracker = new YouTubePlayerTracker();

        // Retrieve the videoId from the intent's extras
        videoId = getIntent().getStringExtra("videoId");
        Title = getIntent().getStringExtra("Title");
        Artist = getIntent().getStringExtra("Artist");
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.enableBackgroundPlayback(true);
        CreateNotification.createNotification(this, Title, Artist, R.drawable.ic_pause_black_24dp);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("debug", "Receivingggg!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                if (isPlaying){
                    youTubePlayer.pause();
                } else {
                    youTubePlayer.play();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        playVideo(videoId);
    }



    public void onTrackPlay() {
        Log.d("debug", "Playingggg!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        CreateNotification.createNotification(this, Title, Artist,
                R.drawable.ic_pause_black_24dp);
        isPlaying = true;
    }

    public void onTrackPause() {
        Log.d("debug", "Pausing!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        CreateNotification.createNotification(this, Title, Artist,
                R.drawable.ic_play_arrow_black_24dp);
        isPlaying = false;
    }

    @Override
    protected void onPause()
    {
        Log.d("debug", "Onpausenow!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        super.onPause();
    }
    public static void Pause(){youTubePlayer.pause();}
    @Override
    protected void onDestroy()
    {
        Log.d("debug", "OnDestroynow!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        super.onDestroy();
        youTubePlayerView.release();
    }
    private void playVideo(String videoId) {

        youTubePlayerView.addYouTubePlayerListener(listener = new AbstractYouTubePlayerListener() {

            @Override
            public void onReady(@NonNull YouTubePlayer Player) {
                MusicPlayerActivity.Pause();
                DefaultPlayerUiController defaultPlayerUiController = new DefaultPlayerUiController(youTubePlayerView, Player);
                youTubePlayerView.setCustomPlayerUi(defaultPlayerUiController.getRootView());
                youTubePlayer = Player;
                Player.loadVideo(videoId, 0);
                youTubePlayer.addListener(youTubePlayerTracker);
                isPlaying = true;
            }

            @Override
            public void onStateChange(@androidx.annotation.NonNull YouTubePlayer youTubePlayer, @androidx.annotation.NonNull PlayerConstants.PlayerState playerState) {
                if (youTubePlayerTracker.getState().equals(PlayerConstants.PlayerState.PLAYING))
                    onTrackPause();
                else onTrackPlay();
            }
        });
    }
}