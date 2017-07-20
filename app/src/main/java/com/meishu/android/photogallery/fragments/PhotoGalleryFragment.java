package com.meishu.android.photogallery.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.data.FlickrFetchr;

import java.io.IOException;

/**
 * Created by Meishu on 20.07.2017.
 */

public class PhotoGalleryFragment extends Fragment {

    public static final String TAG = "PhotoGalleryFragment";
    public static final String SITE = "https://www.bignerdranch.com";

    private RecyclerView recyclerView;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute(SITE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_galery, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return v;
    }

    private class FetchItemsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (TextUtils.isEmpty(params[0]))
                return null;
            try {
                String result = new FlickrFetchr().getURLString(params[0]);
                Log.i(TAG, "Fetched contents of url: " + result);
            }
            catch (IOException e) {
                Log.e(TAG, "Error fetching", e);
            }
            return null;
        }
    }
}
