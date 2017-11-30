package com.example.sin.musictroller_14110090;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.sin.musictroller_14110090.adapter.DownloadedPlayingListAdapter;
import com.example.sin.musictroller_14110090.adapter.DownloadedSongListAdapter;
import com.example.sin.musictroller_14110090.adapter.PlayingListAdapter;
import com.example.sin.musictroller_14110090.model.DownloadSong;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.example.sin.musictroller_14110090.MediaPlayer.PLAYER_PLAY;

public class OfflinePlayingActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, AdapterView.OnItemClickListener {
    private TextView tvTitle;
    private ListView lvPlaylist;
     private  DownloadedPlayingListAdapter downloadedPlayingListAdapter;
    private TextView tvArtist;
    private TextView tvTimeProcess;
    private SeekBar sbProcess;
    private TextView tvTimeTotal;
    private ImageView ivShuffle;
    private ImageView ivPrevious;
    private ImageView ivPlay;
    private boolean isRunning;
    private int UPDATE_TIME = 1;
    private int timeCurrent;
    private ImageView ivNext;
    private ImageView ivRepeat;
 //   private boolean repeat=false;
    private TabHost tab;
    Animation anim;

    ArrayList<DownloadSong> downloadSongs;
    private ImageView btnDownload;
    Boolean suffle = false;
    ImageView imgDis;
    int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_playing);
        initViews();
        Intent intent = getIntent();
        setResult(2,intent);
        anim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        position = InfoClass.getPositop();
        MediaPlayer.setOnCompletionListener(this);
        if (MediaPlayer.state == PLAYER_PLAY) {
            MediaPlayer.pause();
        }
        initListeners();
        String path = intent.getStringExtra("url");
        downloadSongs= new ArrayList<DownloadSong>();
        GetDownloadedData();
        downloadedPlayingListAdapter = new DownloadedPlayingListAdapter(downloadSongs,this,R.layout.downloaded_song_item);
        lvPlaylist.setAdapter(downloadedPlayingListAdapter);
        playMusic(path);


    }
    private void playMusic(String path){
        if (MediaPlayer.getState() == PLAYER_PLAY) {
            MediaPlayer.stop();
        }
        MediaPlayer.setup(path);
        MediaPlayer.play();

        ivPlay.setImageResource(R.drawable.pause);
        // set up tên bài hát + ca sĩ
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        //Image im = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        //   String artist = infoClass.Songs.get(position).getArtist();
        //  String title = infoClass.Songs.get(position).getTitle();

        tvArtist.setText(artist);
        tvTitle.setText(title);
        isRunning = true;

        // set up time
        // total time

        tvTimeTotal.setText(getTimeFormat(MediaPlayer.getDuration()));
        // process time // set up seekbar
        sbProcess.setMax(MediaPlayer.getDuration());
        if (downloadSongs.get(position).getThumbnail() != null)
            imgDis.setImageBitmap(downloadSongs.get(position).getThumbnail());
        else
            imgDis.setImageResource(R.drawable.disk);
        imgDis.startAnimation(anim);
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {

                    Message message = new Message();
                    message.what = UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }


            }
        }).start();

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvTimeTotal.setText(getTimeFormat(MediaPlayer.getDuration()));
            // process time // set up seekbar
            sbProcess.setMax(MediaPlayer.getDuration());
            if (msg.what == UPDATE_TIME) {
                timeCurrent = MediaPlayer.getTimeCurrent();
                tvTimeProcess.setText(getTimeFormat(timeCurrent));
                sbProcess.setProgress(timeCurrent);
            }
        }
    };
    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }
    private void initListeners() {

        ivShuffle.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        sbProcess.setOnSeekBarChangeListener(this);
        imgDis.setOnClickListener(this);
        lvPlaylist.setOnItemClickListener(this);

    }
    Boolean repeat = false;
    private void GetDownloadedData(){

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download" ;
        //  File fai = Environment.getExternalStorageDirectory();
        File file = new File(path);
        File[] files = file.listFiles(); // lay tat ca cac file trong thu muc. ở đây là Download
        for (int i = 0; i < files.length; i++) {

            // doc tat ca cac file co trong download them vao list nhac
            String s = files[i].getName();
            if (s.endsWith(".mp3")) {
                DownloadSong song = new DownloadSong();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(files[i].getAbsolutePath());
                song.setUrl(files[i].getAbsolutePath());
                song.setArtist( mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                song.setTitle(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                //  mmr.release();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(files[i].getAbsolutePath());
                byte[] art = retriever.getEmbeddedPicture();

                if( art != null ){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                    song.setThumbnail(bitmap);
                }
                else{
                    song.setThumbnail(null);
                }
                retriever.release();
               mmr.release();
                downloadSongs.add(song);
                // size=offlineSong.size();
                //  mmr.release();

            }
        }
    }
    @Override
    public void finish() {

        super.finish();
    }
    private void initViews() {
        tab= (TabHost) findViewById(R.id.tabmain);
        tab.setup();
        TabHost.TabSpec tap1=tab.newTabSpec("tab1");
        tap1.setIndicator("",getResources().getDrawable(R.drawable.playing_list));
        tap1.setContent(R.id.tab1);
        tab.addTab(tap1);
        TabHost.TabSpec tap2=tab.newTabSpec("tab2");
        tap2.setIndicator("",getResources().getDrawable(R.drawable.play_24));
        tap2.setContent(R.id.tab2);
        tab.addTab(tap2);
        tab.setCurrentTab(1);
        imgDis = (ImageView) findViewById(R.id.ivDisk);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvTimeProcess = (TextView) findViewById(R.id.tv_time_process);
        sbProcess = (SeekBar) findViewById(R.id.sb_process);
        tvTimeTotal = (TextView) findViewById(R.id.tv_time_total);
        ivShuffle = (ImageView) findViewById(R.id.iv_shuffle);
        ivPrevious = (ImageView) findViewById(R.id.iv_previous);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        ivRepeat = (ImageView) findViewById(R.id.iv_repeat);
        lvPlaylist= (ListView) findViewById(R.id.lv_song_playing_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_next:
                nextMusic();
                break;

            case R.id.iv_play:
                if (MediaPlayer.getState() == PLAYER_PLAY) {
                    ivPlay.setImageResource(R.drawable.play);
                    // imgDis.startAnimation(anim);
                    imgDis.clearAnimation();
                    MediaPlayer.pause();
                } else {
                    ivPlay.setImageResource(R.drawable.pause);
                    imgDis.startAnimation(anim);
                    MediaPlayer.play();
                }
                break;

            case R.id.iv_previous:
                previousMusic();
                break;
            case R.id.iv_repeat:
                if (!repeat) {
                    repeat = true;
                    ivRepeat.setImageResource(R.drawable.repeatall);
                } else {
                    repeat = false;
                    ivRepeat.setImageResource(R.drawable.repeat);
                }
                break;
            case R.id.iv_shuffle:
                if (!suffle) {
                    suffle = true;
                    ivShuffle.setImageResource(R.drawable.shuffled);
                } else {
                    suffle = false;
                    ivShuffle.setImageResource(R.drawable.shuffle);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position=position;
        InfoClass.setPositop(position);
        playMusic(downloadSongs.get(position).getUrl());
//        tvTitle.setText(downloadSongs.get(position).getTitle());
//        tvArtist.setText(downloadSongs.get(position).getArtist());
//        if(downloadSongs.get(position).getThumbnail()!=null)
//            imgDis.setImageBitmap(downloadSongs.get(position).getThumbnail());
//        else
//            imgDis.setImageResource(R.drawable.disk);
        tab.setCurrentTab(1);
       // downloadedPlayingListAdapter = new DownloadedPlayingListAdapter(downloadSongs,this,R.layout.downloaded_song_item);
        downloadedPlayingListAdapter.notifyDataSetChanged();
        lvPlaylist.setAdapter(downloadedPlayingListAdapter);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (timeCurrent != progress && timeCurrent != 0)
            MediaPlayer.seek(sbProcess.getProgress() * 1000);
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void nextMusic() {
            if (suffle) {
                Random rand = new Random();
                position = rand.nextInt(downloadSongs.size());
                ivPlay.setImageResource(R.drawable.play);
                InfoClass.setPositop(position);
                playMusic(downloadSongs.get(position).getUrl());
            } else {
                position++;

                if (position >= downloadSongs.size()) {
                    if (repeat) {
                        position = 0;
                        InfoClass.setPositop(position);
                        playMusic(downloadSongs.get(position).getUrl());
                    } else {
                        position--;
                        InfoClass.setPositop(position);
                        playMusic(downloadSongs.get(position).getUrl());
                    }
                } else {
                    InfoClass.setPositop(position);
                    playMusic(downloadSongs.get(position).getUrl());
                }
            }
//        tvTitle.setText(downloadSongs.get(position).getTitle());
//        tvArtist.setText(downloadSongs.get(position).getArtist());
//        if(downloadSongs.get(position).getThumbnail()!=null)
//            imgDis.setImageBitmap(downloadSongs.get(position).getThumbnail());
//        else
//            imgDis.setImageResource(R.drawable.disk);
        tab.setCurrentTab(1);
        downloadedPlayingListAdapter.notifyDataSetChanged();
      //  downloadedPlayingListAdapter = new DownloadedPlayingListAdapter(downloadSongs,this,R.layout.downloaded_song_item);
        lvPlaylist.setAdapter(downloadedPlayingListAdapter);
    }
    private void previousMusic() {
            if (suffle) {
                Random rand = new Random();
                position = rand.nextInt(downloadSongs.size());
                ivPlay.setImageResource(R.drawable.pause);
                InfoClass.setPositop(position);
                playMusic(downloadSongs.get(position).getUrl());

            } else {
                position--;
                if (repeat) {

                    InfoClass.setPositop(position);
                    playMusic(downloadSongs.get(position).getUrl());
                } else {
                    if (position < 0) {
                        position++;
                        InfoClass.setPositop(position);
                        playMusic(downloadSongs.get(position).getUrl());
                    } else {
                        InfoClass.setPositop(position);
                        playMusic(downloadSongs.get(position).getUrl());
                    }
                }
            }
//        tvTitle.setText(downloadSongs.get(position).getTitle());
//        tvArtist.setText(downloadSongs.get(position).getArtist());
//        if(downloadSongs.get(position).getThumbnail()!=null)
//            imgDis.setImageBitmap(downloadSongs.get(position).getThumbnail());
//        else
//            imgDis.setImageResource(R.drawable.disk);
        downloadedPlayingListAdapter.notifyDataSetChanged();
        tab.setCurrentTab(1);
        downloadedPlayingListAdapter = new DownloadedPlayingListAdapter(downloadSongs,this,R.layout.downloaded_song_item);
        lvPlaylist.setAdapter(downloadedPlayingListAdapter);

    }
    @Override
    public void OnEndMusic() {
        nextMusic();
    }
}
