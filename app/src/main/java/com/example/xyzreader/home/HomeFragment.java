package com.example.xyzreader.home;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.XyzApp;
import com.example.xyzreader.details.DetailsViewPagerFragment;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;
import com.example.xyzreader.retrofit.ItemService;
import com.example.xyzreader.ui.BaseActivity;
import com.example.xyzreader.ui.BaseFragment;
import com.minimize.android.rxrecycleradapter.BR;
import com.minimize.android.rxrecycleradapter.RxAdapter;

import java.util.Collections;
import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.HomeListBinding;
import example.com.xyzreader.databinding.HomeListItemBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 20/12/2015.
 */
public class HomeFragment extends BaseFragment {

    HomeListBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.home_list, container, false);
        injectFragment(this);
        ((BaseActivity) getActivity()).setSupportActionBar(mBinding.toolBar);
        ((BaseActivity) getActivity()).getSupportActionBar()
                .setTitle("");
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(sglm);
        List<Item> allItems = ItemHelper.getAllItems();
        final RxAdapter<Item, HomeListItemBinding> rxAdapter = new RxAdapter<>(R.layout.home_list_item, Collections.emptyList());
        if (allItems != null && allItems.size() > 0) {
            rxAdapter.updateDataSet(allItems);
        }
        rxAdapter.asObservable()
                .subscribe(simpleViewItem -> {
                    final HomeListItemBinding binding = simpleViewItem.getViewDataBinding();
                    final Item item = simpleViewItem.getItem();
                    binding.setVariable(BR.item, item);
                    binding.executePendingBindings();
                    binding.getRoot()
                            .setOnClickListener(v -> itemClicked(item, binding));
                    ViewCompat.setTransitionName(binding.thumbnail, item.getId());
                }, throwable -> {
                    Timber.e("onCreate : " + throwable.getMessage());
                });
        mBinding.recyclerView.setAdapter(rxAdapter);

        ItemService itemService = mRetrofit.create(ItemService.class);
        Observable<List<Item>> itemsObservable = itemService.getItems();
        itemsObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    //Save the items
                    mRealm.beginTransaction();
                    ItemHelper.saveItems(mRealm, items);
                    mRealm.commitTransaction();
                    //Create RxAdapter for displaying the items
                    rxAdapter.updateDataSet(items);
                }, throwable1 -> {
                    Timber.e("onCreate : retro " + throwable1.getMessage());
                });
        return mBinding.getRoot();
    }

    private void itemClicked(final Item item, final HomeListItemBinding binding) {
        Timber.e("itemClicked : " + "item = [" + item + "]");
        //launch Detail Activity from here, passing the item
        ViewCompat.setTransitionName(binding.thumbnail, item.getId());
        DetailsViewPagerFragment toFragment = new DetailsViewPagerFragment();
        toFragment.setItem(item);
        XyzApp.launchFragmentWithSharedElements(false, this, toFragment, R.id.container, binding.thumbnail);
    }

    @BindingAdapter({"bind:image", "bind:aspect_ratio"})
    public static void loadImage(DynamicHeightNetworkImageView imageView, String url, float aspectRatio) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);

        if (aspectRatio != 0)
            imageView.setAspectRatio(aspectRatio);
        else
            imageView.setAspectRatio(1.5f);
    }

    @BindingAdapter("bind:subtitle")
    public static void subtitle(TextView textView, Item item) {
        try {
            Time time = new Time();
            time.parse3339(item.getPublishedDate());
            textView.setText(DateUtils.getRelativeTimeSpanString(
                    time.toMillis(false),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL                 )
                    .toString()
                    + " by "
                    + item.getAuthor());
        } catch (Exception e) {
            Timber.e("subtitle : "+e.getMessage());

        }
    }


}
