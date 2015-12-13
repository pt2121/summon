package com.prt2121.summon

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by pt2121 on 12/8/15.
 */
data class Invite(

    @SerializedName("_id") @Expose
    val _id: String? = null,

    @SerializedName("from") @Expose
    val from: User,

    @SerializedName("to") @Expose
    val to: User,

    @SerializedName("destinationLatLng") @Expose
    val destinationLatLng: String,

    @SerializedName("destinationAddress") @Expose
    val destinationAddress: String,

    @SerializedName("message") @Expose
    val message: String,

    @SerializedName("status") @Expose
    val status: Status = Status.PENDING,

    @SerializedName("pickupAddress") @Expose
    val pickupAddress: String = ""
)

enum class Status {
  PENDING, ACCEPT, REJECT, CANCEL
}

data class User(

    @SerializedName("firstName") @Expose
    val firstName: String,

    @SerializedName("lastName") @Expose
    val lastName: String,

    @SerializedName("phoneNumber") @Expose
    val phoneNumber: String
)