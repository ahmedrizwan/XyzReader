package com.example.xyzreader.ui;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;
import com.example.xyzreader.retrofit.ItemService;
import com.minimize.android.rxrecycleradapter.BR;
import com.minimize.android.rxrecycleradapter.RxAdapter;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.ActivityMainBinding;
import example.com.xyzreader.databinding.ListItemArticleBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectActivity(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
         setSupportActionBar(mBinding.toolBar);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(sglm);
        List<Item> allItems = ItemHelper.getAllItems();
        final RxAdapter<Item, ListItemArticleBinding> rxAdapter = new RxAdapter<>(R.layout.list_item_article, Collections.emptyList());
        if (allItems != null && allItems.size() > 0) {
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
                                ViewCompat.setTransitionName(binding.thumbnail, item.getId());
                            });

                    mBinding.recyclerView.setAdapter(rxAdapter);
                });
    }

    private void itemClicked(final Item item, final ListItemArticleBinding binding) {
        Timber.e("itemClicked : " + "item = [" + item + "]");
        //launch Detail Activity from here, passing the item
        Intent intent = new Intent(this, ViewPagerActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        // Pass data object in the bundle and populate details activity.
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, binding.thumbnail, "profile");
        startActivity(intent, options.toBundle());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
