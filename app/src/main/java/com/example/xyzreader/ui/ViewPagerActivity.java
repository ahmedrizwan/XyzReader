package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;

import org.parceler.Parcels;

import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.FragmentViewpagerBinding;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 19/12/2015.
 */
public class ViewPagerActivity extends BaseActivity {
    FragmentViewpagerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_viewpager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(3);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Parcelable itemParcelable = extras.getParcelable("item");
            Item item = Parcels.unwrap(itemParcelable);
            if (item != null) {
                //set to current item's page
                int indexOfItem = -1;
                List<Item> allItems = ItemHelper.getAllItems();
                for (int i = 0; i < allItems.size(); i++) {
                    if (allItems.get(i).getId()
                            .equals(item.getId()))
                        indexOfItem = i;
                }
                Timber.e("onCreate: item Position " + indexOfItem);
                mBinding.viewPager.setCurrentItem(indexOfItem);
            }
        }

    }
}
