package com.travelquest.travelquest.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;


import com.travelquest.travelquest.R;
import com.travelquest.travelquest.UserPreference;
import com.travelquest.travelquest.UserProfile;
import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;

import org.json.JSONException;

import java.util.HashMap;


public class LoginForm extends AppCompatActivity{

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    EditText mail, first_name, last_name;
    RadioGroup gender;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        mail = (EditText) findViewById(R.id.form_mail);
        first_name = (EditText) findViewById(R.id.form_first_name);
        last_name = (EditText) findViewById(R.id.form_last_name);
        gender = (RadioGroup) findViewById(R.id.form_gender);


        mail.setText(pref.getString("mail", null));
        next = (Button) findViewById(R.id.login_form_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }


    private void createUser(){
        final HashMap<String, String> params = new HashMap<>();

        params.put("first_name", first_name.getText().toString());
        params.put("last_name", last_name.getText().toString());
        params.put("gender", "Unknown");
        params.put("mail", mail.getText().toString());
        params.put("password", "");
        params.put("accountType", "basic");


        //Calling the create hero API
        PerformNetworkRequest request = new PerformNetworkRequest(API.URL_CREATE_USER, params);
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
            //////// Create user session ////////
            editor.putString("first_name", first_name.getText().toString());
            editor.commit(); // commit changes
            /////// Launch next activity //////
            Intent intent = new Intent(LoginForm.this, LoginTransition.class);
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
