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
import android.widget.TextView;
import android.widget.Toast;

import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;
import com.travelquest.travelquest.login.Login;
import com.travelquest.travelquest.login.LoginTransition;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserAccount extends BaseNavActivity {
    EditText first_name, password, confirm_password;

    Button preference_save, information_save;
    Button food, drink, sight, music, party;

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
        View headerView = nvDrawer.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_title);
        navUsername.setText(pref.getString("first_name", null));

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

        food = (Button) findViewById(R.id.account_food);
        drink = (Button) findViewById(R.id.account_drink);
        sight = (Button) findViewById(R.id.account_sight);
        music = (Button) findViewById(R.id.account_music);
        party = (Button) findViewById(R.id.account_party);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        sight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
        party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected())
                    v.setSelected(false);
                else
                    v.setSelected(true);
            }
        });
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
        params.put("food",  String.valueOf(food.isSelected()));
        params.put("drink",  String.valueOf(drink.isSelected()));
        params.put("sight",  String.valueOf(sight.isSelected()));
        params.put("music",  String.valueOf(music.isSelected()));
        params.put("party",  String.valueOf(party.isSelected()));

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
        if(!first_name.getText().toString().isEmpty())
            params.put("first_name",  first_name.getText().toString());
        if(!password.getText().toString().isEmpty())
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
            Log.d("debug_update", s);
            try {
                JSONObject temp = new JSONObject(s);
                boolean result = temp.getBoolean("error");
                if(result)
                    Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_LONG).show();
            }catch (JSONException e){
                e.printStackTrace();
            }

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
