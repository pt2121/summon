package com.prt2121.summon.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/13/15.
 */
data class Driver(
    @SerializedName("phone_number")
    @Expose
    val phoneNumber: String,
    @Expose
    val rating: Float,
    @SerializedName("picture_url")
    @Expose
    val pictureUrl: String,
    @Expose
    val name: String
)