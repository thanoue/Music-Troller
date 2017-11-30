package com.example.sin.musictroller_14110090.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sin.musictroller_14110090.R;
import com.example.sin.musictroller_14110090.model.DownloadSong;
import com.example.sin.musictroller_14110090.model.OnlineSong;

import java.util.ArrayList;

/**
 * Created by Sin on 05/20/17.
 */

public class DownloadedSongListAdapter extends BaseAdapter {
    private ArrayList<DownloadSong> Songs;
    private Context context;
    private int layout;

    public DownloadedSongListAdapter(ArrayList<DownloadSong> songs, Context context, int layout) {
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
        final DownloadedSongListAdapter.Holder holder;
        if (convertView == null) {
            holder = new DownloadedSongListAdapter.Holder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.tv_artist);
            holder.imAVT= (ImageView) convertView.findViewById(R.id.ivAVT);
            convertView.setTag(holder);
        } else {
            holder = (DownloadedSongListAdapter.Holder) convertView.getTag();
        }
        String  artist = Songs.get(position).getArtist();
        String  title = Songs.get(position).getTitle();
        if(Songs.get(position).getThumbnail()!=null)
            holder.imAVT.setImageBitmap(Songs.get(position).getThumbnail());
        else
            holder.imAVT.setImageResource(R.drawable.disk);
        holder.tvArtist.setText(artist);
        holder.tvTitle.setText(title);
        return convertView;
    }
    private class Holder {
        TextView tvTitle;
        ImageView imAVT;
        TextView tvArtist;
    }
}

