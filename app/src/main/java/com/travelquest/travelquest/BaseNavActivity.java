package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.travelquest.travelquest.login.Login;

public class BaseNavActivity extends AppCompatActivity{


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    protected DrawerLayout mDrawer;
    protected Toolbar toolbar;
    protected NavigationView nvDrawer;

    TextView header_title;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();



    }

    protected void setupDrawerContent(NavigationView navigationView, final DrawerLayout mDrawer) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem, mDrawer);
                        return true;
                    }
                });
    }

    protected void selectDrawerItem(MenuItem menuItem, DrawerLayout mDrawer) {

        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                Intent intent_home = new Intent(getApplicationContext(), Homepage.class);
                startActivity(intent_home);
                finish();
                break;
            case R.id.nav_map:
                Intent intent_map = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent_map);
                break;
            case R.id.nav_user:
                Intent intent_account = new Intent(getApplicationContext(), UserAccount.class);
                startActivity(intent_account);
                break;
            case R.id.nav_user_pois:
                Intent intent_user_pois = new Intent(getApplicationContext(), UserPoIs.class);
                startActivity(intent_user_pois);
                break;
            case R.id.nav_logout:
                editor.clear();
                editor.commit();
                Intent intent_logout = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_logout);
                finish();
                break;

        }



        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
}
