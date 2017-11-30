package com.example.sin.musictroller_14110090;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;

import java.io.IOException;

/**
 * Created by Sin on 05/19/17.
 */

public class MediaPlayer implements android.media.MediaPlayer.OnCompletionListener{
    private static final MediaPlayer ourInstance = new MediaPlayer();
    public static final int PLAYER_IDLE = -1;
    public static final int PLAYER_PLAY = 1;
    public static final int PLAYER_PAUSE = 2;
    public static android.media.MediaPlayer mediaPlayer;
    public static int state;
    public static MediaPlayer.OnCompletionListener onCompletionListener;
    static boolean isEnd;
    public static MediaMetadataRetriever retriever;
    public static int getState() {
        return state;
    }
    public static  void setState(int state) {
        MediaPlayer.state = state;
    }

    public static MediaPlayer getInstance() {
        return ourInstance;
    }
    public static int getDuration() {
        return duration/ 1000;
    }
    public static int duration;
    public static void setDuration(int duration) {
        MediaPlayer.duration = duration;
    }

    public static void setOnCompletionListener(OfflinePlayingActivity offlinePlayingActivity) {
        MediaPlayer.onCompletionListener = onCompletionListener;
    }

    public static void setup(String path) {
        try {
            state = PLAYER_IDLE;
            mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            setDuration(mediaPlayer.getDuration());
            // duration = mediaPlayer.getDuration();
            // mediaPlayer.release();
            mediaPlayer.setOnCompletionListener(MediaPlayer.getInstance());
            isEnd = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public interface OnCompletionListener{
        void OnEndMusic();
    }
    public static void setup_online(final String path){
        try{
            state = PLAYER_IDLE;
            mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            // duration = mediaPlayer.getDuration();
            //     mediaPlayer.release();
            mediaPlayer.setOnCompletionListener(MediaPlayer.getInstance());
            mediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(android.media.MediaPlayer mp) {
                    // new LoadImage().execute(path);
                    isEnd=true;     mp.start();
                    if (state == PLAYER_IDLE || state == PLAYER_PAUSE) {
                        state = PLAYER_PLAY;
                        setDuration(mediaPlayer.getDuration());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  static  int     getTimeTotal(String url) {
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(url);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeInmillisec = Integer.parseInt( time );
        int  duration = timeInmillisec / 1000;
        return duration;
    }
    private MediaPlayer() {
    }
    public static  void play() {
        if (state == PLAYER_IDLE || state == PLAYER_PAUSE) {
            state = PLAYER_PLAY;
            mediaPlayer.start();
        }
    }
    public  static  void stop() {
        if (state == PLAYER_PLAY || state == PLAYER_PAUSE) {
            state = PLAYER_IDLE;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pause() {
        if (state == PLAYER_PLAY) {
            mediaPlayer.pause();
            state = PLAYER_PAUSE;
        }
    }

    public static int getTimeCurrent() {
        if (state != PLAYER_IDLE) {
            return mediaPlayer.getCurrentPosition() / 1000;
        } else
            return 0;
    }

    public static void seek(int time) {
        mediaPlayer.seekTo(time);
    }
    @Override
    public   void onCompletion(android.media.MediaPlayer mp) {
        // khi ket thuc bai hat no se vào hàm này, ta viết 1 interface để cho activity
        // biết khi nào kết thúc bài hát để chuyển bài
        // gọi interface
        if(isEnd) {
            onCompletionListener.OnEndMusic();
            isEnd = false;
        }
    }
    public static void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener){
        MediaPlayer.onCompletionListener = onCompletionListener;
    }



}
