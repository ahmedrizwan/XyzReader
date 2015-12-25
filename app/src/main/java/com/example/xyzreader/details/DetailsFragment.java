package com.example.xyzreader.details;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.ui.BaseFragment;

import org.parceler.Parcels;

import java.util.concurrent.ExecutionException;

import example.com.xyzreader.BR;
import example.com.xyzreader.R;
import example.com.xyzreader.databinding.DetailsBodyLayoutBinding;
import example.com.xyzreader.databinding.DetailsViewBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class DetailsFragment extends BaseFragment {

    private Item mItem;

    public void setItem(Item item) {
        mItem = item;
    }

    DetailsViewBinding mBinding;
    DetailsBodyLayoutBinding mBodyLayoutBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.details_view, container, false);

        mBodyLayoutBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.details_body));

        if(savedInstanceState!=null){
            mItem = Parcels.unwrap(savedInstanceState.getParcelable(getString(R.string.parcelable_item)));
        }

        if (mItem != null) {
            mBinding.setVariable(BR.item, mItem);
            mBodyLayoutBinding.setVariable(BR.item, mItem);
        }

        mBinding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));

        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mBinding.fab.setOnClickListener(v -> {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setText(mItem.getTitle() + " by " + mItem.getAuthor())
                    .getIntent(), getString(R.string.action_share)));
        });

        return mBinding.getRoot();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.parcelable_item), Parcels.wrap(mItem));
    }

    @BindingAdapter("bind:collapse_image")
    public static void loadThumbnailHeader(ImageView imageView, String url) {
        Observable.create((Observable.OnSubscribe<GlideDrawable>) subscriber -> {
            try {
                subscriber.onNext(Glide.with(imageView.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(1, 1)
                        .get());
            } catch (InterruptedException | ExecutionException e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageView::setImageDrawable);


        Timber.e("loadThumbnailHeader : YO");
    }

    @BindingAdapter("bind:by_line")
    public static void loadText(TextView textView, Item item) {
        Time time = new Time();
        time.parse3339(item.getPublishedDate());
        textView.setText               (Html.fromHtml(
                DateUtils.getRelativeTimeSpanString(
                        time.toMillis(false),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL)
                        .toString()
                        + " by <font color='#ffffff'>"
                        + item.getAuthor()
                        + "</font>"   ));
    }

    @BindingAdapter("bind:body")
    public static void loadBody(TextView textView, String body) {
        textView.setText(Html.fromHtml(body));
    }


}
