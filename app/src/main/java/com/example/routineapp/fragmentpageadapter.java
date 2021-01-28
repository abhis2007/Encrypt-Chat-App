package com.example.routineapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class fragmentpageadapter extends FragmentPagerAdapter {
    private Context mContext;
    private static int pageCount=7;
    private String[] tabName=new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    public fragmentpageadapter(Context context, FragmentManager fm){
        super(fm);
        mContext=context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentpage.newInstance(position+1);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabName[position];
    }
}
