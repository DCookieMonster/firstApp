package com.apes.firstapp;

/**
 * Created by dor on 22/03/2017.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Temp implements Serializable {

    @SerializedName("summary")
    String summary;

    @SerializedName("icon")
    String icon;

    @SerializedName("temperatureMin")
    double temperatureMin;

    @SerializedName("temperatureMax")
    double temperatureMax;

    final String degree = "\u00B0";

    String imgURL;

}
