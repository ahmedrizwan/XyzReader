package com.example.xyzreader.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.com.xyzreader.R;
import example.com.xyzreader.databinding.FragmentViewpagerBinding;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class ViewPagerFragment extends BaseFragment {
    FragmentViewpagerBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_viewpager, container, false);
        mBinding.viewPager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        return mBinding.getRoot();
    }

}
