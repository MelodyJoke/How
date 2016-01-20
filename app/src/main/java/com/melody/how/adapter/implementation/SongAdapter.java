package com.melody.how.adapter.implementation;

import android.content.res.Resources;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melody.how.R;
import com.melody.how.adapter.BeanAdapter;
import com.melody.how.bean.Song;

public class SongAdapter extends BeanAdapter<Song> {

    private static int nowPlaying;

    // constructor
    public SongAdapter(int resource, SparseArray<Song> songs, Resources resources) {
        super(resource, songs, resources);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = this.beans.get(beans.keyAt(position));

        View view = holder.view;
        view.setTag(song.getId());
        if (song.getId() == nowPlaying)
            holder.view.setBackgroundResource(R.color.playingBg);
        else
            holder.view.setBackgroundResource(R.drawable.list_selector_bg);

        RelativeLayout layout = (RelativeLayout) holder.view;
        TextView order = (TextView) layout.findViewById(R.id.item_order);
        TextView name = (TextView) layout.findViewById(R.id.item_name);
        TextView info = (TextView) layout.findViewById(R.id.item_info);
        ImageButton action = (ImageButton) layout.findViewById(R.id.item_action);

        order.setText(String.valueOf(song.getId()));
        name.setText(song.getDisplayName());
        info.setText(song.getArtist() + " - " + song.getAlbum());
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });
    }

    public void setNowPlaying(int id) {
        this.nowPlaying = id;
    }
}
