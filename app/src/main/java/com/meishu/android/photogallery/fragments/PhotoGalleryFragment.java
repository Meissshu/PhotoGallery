package com.meishu.android.photogallery.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.dataModel.GalleryItem;
import com.meishu.android.photogallery.dataUtils.FlickrFetchr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meishu on 20.07.2017.
 */

public class PhotoGalleryFragment extends Fragment {

    public static final String TAG = "PhotoGalleryFragment";
 //   public static final String SITE = "https://www.bignerdranch.com";

    private RecyclerView recyclerView;
    private List<GalleryItem> galleryItems = new ArrayList<>();

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_galery, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        if (isAdded())
            recyclerView.setAdapter(new PhotoAdapter(galleryItems));
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
           return new FlickrFetchr().fetchItems(getActivity());
        }

        @Override
        protected void onPostExecute(List<GalleryItem> list) {
            galleryItems = list;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public PhotoHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void bind(GalleryItem item) {
            textView.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItemList;

        public PhotoAdapter(List<GalleryItem> list) {
            galleryItemList = list;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.bind(galleryItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return galleryItemList.size();
        }
    }
}
