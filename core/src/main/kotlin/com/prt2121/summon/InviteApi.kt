package com.prt2121.summon

import retrofit.*
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.POST
import retrofit.http.Path

/**
 * Created by pt2121 on 12/12/15.
 */
class InviteApi {

  private fun api(): Api {
    val retrofit = Retrofit.Builder().baseUrl(INVITE_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    return retrofit.create(Api::class.java)
  }

  fun invite(invite: Invite, onCompleted: (Invite?) -> Unit) {
    val api = api()
    api.createInvite(invite).enqueue(object : Callback<Invite> {
      override fun onResponse(response: Response<Invite>?, retrofit: Retrofit?) {
        if (response!!.isSuccess) {
          onCompleted.invoke(response.body())
        }
      }

      override fun onFailure(t: Throwable?) {
        println("failed ${t?.message}")
      }

    })
  }

  fun query(id: String, onCompleted: (Invite?) -> Unit) {
    val api = api()
    api.getInvite(id).enqueue(object : Callback<Invite> {
      override fun onResponse(response: Response<Invite>?, retrofit: Retrofit?) {
        if (response!!.isSuccess) {
          onCompleted.invoke(response.body())
        }
      }

      override fun onFailure(t: Throwable?) {
        println("failed ${t?.message}")
      }

    })
  }

  public interface Api {
    @POST("/invites")
    fun createInvite(@Body invite: Invite): Call<Invite>

    @GET("/invites/{id}")
    fun getInvite(@Path("id") id: String): Call<Invite>
  }

  companion object {
    val INVITE_BASE_URL: String = "https://intense-waters-9652.herokuapp.com/"
    val instance = InviteApi()
  }
}