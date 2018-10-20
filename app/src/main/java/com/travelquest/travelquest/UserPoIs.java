package com.travelquest.travelquest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.travelquest.travelquest.database_handler.PoI;

import java.util.List;

public class UserPoIs extends BaseNavActivity{
    SharedPreferences pref;
    TextView pageTitle;

    private ListView poisView = null;
    private List<PoI> sensorList = null;
    //private SensorAdapter sensorAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pois);

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

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        pageTitle = (TextView) findViewById(R.id.userPoIsTitle);
        pageTitle.setText(pref.getString("first_name", null) + "'s discoveries");

        poisView = (ListView) findViewById(R.id.poisView);
    }
}
