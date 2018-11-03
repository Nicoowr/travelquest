package com.travelquest.travelquest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.travelquest.travelquest.database_handler.PoI;

public class PoIInfo extends BaseNavActivity {
    SharedPreferences pref;
    PoI poi;
    TextView poiTitle;
    TextView poiCountry;
    TextView poiDescription;
    ImageView picture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        // Set the navigation drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set up navigation bar
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer, mDrawer);
        View headerView = nvDrawer.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_title);
        navUsername.setText(pref.getString("first_name", null));

        // Get PoI
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            poi = (PoI) getIntent().getSerializableExtra("poi");
        }

        poiTitle = (TextView) findViewById(R.id.poiInfoTitle);
        poiTitle.setText(poi.getTitle());
        poiTitle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));

        poiCountry = (TextView) findViewById(R.id.poiInfoCountry);
        poiCountry.setText(poi.getCountry());
        poiCountry.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));

        poiDescription = (TextView) findViewById(R.id.poiInfoDescription);
        poiDescription.setText(poi.getDescription());
        poiDescription.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));


        picture = (ImageView) findViewById(R.id.poiInfoPicture);
        Log.d("debug_reward", poi.getImageLink());
        Picasso.with(PoIInfo.this).load(poi.getImageLink()).into(picture);
        picture.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
    }
}
