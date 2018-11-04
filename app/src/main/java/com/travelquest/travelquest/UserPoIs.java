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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.PoI;
import com.travelquest.travelquest.database_handler.PoIAdapter;
import com.travelquest.travelquest.database_handler.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserPoIs extends BaseNavActivity{
    SharedPreferences pref;
    TextView pageTitle;

    private ListView poisView = null;
    private List<PoI> poisList = null;
    private PoIAdapter poiAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pois);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

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



        pageTitle = (TextView) findViewById(R.id.userPoIsTitle);
        pageTitle.setText(pref.getString("first_name", null) + "'s discoveries");

        poisView = (ListView) findViewById(R.id.poisView);

        // Populate
        poisList = new ArrayList<>();

        HashMap<String, String > params = new HashMap<>();
        params.put("id_user", pref.getString("id_user", null));
        UserPoIs.PerformNetworkRequest requestUserPois = new UserPoIs.PerformNetworkRequest(API.URL_GET_USER_POIS, API.POST, params);
        requestUserPois.execute();

        poisView.setOnItemClickListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(UserPoIs.this, PoIInfo.class);
            PoI poi = (PoI) parent.getItemAtPosition(position);

            intent.putExtra("poi", poi);
            startActivity(intent);
        }
    };



    /**
     * inner class to perform network request extending an AsyncTask
     */
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;
        String type;
        HashMap<String, String> params;



        //constructor to initialize values
        PerformNetworkRequest(String url, String type) {
            this.url = url;
            this.type = type;
        }

        PerformNetworkRequest(String url, String type, HashMap<String, String> params) {
            this.url = url;
            this.type = type;
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
                    Toast.makeText(getApplicationContext(), "Points of interest loaded successfully", Toast.LENGTH_LONG).show();

                stringToPois(temp);

            }catch (JSONException e){
                e.printStackTrace();
            }

        }


        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            if(this.type == API.GET)
                return requestHandler.sendGetRequest(url);
            else if (this.type == API.POST)
                return requestHandler.sendPostRequest(url, this.params);
            else
                return "Error in send request";
        }

        protected void stringToPois(JSONObject pois){
            try {
                JSONArray pois_array = pois.getJSONArray("pois");
                int count = pois_array.length();
                for(int i = 0; i < count; i++){
                    JSONObject pois_json = pois_array.getJSONObject(i);
                    double lat = pois_json.getDouble("latitude");
                    double lng = pois_json.getDouble("longitude");
                    String title = pois_json.getString("title");
                    String country = pois_json.getString("country");
                    String description = pois_json.getString("description");
                    String imageLink = pois_json.getString("imageLink");
                    int idPoi = pois_json.getInt("id_poi");
                    PoI newPoI = new PoI(idPoi, lat, lng, country, title, description, imageLink);

                    if(this.url == API.URL_GET_POIS)
                        poisList.add(newPoI);
                    else if (this.url == API.URL_GET_USER_POIS) {
                        poisList.add(newPoI);
                    }
                }
                poiAdapter = new PoIAdapter(UserPoIs.this,android.R.layout.simple_list_item_1, poisList);
                poisView.setAdapter(poiAdapter);
            }catch(Exception e){
                Log.e("json error", this.url + '\n'+ e.toString());
            }
        }
    }
}
