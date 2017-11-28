package com.meishu.android.photogallery.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meishu.android.photogallery.R;
import com.meishu.android.photogallery.fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    public Fragment getFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
