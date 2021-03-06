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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.travelquest.travelquest.R;
import com.travelquest.travelquest.UserPreference;
import com.travelquest.travelquest.UserProfile;
import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginForm extends AppCompatActivity{

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    EditText mail, first_name, last_name, password, confirm_password;
    RadioGroup gender_group;
    RadioButton gender_button;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        Intent intent = getIntent();

        mail = (EditText) findViewById(R.id.form_mail);
        first_name = (EditText) findViewById(R.id.form_first_name);
        last_name = (EditText) findViewById(R.id.form_last_name);
        gender_group = (RadioGroup) findViewById(R.id.form_gender);
        password = (EditText) findViewById(R.id.form_password);
        password.setText(intent.getStringExtra("password"));
        confirm_password = (EditText) findViewById(R.id.form_confirm_password);


        mail.setText(pref.getString("mail", null));
        next = (Button) findViewById(R.id.login_form_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().compareTo(confirm_password.getText().toString()) != 0){
                    Toast.makeText(getApplicationContext(),"Passwords do not match", Toast.LENGTH_LONG).show();
                    return;
                }
                if(first_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please specify your first name", Toast.LENGTH_LONG).show();
                    return;
                }

                createUser();
            }
        });
    }


    private void createUser(){
        // Get gender
        int gender_id = gender_group.getCheckedRadioButtonId();
        gender_button = (RadioButton) findViewById(gender_id);
        String sex;
        if(gender_button == null)
            sex = "Unknown";
        else
            sex = gender_button.getText().toString();

        final HashMap<String, String> params = new HashMap<>();
        params.put("first_name", first_name.getText().toString());
        params.put("last_name", last_name.getText().toString());
        params.put("gender", sex);
        params.put("mail", mail.getText().toString());
        params.put("password", password.getText().toString());
        params.put("account_type", "basic");


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
            try {
                JSONObject temp = new JSONObject(s);
                boolean result = temp.getBoolean("error");
                if(result) {
                    Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                    Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_LONG).show();
            }catch (JSONException e){
                e.printStackTrace();
            }
            //////// Create user session ////////
            editor.putString("first_name", first_name.getText().toString());
            editor.commit(); // commit changes
            /////// Launch next activity //////
            Intent intent = new Intent(LoginForm.this, UserPreference.class);
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
