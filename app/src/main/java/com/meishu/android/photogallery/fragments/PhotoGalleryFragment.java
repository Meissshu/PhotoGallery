package com.meishu.android.photogallery.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.dataModel.GalleryItem;
import com.meishu.android.photogallery.dataUtils.FlickrFetchr;
import com.meishu.android.photogallery.dataUtils.QueryPreferencesUtils;
import com.meishu.android.photogallery.dataUtils.ThumbnailDownloader;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Meishu on 20.07.2017.
 */

public class PhotoGalleryFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener {

    public static final String TAG = "PhotoGalleryFragment";
    private static final int COLUMN_WIDGHT = 240;
    //   public static final String SITE = "https://www.bignerdranch.com";

    private RecyclerView recyclerView;
    private List<GalleryItem> galleryItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;


    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        Handler responseHandler = new Handler();
        thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        thumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bind(drawable);
            }
        });
        thumbnailDownloader.start();
        thumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thumbnailDownloader.clearQueue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_galery, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        setupAdapter();
        return v;
    }

    private void hideKeyboardFromFragment(Activity activity) {
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Text submit from search view: " + query);
                QueryPreferencesUtils.setStoredQuery(getActivity(), query);
                hideKeyboardFromFragment(getActivity());
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Text changed: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferencesUtils.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_item_clear:
                QueryPreferencesUtils.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateItems() {
        String query = QueryPreferencesUtils.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if (isAdded())
            recyclerView.setAdapter(new PhotoAdapter(galleryItems));
    }

    @Override
    public void onGlobalLayout() {
        int spanCount = Math.round(recyclerView.getWidth() / COLUMN_WIDGHT);
        Log.i(TAG, "Span count: " + String.valueOf(spanCount));
        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(spanCount);
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String query;

        public FetchItemsTask(String query) {
            this.query = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (query == null) {
                return new FlickrFetchr().fetchRecentPhotos(getActivity());
            } else {
                return new FlickrFetchr().searchPhotos(query, getActivity());
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> list) {
            galleryItems = list;
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_iv);
        }

        public void bind(Drawable drawable) {
            imageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> galleryItemList;

        public PhotoAdapter(List<GalleryItem> list) {
            galleryItemList = list;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = galleryItemList.get(position);
            Drawable placeholder = ResourcesCompat.getDrawable(getResources(), R.drawable.bill_up_close, null);
            holder.bind(placeholder);
            thumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return galleryItemList.size();
        }
    }
}
