package com.ofcat.whereboardgame.findperson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ofcat.whereboardgame.R;

/**
 * Created by orangefaller on 2017/4/16.
 */

public class FindPersonActivity extends AppCompatActivity {

    private RecyclerView rvFindPerson;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findperson);
        initActionBar();
        initView();
    }

    private void initView() {
        rvFindPerson = (RecyclerView) findViewById(R.id.rv_find_person_list);
        rvFindPerson.setLayoutManager(new LinearLayoutManager(this));
        rvFindPerson.setAdapter(new FindPersonAdapter(this));

    }

    private void initActionBar() {
        try {
            getSupportActionBar().setTitle(R.string.map_choose_button_find_person);
        } catch (NullPointerException e) {

        }
    }
}


