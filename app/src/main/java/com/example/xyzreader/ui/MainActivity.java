package com.example.xyzreader.ui;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.xyzreader.ImageLoaderHelper;
import com.example.xyzreader.model.Item;
import com.example.xyzreader.retrofit.ItemService;
import com.minimize.android.rxrecycleradapter.BR;
import com.minimize.android.rxrecycleradapter.RxAdapter;

import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.ActivityMainBinding;
import example.com.xyzreader.databinding.ListItemArticleBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectActivity(this);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mActivityMainBinding.toolBar);

        mActivityMainBinding.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show());

        ItemService itemService = mRetrofit.create(ItemService.class);
        Observable<List<Item>> itemsObservable = itemService.getItems();
        itemsObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    //Create RxAdapter
                    final RxAdapter<Item, ListItemArticleBinding> rxAdapter = new RxAdapter<>(R.layout.list_item_article, items);
                    rxAdapter.asObservable()
                            .subscribe(simpleViewItem -> {
                                final ListItemArticleBinding binding = simpleViewItem.getViewDataBinding();
                                binding.setVariable(BR.item, simpleViewItem.getItem());
                                binding.executePendingBindings();
                            });

                    mActivityMainBinding.recyclerView.setAdapter(rxAdapter);
                    int columnCount = getResources().getInteger(R.integer.list_column_count);
                    StaggeredGridLayoutManager sglm =
                            new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                    mActivityMainBinding.recyclerView.setLayoutManager(sglm);
                });
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

    @BindingAdapter({"bind:image", "bind:aspect_ratio"})
    public static void loadImage(DynamicHeightNetworkImageView imageView, String url, float aspectRatio) {
        imageView.setImageUrl(url, ImageLoaderHelper.getInstance(imageView.getContext())
                .getImageLoader());
        if (aspectRatio != 0)
            imageView.setAspectRatio(aspectRatio);
        else
            imageView.setAspectRatio(1.5f);
        Log.e(TAG, "loadImage() called with: " + "imageView = [" + imageView + "], url = [" + url + "], aspectRatio = [" + aspectRatio + "]");
    }

}
