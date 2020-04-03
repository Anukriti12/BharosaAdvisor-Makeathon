package com.ap.bharosaadvisor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ap.bharosaadvisor.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class Learn extends Fragment
{
    Context ctx;
    FragmentManager fm;
    ViewPager tabPager;
    SmartTabLayout tabView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);
        ctx = rootView.getContext();
        fm = getFragmentManager();

        tabPager = rootView.findViewById(R.id.learn_tab_pager);
        tabView = rootView.findViewById(R.id.learn_tab_view);
        FragmentPagerItemAdapter tabAdapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(ctx)
                .add(R.string.videos, Videos.class)
                .add(R.string.documents, Documents.class)
                .create());
        tabPager.setAdapter(tabAdapter);
        tabView.setViewPager(tabPager);

        return rootView;
    }
}
