package com.ofcat.whereboardgame.findperson;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ofcat.whereboardgame.R;
import com.ofcat.whereboardgame.firebase.model.FireBaseUrl;
import com.ofcat.whereboardgame.util.FirebaseTableKey;
import com.ofcat.whereboardgame.util.MyLog;
import com.ofcat.whereboardgame.util.SharedPreferenceKey;

import java.util.ArrayList;

/**
 * Created by orangefaller on 2017/9/12.
 */

public class StoreListActivity extends AppCompatActivity {

    private final String TAG = StoreListActivity.class.getSimpleName();

    private FirebaseDatabase database;
    private DatabaseReference storeTabGenre;
    private SharedPreferences sp;

    private TabLayout tabLayoutStoreLocation;
    private ViewPager viewPagerStoreList;

    private StoreListViewPagerAdapter adapter;

    private ArrayList<String> genreList = new ArrayList<>();
    private int listCount;
    private int storeTabPosition;


    private ValueEventListener genreValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (genreList != null) {
                genreList.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    MyLog.i(TAG, "genre = " + child.getValue(String.class));
                    genreList.add(child.getValue(String.class));
                }

                listCount = genreList.size();
                showGenreTabLayout(genreList, storeTabPosition);
                showStoreViewPager(listCount);

            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        initActionBar();
        initView();
        sp = getSharedPreferences("BGS_DATA", MODE_PRIVATE);
        storeTabPosition = sp.getInt(SharedPreferenceKey.KEY_SP_STORE_TAB_POSITION, 0);
        database = FirebaseDatabase.getInstance();

        FireBaseUrl tabUrl = new FireBaseUrl.Builder()
                .addUrlNote(FirebaseTableKey.TABLE_STORE_TAB_GENRE)
                .build();
        storeTabGenre = database.getReferenceFromUrl(tabUrl.getUrl());
        storeTabGenre.addListenerForSingleValueEvent(genreValueEventListener);

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.store_findperson_title);
        } catch (NullPointerException e) {

        }
    }

    private void initView() {
        tabLayoutStoreLocation = (TabLayout) findViewById(R.id.board_game_store_tab_layout);
        viewPagerStoreList = (ViewPager) findViewById(R.id.board_game_store_view_pager);

        tabLayoutStoreLocation.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPagerStoreList));
        tabLayoutStoreLocation.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                MyLog.i(TAG, "select pos = " + position);
                saveTabPosition(position);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPagerStoreList.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutStoreLocation));
    }


    private void showGenreTabLayout(ArrayList<String> genres, final int selectPosition) {
        MyLog.i(TAG, "init position = " + selectPosition);
//        int i = 0;

        if (genres != null && !genres.isEmpty()) {
            for (String genre : genres) {

                TabLayout.Tab tab = tabLayoutStoreLocation.newTab().setText(genre);
//                if (selectPosition > 0 && i == selectPosition) {
//                    tab.select();
//                }
                tabLayoutStoreLocation.addTab(tab);
//                i++;
            }
        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (selectPosition > 0) {
                            tabLayoutStoreLocation.getTabAt(selectPosition).select();
                        }
                    }
                }, 100);

    }

    private void showStoreViewPager(int listCount) {
        if (adapter == null) {
            adapter = new StoreListViewPagerAdapter(getSupportFragmentManager(), listCount);
        }

        viewPagerStoreList.setAdapter(adapter);
    }

    private void saveTabPosition(int position) {
        sp.edit()
                .putInt(SharedPreferenceKey.KEY_SP_STORE_TAB_POSITION, position)
                .apply();
    }
}
