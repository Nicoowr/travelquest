package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;
import com.travelquest.travelquest.login.Login;
import com.travelquest.travelquest.login.LoginTransition;

import java.util.HashMap;

public class UserAccount extends BaseNavActivity {
    //SharedPreferences pref;
    //SharedPreferences.Editor editor;

    //private DrawerLayout mDrawer;
    //private Toolbar toolbar;
    //private NavigationView nvDrawer;

    Button save;
    CheckBox food, drink, sight, music, party;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Set up navigation bar
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer, mDrawer);

//        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//        editor = pref.edit();

        food = (CheckBox) findViewById(R.id.account_food);
        drink = (CheckBox) findViewById(R.id.account_drink);
        sight = (CheckBox) findViewById(R.id.account_sight);
        music = (CheckBox) findViewById(R.id.account_music);
        party = (CheckBox) findViewById(R.id.account_party);
        save = (Button) findViewById(R.id.account_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateUserPreferences();
                //Intent intent = new Intent(UserPreference.this, LoginTransition.class);
                //startActivity(intent);
            }
        });
    }

    protected void updateUserPreferences(){
        HashMap<String, String> params = new HashMap<>();
        params.put("user_mail",  pref.getString("mail", null));
        params.put("food",  String.valueOf(food.isChecked()));
        params.put("drink",  String.valueOf(drink.isChecked()));
        params.put("sight",  String.valueOf(sight.isChecked()));
        params.put("music",  String.valueOf(music.isChecked()));
        params.put("party",  String.valueOf(party.isChecked()));

        PerformNetworkRequest request = new PerformNetworkRequest(API.URL_UPDATE_USER_PREFERENCES, params);

        request.execute();
    }

    /**
     * inner class to perform network request extending an AsyncTask
     */
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;


        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params) {
            this.url = url;
            this.params = params;

        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("debug_pref", s);
            /////// Launch next activity //////
            Intent intent = new Intent(UserAccount.this, Homepage.class);
            startActivity(intent);
        }


        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            return requestHandler.sendPostRequest(url, params);


        }
    }

//   private void setupDrawerContent(NavigationView navigationView) {
//       navigationView.setNavigationItemSelectedListener(
//               new NavigationView.OnNavigationItemSelectedListener() {
//                   @Override
//                   public boolean onNavigationItemSelected(MenuItem menuItem) {
//                       selectDrawerItem(menuItem);
//                       return true;
//                   }
//               });
//   }

//   public void selectDrawerItem(MenuItem menuItem) {

//       switch(menuItem.getItemId()) {
//           case R.id.nav_home:
//               Intent intent_home = new Intent(getApplicationContext(), Homepage.class);
//               startActivity(intent_home);
//               finish();
//               break;
//           case R.id.nav_map:
//               Intent intent_map = new Intent(getApplicationContext(), MapsActivity.class);
//               startActivity(intent_map);
//               break;
//           //case R.id.nav_user:
//           //    fragmentClass = ThirdFragment.class;
//           //    break;
//           case R.id.nav_logout:
//               editor.clear();
//               editor.commit();
//               Intent intent_logout = new Intent(getApplicationContext(), Login.class);
//               startActivity(intent_logout);
//               finish();
//               break;

//       }



//       // Highlight the selected item has been done by NavigationView
//       menuItem.setChecked(true);
//       // Set action bar title
//       setTitle(menuItem.getTitle());
//       // Close the navigation drawer
//       mDrawer.closeDrawers();
//   }
}
