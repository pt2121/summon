package com.prt2121.summon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/1/15.
 */

data class AuthResponse(

    @SerializedName("access_token")
    @Expose
    val accessToken: String,

    @SerializedName("token_type")
    @Expose
    val tokenType: String,

    @SerializedName("expires_in")
    @Expose
    var expiresIn: Int,

    @SerializedName("refresh_token")
    @Expose
    var refreshToken: String,

    @SerializedName("scope")
    @Expose
    var scope: String

)