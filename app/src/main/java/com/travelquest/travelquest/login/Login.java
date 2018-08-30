package com.travelquest.travelquest.login;


// Front End Dependencies
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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


public class Login extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private static final int CODE_POST_CREATE = 1026;
    private static final int CODE_POST_EXISTS = 1027;

    Button register;
    CallbackManager callbackManager;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        ////// Initialize activity /////
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        // Configure classic signin/register
        register = (Button) findViewById(R.id.email_sign_in_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userExists("nicolas.li@hotmail.fr", "");
            }
        });

        // Configure Facebook login process
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //getUserDetails(loginResult);
                //createUser(loginResult);
                //TODO: generalize mail
                userExists("nicolas.li@hotmail.fr", loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
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


        /////// For testing purpose //////
        register =(Button) findViewById((R.id.email_sign_in_button));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent= new Intent(Login.this, UserPreference.class);
                //startActivity(intent);
                userExists("nicolas.li@hotmail.fr", null);
            }
        });
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

   private void createUser(final LoginResult loginResult) {
       final HashMap<String, String> params = new HashMap<>();
       GraphRequest data_request = GraphRequest.newMeRequest(
               loginResult.getAccessToken(),
               new GraphRequest.GraphJSONObjectCallback() {
                   @Override
                   public void onCompleted(JSONObject object, GraphResponse response) {
                    try {

                        params.put("first_name", object.get("first_name").toString());
                        params.put("last_name", object.get("last_name").toString());
                        params.put("gender", "Unknown");
                        params.put("mail", object.get("email").toString());
                        params.put("password", "");
                        params.put("accountType", "facebook");

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                       //Calling the create hero API
                       PerformNetworkRequest request = new PerformNetworkRequest(API.URL_CREATE_USER, params, CODE_POST_CREATE, loginResult);
                       request.execute();

                   }
               }
       );
       Bundle permission_param = new Bundle();
       permission_param.putString("fields", "id,first_name,last_name,email,picture.width(120).height(120)");
       data_request.setParameters(permission_param);
       data_request.executeAsync();

   }

    /**
     * This function returns the fullname of the user if he/she exists or null if not
     * @param mail
     * @return String or null
     */
   private void userExists(String mail, LoginResult loginResult){
       HashMap<String, String> params = new HashMap<>();
       params.put("mail", mail);

       PerformNetworkRequest request = new PerformNetworkRequest(API.URL_USER_EXISTS, params, CODE_POST_EXISTS, loginResult);

       request.execute();

       return;
   }

    private void userExists(String mail, String password){
        HashMap<String, String> params = new HashMap<>();
        params.put("mail", mail);

        PerformNetworkRequest request = new PerformNetworkRequest(API.URL_USER_EXISTS, params, CODE_POST_EXISTS, loginResult);

        request.execute();

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

        LoginResult loginResult;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode, LoginResult loginResult) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.loginResult = loginResult;
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
            facebookLog(s);

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

        protected void facebookLog(String s){
            if (requestCode == CODE_POST_EXISTS){
                try{
                    Log.d("debug", s);
                    JSONObject object = new JSONObject(s);
                    if(object.get("user_info").toString().compareTo("[]") == 0){// The user doesn't exist
                        createUser(loginResult);
                    }else{// The user exists

                        Log.d("debug", object.getJSONArray("user_info").getJSONObject(0).toString());
                        //////// Create user session ////////
                        JSONObject user_info = object.getJSONArray("user_info").getJSONObject(0);
                        editor.putString("first_name", user_info.get("first_name").toString());
                        editor.putString("mail", user_info.get("mail").toString());
                        editor.commit(); // commit changes

                        /////// Launch next activity //////
                        Intent intent = new Intent(Login.this, LoginTransition.class);
                        startActivity(intent);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == CODE_POST_CREATE){
                try{
                    Log.d("debug_create", s);
                    JSONObject object = new JSONObject(s);

                    //////// Create user session ////////
                    JSONObject user_info = object.getJSONArray("user_info").getJSONObject(0);
                    editor.putString("first_name", user_info.get("first_name").toString());
                    editor.putString("mail", user_info.get("mail").toString());
                    editor.commit(); // commit changes

                }catch(JSONException e){
                    e.printStackTrace();
                }

                /////// Launch next activity //////
                Intent intent = new Intent(Login.this, UserPreference.class);
                startActivity(intent);
            }
        }
    }


}
