package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;
import com.travelquest.travelquest.login.Login;
import com.travelquest.travelquest.login.LoginTransition;

import java.util.HashMap;

public class UserAccount extends BaseNavActivity {
    EditText first_name, password, confirm_password;

    Button preference_save, information_save;
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

        first_name = (EditText) findViewById(R.id.update_first_name);
        password = (EditText) findViewById(R.id.update_password);
        confirm_password = (EditText) findViewById(R.id.update_confirm_password);
        information_save = (Button) findViewById(R.id.update_information_save);
        information_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateUserInformation();
                //Intent intent = new Intent(UserPreference.this, LoginTransition.class);
                //startActivity(intent);
            }
        });

        food = (CheckBox) findViewById(R.id.account_food);
        drink = (CheckBox) findViewById(R.id.account_drink);
        sight = (CheckBox) findViewById(R.id.account_sight);
        music = (CheckBox) findViewById(R.id.account_music);
        party = (CheckBox) findViewById(R.id.account_party);
        preference_save = (Button) findViewById(R.id.update_preferences_save);
        preference_save.setOnClickListener(new View.OnClickListener() {
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

    protected void updateUserInformation(){
        if(password.getText().toString().compareTo(confirm_password.getText().toString()) != 0){
            Toast.makeText(getApplicationContext(),"Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_mail",  pref.getString("mail", null));
        params.put("first_name",  String.valueOf(food.isChecked()));
        params.put("password",  password.getText().toString());

        PerformNetworkRequest request = new PerformNetworkRequest(API.URL_UPDATE_USER_INFORMATION, params);

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

    protected boolean checkPassword(){
        return password.getText().toString().compareTo(confirm_password.getText().toString()) == 0;
    };
}
