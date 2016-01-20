package com.melody.how.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melody.how.R;
import com.melody.how.adapter.implementation.AlbumAdapter;
import com.melody.how.util.OnFragmentInteractionListener;

public class AlbumFragment extends Fragment {

    private AlbumAdapter albumAdapter;

    private OnFragmentInteractionListener mListener;

    public AlbumFragment() {

    }

    public static AlbumFragment newInstance(AlbumAdapter adapter) {
        AlbumFragment fragment = new AlbumFragment();
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
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        RecyclerView albumGridView = (RecyclerView) view.findViewById(R.id.main_gridview_album);
        albumGridView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 2);
        albumGridView.setLayoutManager(gridLayoutManager2);
        albumGridView.setItemAnimator(new DefaultItemAnimator());
        albumGridView.setAdapter(this.albumAdapter);

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

    private void setAdapter(AlbumAdapter adapter) {
        this.albumAdapter = adapter;
    }
}
