package com.example.xyzreader.ui;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xyzreader.model.Item;

import example.com.xyzreader.BR;
import example.com.xyzreader.R;
import example.com.xyzreader.databinding.FragmentDetailsBinding;

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
        mBinding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(),android.R.color.transparent));
        if (mItem != null) {
            mBinding.setVariable(BR.item, mItem);
        }
        return mBinding.getRoot();
    }

    @BindingAdapter("bind:image")
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }
}
