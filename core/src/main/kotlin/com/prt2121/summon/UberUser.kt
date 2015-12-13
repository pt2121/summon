package com.prt2121.summon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/12/15.
 */
data class UberUser(

    @SerializedName("first_name") @Expose
    val firstName: String,

    @SerializedName("last_name") @Expose
    val lastName: String,

    @SerializedName("email") @Expose
    val email: String,

    @SerializedName("picture") @Expose
    val picture: String,

    @SerializedName("promo_code") @Expose
    val promoCode: String,

    @SerializedName("uuid") @Expose
    val uuid: String

)