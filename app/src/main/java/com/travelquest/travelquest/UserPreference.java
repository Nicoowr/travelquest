package com.travelquest.travelquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;
import com.travelquest.travelquest.login.LoginTransition;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserPreference extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Button next;
    Button food, drink, sight, music, party;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        food = (Button) findViewById(R.id.preference_food);
        drink = (Button) findViewById(R.id.preference_drink);
        sight = (Button) findViewById(R.id.preference_sight);
        music = (Button) findViewById(R.id.preference_music);
        party = (Button) findViewById(R.id.preference_party);
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

        next = (Button) findViewById(R.id.preferences_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createUserPreferences();
                //Intent intent = new Intent(UserPreference.this, LoginTransition.class);
                //startActivity(intent);
            }
        });
    }

    protected void createUserPreferences(){
        HashMap<String, String> params = new HashMap<>();
        params.put("user_mail",  pref.getString("mail", null));
        params.put("food",  String.valueOf(food.isSelected()));
        params.put("drink",  String.valueOf(drink.isSelected()));
        params.put("sight",  String.valueOf(sight.isSelected()));
        params.put("music",  String.valueOf(music.isSelected()));
        params.put("party",  String.valueOf(party.isSelected()));

        PerformNetworkRequest request = new PerformNetworkRequest(API.URL_CREATE_USER_PREFERENCES, params);

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
            try {
                JSONObject temp = new JSONObject(s);
                boolean result = temp.getBoolean("error");
                if(result) {
                    Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                    Toast.makeText(getApplicationContext(), "Preferences saved.", Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                e.printStackTrace();
            }
            /////// Launch next activity //////
            Intent intent = new Intent(UserPreference.this, LoginTransition.class);
            startActivity(intent);
        }


        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            //if (requestCode == CODE_POST_REQUEST)
            return requestHandler.sendPostRequest(url, params);


            //if (requestCode == CODE_GET_REQUEST)
            //    return requestHandler.sendGetRequest(url);

        }
    }
}
