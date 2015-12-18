package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;

import java.util.List;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.FragmentViewpagerBinding;
import timber.log.Timber;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class ViewPagerFragment extends BaseFragment {
    FragmentViewpagerBinding mBinding;
    private Item mItem;

    public void setItem(Item item) {
        mItem = item;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewpager, container, false);
        MyPagerAdapter adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        mBinding.viewPager.setAdapter(adapter);
        if (mItem != null) {
            //set to current item's page
            int indexOfItem = -1;
            List<Item> allItems = ItemHelper.getAllItems();
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i).getId()
                        .equals(mItem.getId()))
                    indexOfItem = i;
            }
            Timber.e("onCreateView : item Position " + indexOfItem);
            mBinding.viewPager.setCurrentItem(indexOfItem);
        }

        return mBinding.getRoot();
    }

}
