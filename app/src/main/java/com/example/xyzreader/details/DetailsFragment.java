package com.example.xyzreader.details;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.ui.BaseFragment;

import example.com.xyzreader.BR;
import example.com.xyzreader.R;
import example.com.xyzreader.databinding.DetailsViewBinding;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class DetailsFragment extends BaseFragment {

    private Item mItem;

    public void setItem(Item item) {
        mItem = item;
    }

    DetailsViewBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.details_view, container, false);
        if (mItem != null) {
            ViewCompat.setTransitionName(mBinding.thumbnail, mItem.getId());
            mBinding.setVariable(BR.item, mItem);
        }

        mBinding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(),android.R.color.transparent));
        mBinding.fab.setOnClickListener(v -> {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setText(mItem.getTitle()+" by "+mItem.getAuthor())
                    .getIntent(), getString(R.string.action_share)));
        });
        return mBinding.getRoot();
    }

    @BindingAdapter("bind:image")
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @BindingAdapter("bind:by_line")
    public static void loadText(TextView textView, Item item) {
        Time time = new Time();
        time.parse3339(item.getPublishedDate());
        textView.setText(Html.fromHtml(
                DateUtils.getRelativeTimeSpanString(
                        time.toMillis(false),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString()
                        + " by <font color='#ffffff'>"
                        + item.getAuthor()
                        + "</font>" ));
    }
    @BindingAdapter("bind:body")
    public static void loadBody(TextView textView, String body) {
        textView.setText(Html.fromHtml(body));
    }
}
