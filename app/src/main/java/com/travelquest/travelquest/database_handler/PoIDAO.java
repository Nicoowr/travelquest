//package com.travelquest.travelquest.database_handler;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//
///**
// * inner class to perform network request extending an AsyncTask
// */
//public class PoIDAO extends AsyncTask<Void, Void, String> {
//
//    //the url where we need to send the request
//    String url;
//    String type;
//    HashMap<String, String> params;
//    Context context;
//
//
//
//    //constructor to initialize values
//    PoIDAO(Context context, String url, String type) {
//        this.context = context;
//        this.url = url;
//        this.type = type;
//    }
//
//    PoIDAO(Context context, String url, String type, HashMap<String, String> params) {
//        this.context = context;
//        this.url = url;
//        this.type = type;
//        this.params = params;
//    }
//
//    //when the task started displaying a progressbar
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//
//    //this method will give the response from the request
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        Log.d("debug_update", s);
//        try {
//            JSONObject temp = new JSONObject(s);
//            boolean result = temp.getBoolean("error");
//            if(result)
//                Toast.makeText(context, "An error occurred.", Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(context, "Update successful", Toast.LENGTH_LONG).show();
//
//
//            //if(this.url == API.URL_GET_POIS)
//            stringToPois(temp);
//            //else if (this.url == API.URL_GET_USER_POIS) {
//            //    Log.d("debug_Knownpois", temp.toString());
//            //    storeKnownPois(temp);
//            //}
//
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//
//    }
//
//
//    //the network operation will be performed in background
//    @Override
//    protected String doInBackground(Void... voids) {
//        RequestHandler requestHandler = new RequestHandler();
//        if(this.type == API.GET)
//            return requestHandler.sendGetRequest(url);
//        else if (this.type == API.POST)
//            return requestHandler.sendPostRequest(url, this.params);
//        else
//            return "Error in send request";
//    }
//
//    protected void stringToPois(JSONObject pois){
//        try {
//            JSONArray pois_array = pois.getJSONArray("pois");
//            int count = pois_array.length();
//            for(int i = 0; i < count; i++){
//                JSONObject pois_json = pois_array.getJSONObject(i);
//                double lat = pois_json.getDouble("latitude");
//                double lng = pois_json.getDouble("longitude");
//                String title = pois_json.getString("title");
//                String country = pois_json.getString("country");
//                String description = pois_json.getString("description");
//                String imageLink = pois_json.getString("imageLink");
//                int idPoi = pois_json.getInt("id_poi");
//                PoI newPoI = new PoI(idPoi, lat, lng, country, title, description, imageLink);
//
//                if(this.url == API.URL_GET_POIS)
//                    mPois.add(newPoI);
//                else if (this.url == API.URL_GET_USER_POIS) {
//                    mUserPois.add(newPoI);
//                    mMap.addMarker(new MarkerOptions().position(newPoI.getPosition()).title(title));
//                }
//            }
//            //Log.d("debug_pois", Double.toString(pois.getJSONArray("pois").getJSONObject(0).getDouble("latitude")));
//        }catch(Exception e){
//            Log.e("json error", this.url + '\n'+ e.toString());
//        }
//    }
//
//        /*protected void storeKnownPois(JSONObject pois){
//            try{
//                JSONArray pois_array = pois.getJSONArray("id_pois");
//                int count = pois_array.length();
//                for(int i = 0; i < count; i++){
//                    JSONObject poi_json = pois_array.getJSONObject(i);
//                    int id_poi = poi_json.getInt("id_poi");
//
//                }
//            }catch(Exception e){
//                Log.e("json error", e.toString());
//            }
//        }*/
//}
