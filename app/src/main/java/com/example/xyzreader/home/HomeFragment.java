package com.example.xyzreader.home;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.xyzreader.XyzApp;
import com.example.xyzreader.details.DetailsViewPagerFragment;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;
import com.example.xyzreader.retrofit.ItemService;
import com.example.xyzreader.ui.BaseActivity;
import com.example.xyzreader.ui.BaseFragment;
import com.example.xyzreader.ui.GridSpacingItemDecoration;
import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.minimize.android.rxrecycleradapter.RxAdapter;

import java.util.Collections;
import java.util.List;

import example.com.xyzreader.BR;
import example.com.xyzreader.R;
import example.com.xyzreader.databinding.HomeListBinding;
import example.com.xyzreader.databinding.HomeListItemBinding;
import example.com.xyzreader.databinding.HomeRecyclerviewBinding;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 20/12/2015.
 */
public class HomeFragment extends BaseFragment {

    HomeListBinding mBinding;
    HomeRecyclerviewBinding mRecyclerviewBinding;
    Subscription mNetwork;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.home_list, container, false);
        mRecyclerviewBinding = DataBindingUtil.bind(mBinding.getRoot()
                .findViewById(R.id.recycler_view_layout));


        injectFragment(this);
        ((BaseActivity) getActivity()).setSupportActionBar(mBinding.toolbar);
        ActionBar supportActionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar
                    .setTitle("");
            supportActionBar.setDisplayHomeAsUpEnabled(false);
        }

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerviewBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 6, true));
        mRecyclerviewBinding.recyclerView.setLayoutManager(sglm);
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

        mRecyclerviewBinding.recyclerView.setAdapter(rxAdapter);

        loadArticles(allItems, rxAdapter);


         mNetwork = new ReactiveNetwork().observeConnectivity(getActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivityStatus -> {
                    if (connectivityStatus.equals(ConnectivityStatus.WIFI_CONNECTED) || connectivityStatus.equals(ConnectivityStatus.MOBILE_CONNECTED)) {
                        loadArticles(allItems, rxAdapter);
                    }
                });

        return mBinding.getRoot();
    }

    private void loadArticles(final List<Item> allItems, final RxAdapter<Item, HomeListItemBinding> rxAdapter) {
        if (allItems == null || allItems.size() == 0) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.textViewError.setVisibility(View.GONE);
        }
        //get data
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
                    mBinding.progressBar.setVisibility(View.GONE);
                }, throwable1 -> {
                    //check if there's no data persisted
                    if (allItems == null || allItems.size() == 0) {
                        //show error message
                        mBinding.textViewError.setVisibility(View.VISIBLE);
                        mBinding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void itemClicked(final Item item, final HomeListItemBinding binding) {
        Timber.e("itemClicked : " + "item = [" + item + "]");
        //launch Detail Activity from here, passing the item

        DetailsViewPagerFragment toFragment = new DetailsViewPagerFragment();
        toFragment.setItem(item);
        XyzApp.launchFragmentWithSharedElements(false, this, toFragment, R.id.container, null);
    }

    @BindingAdapter({"bind:image"})
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
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
            Timber.e("subtitle : " + e.getMessage());

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mNetwork.unsubscribe();
    }
}
