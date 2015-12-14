package com.prt2121.summon.model

/**
 * Created by pt2121 on 12/13/15.
 */
data class PriceEstimate(
    val product_id: String,
    val currency_code: String?,
    val localized_display_name: String,
    val display_name: String,
    val estimate: String,
    val low_estimate: Int,
    val high_estimate: Int,
    val surge_multiplier: Float
)