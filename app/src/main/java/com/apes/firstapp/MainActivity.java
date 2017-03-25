package com.apes.firstapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.apes.firstapp.TempManager.COLDER_BY;
import static com.apes.firstapp.TempManager.FROM_YESTERDAY;
import static com.apes.firstapp.TempManager.PAGE;
import static com.apes.firstapp.TempManager.REQ_UNSPLASH_URL;
import static com.apes.firstapp.TempManager.SAME_AS_YESTERDAY;
import static com.apes.firstapp.TempManager.WARMER_BY;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Map.Entry<String, String>> arr =
            new ArrayList<Map.Entry<String, String>>();

    private Context mContext;

    public Context getContext(){
        return mContext;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Intent i = getIntent();
        Temp todaysTemp = (Temp) i.getSerializableExtra("today");
        Temp yestredaysTemp = (Temp) i.getSerializableExtra("yesterday");

        buildUI(todaysTemp, yestredaysTemp);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                swipeContainer.setRefreshing(true);
                onReload();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void onReload() {
        Temp[] temp = TempManager.fetchTimelineAsync(getContext());
        buildUI(temp[0], temp[1]);
        swipeContainer.setRefreshing(false);
    }

    private void buildUI(Temp todaysTemp, Temp yestredaysTemp) {
        int avgTempToday = (int) Math.round((todaysTemp.temperatureMax - todaysTemp.temperatureMin) / 2);
        String avgTempText = avgTempToday + todaysTemp.degree;
        TextView tvTodaysTemp = (TextView) findViewById(R.id.text_temp);
        tvTodaysTemp.setText(avgTempText);
        TextView tvSummary = (TextView) findViewById(R.id.text_summary);
        tvSummary.setText(todaysTemp.summary);

        String imgUrl = todaysTemp.imgURL;
        Log.d("imgURL", imgUrl);
        ImageView imageView = (ImageView) findViewById(R.id.img);
        imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        Ion.with(imageView).load(imgUrl);

        int avgTempYesterday = (int) Math.round((yestredaysTemp.temperatureMax - yestredaysTemp.temperatureMin) / 2);
        int absTempDiv = Math.round(Math.abs(avgTempYesterday - avgTempToday));
        String yesterdayTest = COLDER_BY + absTempDiv + yestredaysTemp.degree + FROM_YESTERDAY;
        if (avgTempYesterday > avgTempToday) {
            yesterdayTest = WARMER_BY + absTempDiv + yestredaysTemp.degree + FROM_YESTERDAY;
        }
        if (avgTempYesterday == avgTempToday) {
            yesterdayTest = SAME_AS_YESTERDAY;
        }
        TextView tvYesterdayTemp = (TextView) findViewById(R.id.text_yesterday);
        tvYesterdayTemp.setText(yesterdayTest);
    }


}
