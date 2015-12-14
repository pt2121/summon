package com.prt2121.summon.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/13/15.
 */
data class Request(
    @Expose
    val status: String,
    @Expose
    val driver: Driver,
    @Expose
    val eta: Int,
    @Expose
    val location: Location,
    @Expose
    val vehicle: Vehicle,
    @SerializedName("request_id")
    @Expose
    val requestId: String
)