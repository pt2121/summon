package com.prt2121.summon.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/13/15.
 */
class Vehicle(
    @Expose
    val make: String,
    @Expose
    val model: String,
    @SerializedName("license_plate")
    @Expose
    val licensePlate: String,
    @SerializedName("picture_url")
    @Expose
    val pictureUrl: String
)