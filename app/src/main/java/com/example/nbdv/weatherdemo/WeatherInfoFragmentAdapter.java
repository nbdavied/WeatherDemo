package com.example.nbdv.weatherdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.List;

/**
 * Created by nbdav on 2016/3/12.
 */
public class WeatherInfoFragmentAdapter extends FragmentStatePagerAdapter {
    private List<WeatherInfoFragment> fragList;

    public WeatherInfoFragmentAdapter(FragmentManager fm,List<WeatherInfoFragment> fragList) {
        super(fm);
        this.fragList=fragList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
