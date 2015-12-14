package com.prt2121.summon.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/13/15.
 */
data class RequestRequest(
    @Expose
    @SerializedName("start_latitude")
    val startLatitude: String,
    @Expose
    @SerializedName("start_longitude")
    val startLongitude: String,
    @Expose
    @SerializedName("end_latitude")
    val endLatitude: String,
    @Expose
    @SerializedName("end_longitude")
    val endLongitude: String,
    @SerializedName("product_id")
    @Expose
    val productId: String
)