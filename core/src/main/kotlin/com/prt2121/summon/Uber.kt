package com.prt2121.summon

import retrofit.*
import retrofit.http.POST
import retrofit.http.Query

/**
 * Created by pt2121 on 12/1/15.
 */
class Uber {

  private fun api(): Api {
    val retrofit = Retrofit.Builder().baseUrl(LOGIN_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    return retrofit.create(Api::class.java)
  }

  fun auth(code: String) {
    val api = api()
    api.authToken(SECRET, ID, GRANT_TYPE, code, REDIRECT_URL)
        .enqueue(object : Callback<AuthResponse> {
          override fun onResponse(response: Response<AuthResponse>?, retrofit: Retrofit?) {
            val token = if (response!!.isSuccess) {
              val body = response.body()
              body.accessToken
            } else {
              null
            }
          }

          override fun onFailure(t: Throwable?) {
            println("failed ${t?.message}")
          }
        })
  }

  public interface Api {
    @POST("/oauth/token")
    fun authToken(@Query("client_secret") clientSecret: String,
                  @Query("client_id") clientId: String,
                  @Query("grant_type") grantType: String,
                  @Query("code") code: String,
                  @Query("redirect_uri") redirectUrl: String
    ): Call<AuthResponse>
  }

  companion object {
    val LOGIN_BASE_URL: String = "https://login.uber.com/"
    val SECRET: String = "SiTDLslIxQOjSzRTRoAN9jllM7RqRr5r_fVeOvp0"
    val ID: String = "SbShEB9EQK8Kz1NwkUyVMkFdrQtYhRhE"
    val REDIRECT_URL = "https://localhost:8000"
    val GRANT_TYPE = "authorization_code"
    val LOGIN_URL = "${LOGIN_BASE_URL}oauth/authorize?response_type=code&client_id=$ID&scope=profile+request&redirect_uri=https%3A%2F%2Flocalhost:8000"   //$REDIRECT_URL"
    fun newInstance(): Uber = Uber()
  }
}