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
import com.melody.how.adapter.implementation.QueueAdapter;
import com.melody.how.util.OnFragmentInteractionListener;

public class QueueFragment extends Fragment {

    private QueueAdapter queueAdapter;

    private OnFragmentInteractionListener mListener;

    public QueueFragment() {

    }

    public static QueueFragment newInstance(QueueAdapter adapter) {
        QueueFragment fragment = new QueueFragment();
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
        View view = inflater.inflate(R.layout.fragment_queue, container, false);
        RecyclerView queueGridView = (RecyclerView) view.findViewById(R.id.main_gridview_queue);
        queueGridView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        queueGridView.setLayoutManager(gridLayoutManager);
        queueGridView.setItemAnimator(new DefaultItemAnimator());
        queueGridView.setAdapter(this.queueAdapter);

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

    private void setAdapter(QueueAdapter adapter) {
        this.queueAdapter = adapter;
    }
}
