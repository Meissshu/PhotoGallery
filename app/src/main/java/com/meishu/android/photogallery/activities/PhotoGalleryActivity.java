package com.meishu.android.photogallery.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    public Fragment getFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
