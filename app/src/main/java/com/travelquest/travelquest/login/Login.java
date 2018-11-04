package com.travelquest.travelquest.login;


// Front End Dependencies
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.travelquest.travelquest.R;
import com.travelquest.travelquest.UserPreference;
import com.travelquest.travelquest.UserProfile;
import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


//TODO: proper logout + side menu or action bar

public class Login extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private static final int CODE_POST_CREATE = 1026;
    private static final int CODE_POST_EXISTS = 1027;

    Button register;
    CallbackManager callbackManager;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    JSONObject facebook_data;

    AutoCompleteTextView email;
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        ////// Initialize activity /////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        FrameLayout layout = (FrameLayout)findViewById(R.id.login_layout);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        layout.startAnimation(anim);

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        // Configure basic signin/register
        register = (Button) findViewById(R.id.email_sign_in_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editor.putString("first_name", email.getText().toString());
                //editor.commit();
                //Intent intent = new Intent(Login.this, UserPreference.class);
                //startActivity(intent);
                userExists(email.getText().toString(), password.getText().toString());
            }
        });

        // Configure Facebook login process
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserFacebookData(loginResult);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("facebook error", exception.toString());
            }
        });

        ////////// Handle logout button ///////
        AccessTokenTracker tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    Log.d("FB", "User Logged Out.");
                    editor.clear();
                    editor.commit();
                }
            }
        };
        tokenTracker.startTracking();

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    /**
     * Getting User details when signing up through facebook
     * @param loginResult
     */
    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        Intent intent = new Intent(Login.this,
                                UserProfile.class);
                        //Intent intent = new Intent(Login.this, MapsActivity.class);
                        intent.putExtra("userProfile", json_object.toString());
                        startActivity(intent);
                        finish();
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email, picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    /**
     * Create user function in database
     */

   private void createUser(JSONObject user_info) {
       final HashMap<String, String> params = new HashMap<>();

        try {
            params.put("first_name", user_info.get("first_name").toString());
            params.put("last_name", user_info.get("last_name").toString());
            params.put("gender", "Unknown");
            params.put("mail", user_info.get("email").toString());
            params.put("password", "");
            params.put("account_type", "facebook");
        }catch (JSONException e){
            e.printStackTrace();
        }


       //Calling the create hero API
       PerformNetworkRequest request = new PerformNetworkRequest(API.URL_CREATE_USER, params, CODE_POST_CREATE, user_info, "facebook");
       request.execute();

   }

    /**
     * These functions launch the user registration depending on if he/she exists or null if not (using facebook or basic form)
     * @param mail
     * @return String or null
     */

    //Facebook case
   private void userExists(String mail, JSONObject user_info){
       HashMap<String, String> params = new HashMap<>();
       params.put("mail", mail);
       params.put("password", "");
       params.put("login_type", "facebook");

       PerformNetworkRequest request = new PerformNetworkRequest(API.URL_USER_EXISTS, params, CODE_POST_EXISTS, user_info, "facebook");

       request.execute();

       return;
   }

   // Basic case
    private void userExists(String mail, String password){
        HashMap<String, String> params = new HashMap<>();
        params.put("mail", mail);
        params.put("password", password);
        params.put("login_type", "basic");

        PerformNetworkRequest request = new PerformNetworkRequest(API.URL_USER_EXISTS, params, CODE_POST_EXISTS, null, "basic");

        request.execute();

        return;
    }


    /**
     * This function return a JSON object with the user data obtained with facebook login
     * @param loginResult
     * @return JSONObject
     */
    private void getUserFacebookData(LoginResult loginResult){

       GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        facebook_data = object;

                        try {
                            userExists(facebook_data.get("email").toString(), facebook_data);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
        );

        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,first_name,last_name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

       return;
    }


    /**
     * inner class to perform network request extending an AsyncTask
     */
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        JSONObject user_info;

        String login_type;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode, JSONObject user_info, String login_type) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.user_info = user_info;
            this.login_type = login_type;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("debug", s);
            if( login_type.compareTo("facebook") == 0)
                facebookLog(s);
            else if(login_type.compareTo("basic") == 0)
                basicLog(s);

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

        protected void basicLog(String s){
            Log.d("debug_basiclog", s);
            try{
                JSONObject object = new JSONObject(s);
                if(object.get("user_info").toString().compareTo("[]") == 0){// The user does not exist
                    editor.putString("mail", params.get("mail"));
                    editor.commit();
                    /////// Launch next activity //////
                    Intent intent = new Intent(Login.this, LoginForm.class);
                    intent.putExtra("password", password.getText().toString());
                    startActivity(intent);
                    finish();

                }else{// The user exists

                    //////// Create user session if password matches////////
                    JSONObject temp = object.getJSONArray("user_info").getJSONObject(0);
                    if(Boolean.valueOf(temp.get("token").toString()) && temp.get("account_type").toString().compareTo("basic")==0) {

                        editor.putString("first_name", temp.get("first_name").toString());
                        editor.putString("mail", temp.get("mail").toString());
                        editor.putString("id_user", temp.get("id_user").toString());
                        editor.commit(); // commit changes

                        /////// Launch next activity //////
                        Intent intent = new Intent(Login.this, LoginTransition.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(Login.this,"Wrong password", Toast.LENGTH_SHORT).show();
                        //password_fail.setText("Wrong password");
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        //TODO: check if there is not a security failure (log with mail only on facebook?)
        protected void facebookLog(String s){
            if (requestCode == CODE_POST_EXISTS){
                try{
                    Log.d("debug", s);
                    JSONObject object = new JSONObject(s);
                    if(object.get("user_info").toString().compareTo("[]") == 0){// The user doesn't exist
                        createUser(user_info);
                    }else{// The user exists

                        //////// Create user session ////////
                        editor.putString("first_name", user_info.get("first_name").toString());
                        editor.putString("mail", user_info.get("email").toString());
                        editor.commit(); // commit changes

                        /////// Launch next activity //////
                        Intent intent = new Intent(Login.this, LoginTransition.class);
                        startActivity(intent);
                        finish();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == CODE_POST_CREATE){
                try{
                    Log.d("debug_create", s);
                    JSONObject object = new JSONObject(s);

                    //////// Create user session ////////
                    editor.putString("first_name", user_info.get("first_name").toString());
                    editor.putString("mail", user_info.get("email").toString());
                    editor.commit(); // commit changes

                }catch(JSONException e){
                    e.printStackTrace();
                }

                /////// Launch next activity //////
                Intent intent = new Intent(Login.this, UserPreference.class);
                startActivity(intent);
                finish();
            }
        }
    }


}
