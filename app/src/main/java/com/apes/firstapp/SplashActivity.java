package com.apes.firstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Downloader;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Created by dor on 23/03/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        Intent intent = new Intent(this, MainActivity.class);
        Temp [] temp = TempManager.fetchTimelineAsync(this);
        intent.putExtra("today", temp[0]);
        intent.putExtra("yesterday", temp[1]);
        startActivity(intent);
        finish();
    }

    public Context getContext(){
        return mContext;
    }



}
