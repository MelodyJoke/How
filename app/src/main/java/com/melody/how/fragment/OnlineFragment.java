package com.melody.how.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melody.how.R;
import com.melody.how.adapter.implementation.SongAdapter;
import com.melody.how.util.OnFragmentInteractionListener;

public class OnlineFragment extends Fragment {

    private SongAdapter songAdapter;

    private com.melody.how.util.OnFragmentInteractionListener mListener;

    public OnlineFragment() {

    }

    public static OnlineFragment newInstance(SongAdapter adapter) {
        OnlineFragment fragment = new OnlineFragment();
        fragment.setAdapter(adapter);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online, container, false);
        RecyclerView songListView = (RecyclerView) view.findViewById(R.id.main_listview_list);
        songListView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(inflater.getContext());
        songListView.setLayoutManager(linearLayoutManager);
        songListView.setItemAnimator(new DefaultItemAnimator());
        songListView.setAdapter(this.songAdapter);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setAdapter(SongAdapter adapter) {
        this.songAdapter = adapter;
    }
}
