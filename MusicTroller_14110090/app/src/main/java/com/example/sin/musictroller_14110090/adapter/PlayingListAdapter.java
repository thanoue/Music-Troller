package com.example.sin.musictroller_14110090.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sin.musictroller_14110090.InfoClass;
import com.example.sin.musictroller_14110090.R;
import com.example.sin.musictroller_14110090.model.OnlineSong;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Sin on 05/20/17.
 */

public class PlayingListAdapter extends BaseAdapter {
    private ArrayList<OnlineSong> Songs;
    private Context context;
    private int layout;

    public PlayingListAdapter(ArrayList<OnlineSong> songs, Context context, int layout) {
        Songs = songs;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return Songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PlayingListAdapter.Holder holder;
        if (convertView == null) {
            holder = new PlayingListAdapter.Holder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
            holder.imAVT= (ImageView) convertView.findViewById(R.id.ivAVT);
            convertView.setTag(holder);
        } else {
            holder = (PlayingListAdapter.Holder) convertView.getTag();
        }
        String  artist = Songs.get(position).getArtist();
        String  title = Songs.get(position).getTitle();
        String imgFile = Songs.get(position).getThumbnail();
        if(position== InfoClass.getPositop())
        {
          //  holder.imAVT.setImageResource(R.drawable.playing_anim);
            holder.imAVT.setBackgroundResource(R.drawable.playing_anim);
            AnimationDrawable animationDrawable=(AnimationDrawable)holder.imAVT.getBackground();
            animationDrawable.start();
        }
        else {
            new PlayingListAdapter.DownloadImageTask(holder.imAVT).execute(imgFile);
        }

        holder.tvArtist.setText(artist);
        holder.tvTitle.setText(title);
        return convertView;
    }
    private class Holder {
        TextView tvTitle;
        ImageView imAVT;
        TextView tvArtist;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
            bmImage.setImageBitmap(result);
        }
    }
}
