package com.example.sin.musictroller_14110090;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.sin.musictroller_14110090.adapter.DownloadedSongListAdapter;
import com.example.sin.musictroller_14110090.adapter.OnlineSongListAdapter;
import com.example.sin.musictroller_14110090.adapter.PlayingListAdapter;
import com.example.sin.musictroller_14110090.model.DownloadSong;
import com.example.sin.musictroller_14110090.model.OnlineSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SongListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView lvPlayList;
    Switch swtype;
    TextView tvType;
    ImageButton btndemo;
    ArrayList<OnlineSong> Songs;
    ArrayList<DownloadSong> DownloadedSongs;
    OnlineSongListAdapter onlineSongListAdapter;
    DownloadedSongListAdapter downloadedSongListAdapter;
    String url = "http://192.168.1.6:8080/androidwebservice/getdata.php";
    boolean online =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        lvPlayList = (ListView) findViewById(R.id.lvPlayList);

        Songs =new ArrayList<OnlineSong>();
        DownloadedSongs=new ArrayList<DownloadSong>();
        onlineSongListAdapter = new OnlineSongListAdapter(Songs,this,R.layout.song_item);
        downloadedSongListAdapter = new DownloadedSongListAdapter(DownloadedSongs,this,R.layout.downloaded_song_item);
        swtype = (Switch)findViewById(R.id.switch1);
        tvType=(TextView)findViewById(R.id.tv_type_list);
        swtype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(online){
                    online=false;
                    tvType.setText("Downloaded");
                    lvPlayList.setAdapter(downloadedSongListAdapter);
                }
                else
                {
                    online=true;
                    tvType.setText("Online");
                    lvPlayList.setAdapter(onlineSongListAdapter);
                }
            }
        });
        GetData(url);
        lvPlayList.setAdapter(onlineSongListAdapter);
        lvPlayList.setOnItemClickListener(this);


    }
    private void GetDownloadedData(){
     //   DownloadedSongs = new ArrayList<DownloadSong>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download";
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
                DownloadedSongs.add(song);
               // size=offlineSong.size();
              //  mmr.release();

            }
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
                                Songs.add(new OnlineSong(obj.getInt("id"),obj.getString("Title"),obj.getString("artist"),
                                        obj.getString("thumbnail"),obj.getString("source_128"),obj.getString("Link"),obj.getString("link_download_128")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        onlineSongListAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(online){
            String path = Songs.get(position).getSource_128();
            Intent i = new Intent(SongListActivity.this, MainActivity.class);
            i.putExtra("source_128",path);
            InfoClass.setPositop(position);
            i.putExtra("url",url);
            i.putExtra("artist",Songs.get(position).getArtist());
            i.putExtra("title",Songs.get(position).getTitle());
            i.putExtra("thumbnail",Songs.get(position).getThumbnail());
            startActivityForResult(i,1);
        }
        else{
            Intent i = new Intent(SongListActivity.this, OfflinePlayingActivity.class);
            String path = DownloadedSongs.get(position).getUrl();
            i.putExtra("url",path);
            InfoClass.setPositop(position);
            startActivityForResult(i,2);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Bitmap bmImage;

        public DownloadImageTask(Bitmap bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage=result;
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==1){
            Toast.makeText(this,"Trở về từ online",Toast.LENGTH_LONG).show();
            DownloadedSongs=new ArrayList<DownloadSong>();
            GetDownloadedData();
            downloadedSongListAdapter = new DownloadedSongListAdapter(DownloadedSongs,this,R.layout.downloaded_song_item);
            downloadedSongListAdapter.notifyDataSetChanged();
            lvPlayList.setAdapter(downloadedSongListAdapter);
            swtype.setChecked(true);
            online=false;tvType.setText("Download");
          //  infoClass.adapter.notifyDataSetChanged();
          //  lvPlayList.setAdapter(infoClass.adapter);
           // swType.setChecked(false);
        }
        else{
            if(requestCode==2 && resultCode==2){
               swtype.setChecked(false);
                online=true;tvType.setText("Online");
            }
        }
    }
}
