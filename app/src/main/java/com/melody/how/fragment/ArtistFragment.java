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
import com.melody.how.adapter.implementation.ArtistAdapter;
import com.melody.how.util.OnFragmentInteractionListener;

public class ArtistFragment extends Fragment {

    private ArtistAdapter artistAdapter;

    private OnFragmentInteractionListener mListener;

    public ArtistFragment() {

    }

    public static ArtistFragment newInstance(ArtistAdapter adapter) {
        ArtistFragment fragment = new ArtistFragment();
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
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        RecyclerView albumGridView = (RecyclerView) view.findViewById(R.id.main_gridview_artist);
        albumGridView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        albumGridView.setLayoutManager(gridLayoutManager);
        albumGridView.setItemAnimator(new DefaultItemAnimator());
        albumGridView.setAdapter(this.artistAdapter);

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

    private void setAdapter(ArtistAdapter adapter) {
        this.artistAdapter = adapter;
    }
}
