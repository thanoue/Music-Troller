package com.example.sin.musictroller_14110090;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.IDNA;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sin.musictroller_14110090.adapter.DownloadedSongListAdapter;
import com.example.sin.musictroller_14110090.adapter.OnlineSongListAdapter;
import com.example.sin.musictroller_14110090.model.DownloadSong;
import com.example.sin.musictroller_14110090.model.OnlineSong;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawerLayoutActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView lvPlayList;
    Switch swtype;
    TextView tvType;
    LoginButton btnLogin;
    String avtLink;
    ArrayList<OnlineSong> Songs;
    CallbackManager callbackManager;
    ArrayList<DownloadSong> DownloadedSongs;
    OnlineSongListAdapter onlineSongListAdapter;
    DownloadedSongListAdapter downloadedSongListAdapter;
    String url = "http://192.168.1.6:8080/androidwebservice/getdata.php";
    String urlUpdateAcount = "http://192.168.1.6:8080/androidwebservice/updateAcount.php";
    String urlInsertAcounts ="http://192.168.1.6:8080/androidwebservice/insertAcount.php";
    boolean online =true;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView imvdemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager  = CallbackManager.Factory.create();
        setContentView(R.layout.activity_drawer_layout);
        btnLogin= (LoginButton) findViewById(R.id.login_button);
       // btnLogin.setReadPermissions(Arrays.asList("public profile","email"));
        btnLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
        setLogin();

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
        GetData(url);GetDownloadedData();
        lvPlayList.setAdapter(onlineSongListAdapter);
        lvPlayList.setOnItemClickListener(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imvdemo=(ImageView)findViewById(R.id.ivDisk);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_acount);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.tv_user_name);
        nav_user.setText("Trần Kha");
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nv_acount);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem){
                switch (menuItem.getItemId()){
                    case(R.id.it_liked_song_list):
                        Toast.makeText(DrawerLayoutActivity.this,"Đã click",Toast.LENGTH_LONG).show();
                        break;
                    case (R.id.it_logout):
                        LoginManager.getInstance().logOut();
                        btnLogin.setVisibility(View.VISIBLE);
                        tologin=true;
                        NavigationView navigationView = (NavigationView) findViewById(R.id.nv_acount);
                        View hView =  navigationView.getHeaderView(0);
                        TextView nav_user = (TextView)hView.findViewById(R.id.tv_user_name);
                        nav_user.setText("Chưa đăng nhập");
                        ImageView  userpicture = (ImageView) hView.findViewById(R.id.iv_avt);
                        userpicture.setImageResource(R.drawable.avt);
                        InfoClass.setUser_avt(null);
                        InfoClass.setUser_id("");
                        InfoClass.setUser_name("");
                        avtLink="";
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
    private  void   result(){
        GraphRequest graphrequest=GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON",response.getJSONObject().toString());
                try {
                    String email = object.getString("name");
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nv_acount);
                    View hView =  navigationView.getHeaderView(0);
                    TextView nav_user = (TextView)hView.findViewById(R.id.tv_user_name);
                    nav_user.setText(email);
                  //  ProfilePictureView nav_user2 = (ProfilePictureView) hView.findViewById(R.id.friendProfilePicture);
                    ImageView  userpicture = (ImageView) hView.findViewById(R.id.iv_avt);

                    Picasso.with(DrawerLayoutActivity.this)
                            .load("https://graph.facebook.com/" +Profile.getCurrentProfile().getId() + "/picture?type=large")
                            .into( userpicture);
                    avtLink= "https://graph.facebook.com/" +Profile.getCurrentProfile().getId() + "/picture?type=large";
                    InfoClass.setUser_avt(userpicture.getDrawingCache());
                    InfoClass.setUser_id(Profile.getCurrentProfile().getId());
                    InfoClass.setUser_name(email);
                    InsertAcount(urlInsertAcounts);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,first_name");
        graphrequest.setParameters(parameters);
        graphrequest.executeAsync();
    }
    private void setLogin() {
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                result();
                btnLogin.setVisibility(View.INVISIBLE);
                tologin=false;

            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    private void UpdateAcount(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            Toast.makeText(DrawerLayoutActivity.this,"Bạn đã đăng nhập!!!",Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            Toast.makeText(DrawerLayoutActivity.this, "Lỗi rồi", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DrawerLayoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("User_id",InfoClass.getUser_id());
                param.put("User_name",InfoClass.getUser_name());
                param.put("Avt",avtLink);
                return param;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void InsertAcount(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){
                            Toast.makeText(DrawerLayoutActivity.this,"Insert XOng!",Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            UpdateAcount(urlUpdateAcount);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DrawerLayoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("user_id",InfoClass.getUser_id());
                param.put("user_name",InfoClass.getUser_name());
                param.put("avt",avtLink);
                return param;
            }
        };
        requestQueue.add(stringRequest);

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(online){
            String path = Songs.get(position).getSource_128();
            Intent i = new Intent(DrawerLayoutActivity.this, MainActivity.class);
            i.putExtra("source_128",path);
            InfoClass.setPositop(position);
            i.putExtra("url",url);
            i.putExtra("artist",Songs.get(position).getArtist());
            i.putExtra("title",Songs.get(position).getTitle());
            i.putExtra("thumbnail",Songs.get(position).getThumbnail());
            startActivityForResult(i,1);
        }
        else{
            Intent i = new Intent(DrawerLayoutActivity.this, OfflinePlayingActivity.class);
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
    Boolean tologin =true;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(tologin){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
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

    @Override
    protected void onStart() {
        LoginManager.getInstance().logOut();
        super.onStart();
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

}
