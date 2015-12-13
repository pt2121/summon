package com.prt2121.summon.model

/**
 * Created by pt2121 on 12/13/15.
 */
data class Estimate(
    val productId: String,
    val currencyCode: String,
    val localizedDisplayName: String,
    val displayName: String,
    val priceEstimate: String,
    val lowEstimate: Int,
    val highEstimate: Int,
    val surgeMultiplier: Float,
    val timeEstimate: Int
)