package com.ofcat.whereboardgame.findperson;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by orangefaller on 2017/9/13.
 */

public class StoreListViewPagerAdapter extends FragmentStatePagerAdapter {

    private int adapterCount;
    private int apiIndex;

    public StoreListViewPagerAdapter(FragmentManager fm, int adapterCount) {
        super(fm);
        this.adapterCount = adapterCount;
    }

    @Override
    public Fragment getItem(int position) {
        //扣掉第一個資料表和起始表格為1而不是0，所以要加2
        apiIndex = position + 2;
        return StoreListFragment.newInstance(apiIndex);
    }

    @Override
    public int getCount() {
        return adapterCount;
    }
}
