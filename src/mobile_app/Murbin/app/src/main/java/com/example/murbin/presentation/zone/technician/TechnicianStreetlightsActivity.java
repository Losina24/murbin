/*
 * Created by Francisco Javier Paños Madrona on 9/12/20 19:03
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 9/12/20 17:41
 */

package com.example.murbin.presentation.zone.technician;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.murbin.App;
import com.example.murbin.R;
import com.example.murbin.firebase.Auth;
import com.example.murbin.presentation.zone.technician.fragments.StreetlightsListFragment;
import com.example.murbin.presentation.zone.technician.fragments.SubzonesListFragment;

public class TechnicianStreetlightsActivity extends AppCompatActivity {

    private final Auth mAuth = new Auth(this);

    private Toolbar mToolbar;
    private ViewGroup mContainer;
    private String mMessage, mIdSubzone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(App.DEFAULT_TAG, "TechnicianStreetlightsActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technician_streetlight_list);
        initializeLayoutElements();
        if (savedInstanceState == null) {
            StreetlightsListFragment streetlightsListFragment = new StreetlightsListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("idSubzone", mIdSubzone);
            streetlightsListFragment.setArguments(bundle);
            streetlightsListFragment.settings(mIdSubzone);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.technician_streetlight_fragment, streetlightsListFragment);
        }
    }

    /**
     * Method to start the layout elements
     */
    private void initializeLayoutElements() {
        // Toolbar menu
        mToolbar = findViewById(R.id.technician_subzone_activity_toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setTitle("");
        }

        mContainer = findViewById(R.id.technician_subzone_activity_container);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {
                mMessage = extras.getString("message", "");
                if (mMessage != null && !mMessage.isEmpty()) {
                    App.getInstance().snackMessage(mContainer, R.color.black, mMessage, this);
                }
            }
            if (extras.containsKey("idSubzone")) {
                mIdSubzone = extras.getString("idSubzone", "");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.administrator_subzones_list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.administrator_subzones_list_menu_create: {
                Intent intent = new Intent(this, TechnicianStreetlightCreateActivity.class);
                intent.putExtra("mIdSubzone", mIdSubzone);
                startActivity(intent);

                break;
            }
            default: {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);

    }

}