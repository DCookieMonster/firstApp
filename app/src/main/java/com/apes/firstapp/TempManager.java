package com.apes.firstapp;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by dor on 24/03/2017.
 */

public class TempManager extends Application {

    static final String SAME_AS_YESTERDAY = "Same As Yesterday";
    static final String FROM_YESTERDAY = "\nFrom Yesterday";
    static final String COLDER_BY = "Colder By ";
    static final String WARMER_BY = "Warmer By ";
    static final String DATA = "data";
    static final String DAILY = "daily";
    static final String DARKSKY_URL = "https://api.darksky.net/forecast/82b05953baf3b7b7396dddd452886009/";
    static final String DARKSKY_PARAMS = "?units=auto&exclude=hourly,flags";
    static final String CLIENT_ID_UNSPLASH = "&client_id=bd822ee6e22311ce7b774014c0dd7806ef5d3181cdd0eaaa7e8d3c9c8dc7a749";
    static final String REQ_UNSPLASH_URL = "https://api.unsplash.com/search/photos?per_page=1&query=";
    static final String PAGE = "&page=";
    static final int DAY_IN_MINUTES = 60 * 60 * 24;
    static final Temp[] temp = new Temp[2];
    static double[] getGPS;

    public static Temp[] fetchTimelineAsync(Context context){
        getGPS = getGPS(context);
//        getGPS[0] = Math.random()*40;
//        getGPS[1] =  Math.random()*40;

        long currentTimeSec = System.currentTimeMillis() / 1000;
        final String URL = DARKSKY_URL + getGPS[0] + "," + getGPS[1] + "," + currentTimeSec + DARKSKY_PARAMS;
        loadTempObject(context, URL, 0);
        long yesterdayTime = currentTimeSec - DAY_IN_MINUTES;
        final String yesterdayUrl = DARKSKY_URL + getGPS[0] + "," + getGPS[1] + "," + yesterdayTime + DARKSKY_PARAMS;
        loadTempObject(context, yesterdayUrl, 1);
        int pageNum = (int) Math.random() * 10;
        String unsplashAPIUrl = REQ_UNSPLASH_URL + temp[0].icon + CLIENT_ID_UNSPLASH + PAGE + pageNum;
        temp[0].imgURL = loadTempImgURL(context, unsplashAPIUrl);

        return temp;
    }

    private static void loadTempObject(Context context, String URL, int index){
        JsonObject response;
        try {
            response = Ion.with(context)
                    .load(URL).asJsonObject().get();
            Gson gson = new Gson();
            temp[index] = gson.fromJson( response.getAsJsonObject(DAILY).getAsJsonArray(DATA).get(0), Temp.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static String loadTempImgURL(Context context, String URL){
        JsonObject response;
        try {
            response = Ion.with(context)
                    .load(URL).asJsonObject().get();
            String imgUrl = response.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("urls").get("regular").getAsString();
            return imgUrl;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return URL;
    }

    private static void volleyLoadTempObject(String URL, int index) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest req = new JsonObjectRequest(URL, new  JSONObject(), future, future);
        // add the request object to the queue to be executed
        NetworkController.getInstance().addToRequestQueue(req);
        try {
            JSONObject response = future.get(); // this will block (forever)
            VolleyLog.v("Response:%n %s", response.toString(4));
            Gson gson = new Gson();
            temp[index] = gson.fromJson((JsonElement) response.getJSONObject(DAILY).getJSONArray(DATA).get(0), Temp.class);
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private static double[] getGPS(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

/* Loop over the array backwards, and if you get an accurate location, then break                 out the loop*/
        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }

}


