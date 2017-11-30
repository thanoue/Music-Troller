package com.example.sin.musictroller_14110090;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.sin.musictroller_14110090.adapter.OnlineSongListAdapter;
import com.example.sin.musictroller_14110090.adapter.PlayingListAdapter;
import com.example.sin.musictroller_14110090.model.OnlineSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.sin.musictroller_14110090.MediaPlayer.PLAYER_PLAY;
import static com.example.sin.musictroller_14110090.MediaPlayer.play;
import static com.example.sin.musictroller_14110090.R.drawable.ic_queue_music_black_24dp;
import static com.example.sin.musictroller_14110090.R.drawable.repeat;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, AdapterView.OnItemClickListener {
    private TextView tvTitle;
    private ListView lvPlaylist;
    private TextView tvArtist;
    private TextView tvTimeProcess;
    private SeekBar sbProcess;
    private TextView tvTimeTotal;
    private ImageView ivShuffle;
    private ImageView ivPrevious;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivRepeat;
    private boolean repeat=false;
    Animation anim;
    private ImageView btnlike;
    private ImageView btnDownload;
    Boolean suffle = false;
    ImageView imgDis;ArrayList<OnlineSong> onlinePlayingSong;
    private int timeProcess;
    private int timeTotal;
    //   private MusicPlayer musicPlayer;
    private boolean isRunning;
    private int UPDATE_TIME = 1;
    private int timeCurrent;
    private int position;
    private TabHost tab;
    private ProgressDialog progressDialog;
    public final int DIALOG=1;
    String urlInsertLike ="http://192.168.1.6:8080/androidwebservice/insertLike.php";
    String urlDislike = "http://192.168.1.6:8080/androidwebservice/deleteLike.php";
    String url="";PlayingListAdapter playingListAdapter;
    // private  int size;
  //  private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anim = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        MediaPlayer.setOnCompletionListener(this);
        if (MediaPlayer.state == PLAYER_PLAY) {
            MediaPlayer.pause();
        }
        Intent intent = getIntent();
        String path = intent.getStringExtra("source_128");
        String title= intent.getStringExtra("title");
        String artist= intent.getStringExtra("artist");
        String thumbnail =intent.getStringExtra("thumbnail");
        url = intent.getStringExtra("url");
        position=InfoClass.getPositop();
        setResult(1,intent);
        initViews();initListeners();
        tvTitle.setText(title);tvArtist.setText(artist);
        Glide.with(this).load(thumbnail).into(imgDis);
        //tabs=(TabWidget)findViewById()
        onlinePlayingSong=new ArrayList<OnlineSong>();
        playingListAdapter = new PlayingListAdapter(onlinePlayingSong,this,R.layout.downloaded_song_item);
        lvPlaylist.setAdapter(playingListAdapter);
        GetData(url);

        playMusic(path);
        btnDownload=(ImageView) findViewById(R.id.iv_download);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartDownload(onlinePlayingSong.get(position).getSource_128());
            }
        });



    }
    private void StartDownload(String url){
        new DownloadTask().execute(url);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case  DIALOG:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Download file...");
                progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.show();
                return  progressDialog;
            default:
                return null;
        }
    }
    private void GetData(String url){
        final RequestQueue requestQueue= Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0;i<response.length();i++){
                            try {
                                JSONObject obj =response.getJSONObject(i);
                                onlinePlayingSong.add(new OnlineSong(obj.getInt("id"),obj.getString("Title"),obj.getString("artist"),
                                        obj.getString("thumbnail"),obj.getString("source_128"),obj.getString("Link"),obj.getString("link_download_128")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        playingListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void playMusic(String path){
        if (MediaPlayer.getState() == PLAYER_PLAY) {
            MediaPlayer.stop();
        }
        MediaPlayer.setup_online(path);
        //  MediaPlayer.play();
        imgDis.startAnimation(anim);
        ivPlay.setImageResource(R.drawable.pause);
        isRunning = true;
        tvTimeTotal.setText(getTimeFormat(MediaPlayer.getDuration()));
        sbProcess.setMax(MediaPlayer.getDuration());
        imgDis.setImageResource(R.drawable.disk);
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
            case R.id.iv_Like:
                LikeASong(urlInsertLike);
                break;
            default:
                break;
        }
    }
    private void previousMusic() {
            if (suffle) {
                Random rand = new Random();
                position = rand.nextInt(onlinePlayingSong.size());
                ivPlay.setImageResource(R.drawable.pause);
                InfoClass.setPositop(position);
                playMusic(onlinePlayingSong.get(position).getSource_128());
            } else {
                position--;
                if (repeat) {
                    if(position>=0){
                        InfoClass.setPositop(position);
                        playMusic(onlinePlayingSong.get(position).getSource_128());
                    }
                    else
                    {
                        position=onlinePlayingSong.size()-1;
                        InfoClass.setPositop(position);
                        playMusic(onlinePlayingSong.get(position).getSource_128());
                    }

                } else {
                    if (position < 0) {
                        position++;
                        InfoClass.setPositop(position);
                        playMusic(onlinePlayingSong.get(position).getSource_128());
                    } else {
                        InfoClass.setPositop(position);
                        playMusic(onlinePlayingSong.get(position).getSource_128());
                    }
                }
            }
         //   playingListAdapter.notifyDataSetChanged();
        tvTitle.setText(onlinePlayingSong.get(position).getTitle());
        tvArtist.setText(onlinePlayingSong.get(position).getArtist());
        Glide.with(this).load(onlinePlayingSong.get(position).getThumbnail()).into(imgDis);
        playingListAdapter = new PlayingListAdapter(onlinePlayingSong,this,R.layout.song_item);
        lvPlaylist.setAdapter(playingListAdapter);
    }

    private void nextMusic() {
            if (suffle) {
                Random rand = new Random();
                position = rand.nextInt(onlinePlayingSong.size());
                ivPlay.setImageResource(R.drawable.play);
                InfoClass.setPositop(position);
                playMusic(onlinePlayingSong.get(position).getSource_128());
            } else {
                position++;
                if (position >= onlinePlayingSong.size()) {
                    if (repeat) {
                        position = 0;
                        InfoClass.setPositop(position);
                        playMusic(onlinePlayingSong.get(position).getSource_128());
                    } else {
                        position--;
                        InfoClass.setPositop(position);
                        playMusic(onlinePlayingSong.get(position).getSource_128());
                    }
                } else {
                    InfoClass.setPositop(position);
                    playMusic(onlinePlayingSong.get(position).getSource_128());
                }
            }
          //  playingListAdapter.notifyDataSetChanged();
        tvTitle.setText(onlinePlayingSong.get(position).getTitle());
        tvArtist.setText(onlinePlayingSong.get(position).getArtist());
        Glide.with(this).load(onlinePlayingSong.get(position).getThumbnail()).into(imgDis);
        playingListAdapter = new PlayingListAdapter(onlinePlayingSong,this,R.layout.song_item);
        lvPlaylist.setAdapter(playingListAdapter);
    }
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
        TabHost.TabSpec tap3=tab.newTabSpec("tab3");
        tap3.setIndicator("",getResources().getDrawable(R.drawable.cmt));
        tap3.setContent(R.id.tab3);
        tab.addTab(tap3);
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
        btnlike=(ImageView)findViewById(R.id.iv_Like);
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
        btnlike.setOnClickListener(this);

    }
    @Override
    public void finish() {

        super.finish();
    }
    @Override
    public void OnEndMusic() {
        nextMusic();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position=position;
        InfoClass.setPositop(position);
        playMusic(onlinePlayingSong.get(position).getSource_128());
        tvTitle.setText(onlinePlayingSong.get(position).getTitle());
        tvArtist.setText(onlinePlayingSong.get(position).getArtist());
        tab.setCurrentTab(1);
        Glide.with(this).load(onlinePlayingSong.get(position).getThumbnail()).into(imgDis);
        playingListAdapter = new PlayingListAdapter(onlinePlayingSong,this,R.layout.song_item);
        lvPlaylist.setAdapter(playingListAdapter);
    }
    private void DislikeASong(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            Toast.makeText(MainActivity.this,"bạn đã hết thích bài hát này",Toast.LENGTH_LONG).show();
                            btnlike.setImageResource(R.drawable.like);

                        }
                        else
                        {
                            DislikeASong(urlDislike);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("song_id",String.valueOf(onlinePlayingSong.get(position).getSong_id()));
                param.put("user_id",InfoClass.getUser_id());
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void LikeASong(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            Toast.makeText(MainActivity.this,"bạn đã thích bài hát này",Toast.LENGTH_LONG).show();
                            btnlike.setImageResource(R.drawable.liked);

                        }
                        else
                        {
                            DislikeASong(urlDislike);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("song_id",String.valueOf(onlinePlayingSong.get(position).getSong_id()));
                param.put("user_id",InfoClass.getUser_id());
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }
    private class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... sUrl) {
            int count;
            try{
                URL url = new URL(sUrl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtoffile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC","Length of file :"+lenghtoffile);
                InputStream input = new BufferedInputStream(url.openStream(),8192);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/"+onlinePlayingSong.get(position).getTitle()+".mp3" );
                byte data[] = new  byte[1024];
                long total =0;
                while ((count=input.read(data))!=-1){
                    total+=count;
                    publishProgress("" +  (int) ((total*100)/lenghtoffile));
                    output.write(data,0,count);

                }
                output.flush();
                output.close();
                input.close();

            }
            catch (Exception e){
                Log.e("Error:11111111111111 ", e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            dismissDialog(DIALOG);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("ANDRO_SYNC",values[0]);
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

    }
}
