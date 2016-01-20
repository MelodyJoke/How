package com.melody.how.adapter.implementation;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melody.how.R;
import com.melody.how.adapter.BeanAdapter;
import com.melody.how.bean.Queue;
import com.melody.how.util.AnimationUtils;
import com.melody.how.util.ImageCallBack;
import com.melody.how.util.ImageLoader;
import com.melody.how.util.Values;

public class QueueAdapter extends BeanAdapter<Queue> {


    // constructor
    public QueueAdapter(int resource, SparseArray<Queue> albums, Resources resources) {
        super(resource, albums, resources);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Queue queue = this.beans.get(beans.keyAt(position));

        holder.view.setTag(queue.getName());

        RelativeLayout layout = (RelativeLayout) holder.view;
        final ImageView head = (ImageView) layout.findViewById(R.id.grid_item_head);
        TextView name = (TextView) layout.findViewById(R.id.grid_item_name);

        new ImageLoader(getResources(), fixImagePath(queue.getName()),
                Values.BOUNDS_DEFAULT, Values.BOUNDS_DEFAULT).loadImage(new ImageCallBack() {
            @Override
            public void getDrawable(Drawable drawable) {
                head.setImageDrawable(drawable);

                head.startAnimation(AnimationUtils.getAnimationSet(
                        AnimationUtils.getScaleAnimation(0.95f, 1, 0.95f, 1, 500),
                        AnimationUtils.getAlphaAnimation(0.8f, 1, 500)));
            }
        });
        name.setText(queue.getName());
    }

    public static String fixImagePath(String name) {
        return "queues/" + name + ".png";
    }
}
