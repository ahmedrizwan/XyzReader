package com.example.xyzreader.ui;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.model.Item;

import example.com.xyzreader.BR;
import example.com.xyzreader.R;
import example.com.xyzreader.databinding.FragmentDetailsBinding;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class DetailsFragment extends BaseFragment {

    private Item mItem;

    public void setItem(Item item) {
        mItem = item;
    }

    FragmentDetailsBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false);

        mBinding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent, null));
        if (mItem != null) {
            Timber.e("onCreate : %s", mItem.getTitle());
            mBinding.setVariable(BR.item, mItem);
        }
        return mBinding.getRoot();
    }

    @BindingAdapter("bind:image")
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}
