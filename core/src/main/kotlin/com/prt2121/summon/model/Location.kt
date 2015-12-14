package com.prt2121.summon.model

import com.google.gson.annotations.Expose

/**
 * Created by pt2121 on 12/13/15.
 */
data class Location(
    @Expose
    val latitude: Double,
    @Expose
    val longitude: Double,
    @Expose
    private val bearing: Int
)