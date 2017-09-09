package com.ofcat.whereboardgame.joinplay;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by orangefaller on 2017/9/9.
 */

public class PlayerRoomViewPagerAdapter extends FragmentStatePagerAdapter {

    public PlayerRoomViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PlayerStoreRoomFragment.newInstance();
            case 1:
                return PlayerCustomRoomFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
