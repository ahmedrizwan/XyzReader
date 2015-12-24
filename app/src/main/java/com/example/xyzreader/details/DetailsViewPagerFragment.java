package com.example.xyzreader.details;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;
import com.example.xyzreader.ui.BaseFragment;

import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.DetailsViewpagerBinding;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 19/12/2015.
 */
public class DetailsViewPagerFragment extends BaseFragment {
    DetailsViewpagerBinding mBinding;
    private Item mItem;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.details_viewpager, container, false);
        DetailsPagerAdapter adapter = new DetailsPagerAdapter(getActivity().getSupportFragmentManager());
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(3);

        if (mItem != null) {
            //set to current item's page
            int indexOfItem = -1;
            List<Item> allItems = ItemHelper.getAllItems();
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i)
                        .getId()
                        .equals(mItem.getId()))
                    indexOfItem = i;
            }
            Timber.e("onCreate: item Position " + indexOfItem);
            mBinding.viewPager.setCurrentItem(indexOfItem);
        }
        return mBinding.getRoot();
    }

    public void setItem(final Item item) {
        mItem = item;
    }
}
