package com.example.xyzreader.ui;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.xyzreader.XyzApp;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;
import com.example.xyzreader.retrofit.ItemService;
import com.minimize.android.rxrecycleradapter.BR;
import com.minimize.android.rxrecycleradapter.RxAdapter;

import java.util.Collections;
import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.FragmentListBinding;
import example.com.xyzreader.databinding.ListItemArticleBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class ListFragment extends BaseFragment {
    FragmentListBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        injectFragment(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        ((BaseActivity) getActivity()).setSupportActionBar(mBinding.toolBar);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(sglm);

        List<Item> allItems = ItemHelper.getAllItems();
        final RxAdapter<Item, ListItemArticleBinding> rxAdapter = new RxAdapter<>(R.layout.list_item_article, Collections.emptyList());
        if(allItems!=null && allItems.size()>0){
            rxAdapter.updateDataSet(allItems);
        }
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
                    rxAdapter.asObservable()
                            .subscribe(simpleViewItem -> {
                                final ListItemArticleBinding binding = simpleViewItem.getViewDataBinding();
                                final Item item = simpleViewItem.getItem();
                                binding.setVariable(BR.item, item);
                                binding.executePendingBindings();
                                binding.getRoot()
                                        .setOnClickListener(v -> itemClicked(item, binding));
                            });

                    mBinding.recyclerView.setAdapter(rxAdapter);

                });
        return mBinding.getRoot();
    }

    private void itemClicked(final Item item, final ListItemArticleBinding binding) {
        Timber.e("itemClicked : " + "item = [" + item + "]");
        //launch Detail Activity from here, passing the item
        XyzApp.launchFragment(((AppCompatActivity) getActivity()),R.id.container, new ViewPagerFragment());
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

}
