package com.ofcat.whereboardgame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import com.ofcat.whereboardgame.report.ReportFragment;

/**
 * Created by orangefaller on 2017/3/5.
 */

public class WhereBoardGameMainActivity extends AppCompatActivity {

    private static final String TAG = WhereBoardGameMainActivity.class.getSimpleName();

    private static final int TAB_HOME = 0;
    private static final int TAB_MAP = 1;
    private static final int TAB_FIND_SOMEONE = 2;
    private static final int TAB_REPORT = 3;

    private TabLayout tabLayout_main_welcome;


    private TabLayout.OnTabSelectedListener selectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            switch (tab.getPosition()) {
                case TAB_HOME:
                    break;
                case TAB_MAP:
                    break;
                case TAB_FIND_SOMEONE:
                    break;
                case TAB_REPORT:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, ReportFragment.newInstance())
                            .commit();
                    break;
            }

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_welcome);
        initView();

    }

    private void initView() {
        tabLayout_main_welcome = (TabLayout) findViewById(R.id.tl_main_welcome);
        tabLayout_main_welcome.addOnTabSelectedListener(selectedListener);

    }
}
