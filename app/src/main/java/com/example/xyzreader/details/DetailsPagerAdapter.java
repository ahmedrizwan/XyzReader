package com.example.xyzreader.details;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.xyzreader.model.Item;
import com.example.xyzreader.model.ItemHelper;

public class DetailsPagerAdapter extends FragmentStatePagerAdapter {

    public DetailsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Item itemAtPosition = ItemHelper.getAllItems()
                .get(position);
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setItem(itemAtPosition);

        return detailsFragment;
    }

    @Override
    public int getCount() {
        return ItemHelper.getItemCount();
    }
}