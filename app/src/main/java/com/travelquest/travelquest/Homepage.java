package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.travelquest.travelquest.login.Login;

public class Homepage extends BaseNavActivity {

    Button travel_alone;
    Button travel_with_people;
    Button meet_local;
    Button meet_foreign;
    Button start_button;
    ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set up navigation bar
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer, mDrawer);
        View headerView = nvDrawer.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_title);
        navUsername.setText(pref.getString("first_name", null));

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        // Setting buttons properties
        travel_alone = (Button) findViewById(R.id.travel_alone);
        travel_alone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        travel_with_people = (Button) findViewById(R.id.travel_with_people);
        travel_with_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        meet_local = (Button) findViewById(R.id.meet_local);
        meet_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        meet_foreign = (Button) findViewById(R.id.meet_foreign_people);
        meet_foreign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });

        loading = (ProgressBar) findViewById(R.id.loading);
        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //loading.setVisibility(View.VISIBLE);

                Intent intent = new Intent(Homepage.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }


}

