package com.melody.how.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BeanAdapter<T> extends RecyclerView.Adapter<BeanAdapter.ViewHolder> {

    // fields
    private int resource;
    protected SparseArray<T> beans;
    private Resources resources;

    private OnRecyclerViewItemClickListener onItemClickListener;

    // constructor
    protected BeanAdapter(int resource, SparseArray<T> beans, Resources resources) {
        this.resource = resource;
        this.beans = beans;
        this.resources = resources;
    }

    // inner class as view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;
        private OnRecyclerViewItemClickListener onItemClickListener;

        public ViewHolder(View itemView, BeanAdapter.OnRecyclerViewItemClickListener onItemClickListener) {
            super(itemView);
            this.view = itemView;
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                this.resource, parent, false), onItemClickListener);
    }

    @Override
    public abstract void onBindViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return this.beans.size();
    }

    // refresh data
    public BeanAdapter refresh() {
        this.notifyDataSetChanged();
        return this;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public BeanAdapter setBeans(SparseArray<T> beans) {
        this.beans = beans;
        return this;
    }

    public Resources getResources() {
        return this.resources;
    }
}
