package com.prt2121.summon.model

/**
 * Created by pt2121 on 12/13/15.
 */
data class TimeEstimate(
    val product_id: String,
    val localized_display_name: String,
    val display_name: String,
    val estimate: Int
)