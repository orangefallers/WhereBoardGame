package com.ofcat.whereboardgame.joinplay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ofcat.whereboardgame.R;

/**
 * Created by orangefaller on 2017/6/21.
 */

public class PlayerRoomListActivity extends AppCompatActivity {
    private final String TAG = PlayerRoomListActivity.class.getSimpleName();

    private TabLayout tabLayoutRoomList;
    private ViewPager viewPagerRoomList;

    private PlayerRoomViewPagerAdapter roomViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_room_list);
        initActionBar();
        initView();
        initAdapter();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initView() {

        tabLayoutRoomList = (TabLayout) findViewById(R.id.player_room_tab_layout);
        viewPagerRoomList = (ViewPager) findViewById(R.id.player_room_view_pager);
        viewPagerRoomList.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutRoomList));
        tabLayoutRoomList.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPagerRoomList));

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.player_room_list_title);
        } catch (NullPointerException e) {

        }
    }

    private void initAdapter() {
        if (roomViewPagerAdapter == null) {
            roomViewPagerAdapter = new PlayerRoomViewPagerAdapter(getSupportFragmentManager());
        }
        if (viewPagerRoomList != null){
            viewPagerRoomList.setAdapter(roomViewPagerAdapter);
        }

    }

}
