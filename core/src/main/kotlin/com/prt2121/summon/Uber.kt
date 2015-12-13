package com.prt2121.summon

import com.prt2121.summon.model.PriceEstimateList
import com.prt2121.summon.model.TimeEstimateList
import retrofit.*
import retrofit.http.GET
import retrofit.http.Header
import retrofit.http.POST
import retrofit.http.Query
import rx.Observable

/**
 * Created by pt2121 on 12/1/15.
 */
class Uber {

  val loginApi = loginApi()
  val api = api()

  private fun api(): Api {
    val retrofit = Retrofit.Builder().baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build()
    return retrofit.create(Api::class.java)
  }

  private fun loginApi(): LoginApi {
    val retrofit = Retrofit.Builder().baseUrl(LOGIN_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    return retrofit.create(LoginApi::class.java)
  }

  fun auth(code: String, onCompleted: (String?) -> Unit) {
    loginApi.authToken(SECRET, ID, GRANT_TYPE, code, REDIRECT_URL)
        .enqueue(object : Callback<AuthResponse> {
          override fun onResponse(response: Response<AuthResponse>?, retrofit: Retrofit?) {
            if (response!!.isSuccess) {
              val body = response.body()
              onCompleted.invoke("${body.tokenType} ${body.accessToken}")
            }
          }

          override fun onFailure(t: Throwable?) {
            println("failed ${t?.message}")
            onCompleted.invoke(null)
          }
        })
  }

  fun me(authToken: String, onCompleted: (UberUser) -> Unit) {
    api.me(authToken).enqueue(object : Callback<UberUser> {
      override fun onFailure(t: Throwable?) {
        println("failed ${t?.message}")
      }

      override fun onResponse(response: Response<UberUser>?, retrofit: Retrofit?) {
        if (response!!.isSuccess) {
          val body = response.body()
          onCompleted.invoke(body)
        }
      }
    })
  }

  fun timeEstimates(authToken: String, startLatitude: Double, startLongitude: Double): Observable<TimeEstimateList> {
    return api.timeEstimates(authToken, startLatitude, startLongitude)
  }

  fun priceEstimates(authToken: String, startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double): Observable<PriceEstimateList> {
    return api.priceEstimates(authToken, startLatitude, startLongitude, endLatitude, endLongitude)
  }

  public interface LoginApi {
    @POST("/oauth/token")
    fun authToken(@Query("client_secret") clientSecret: String,
                  @Query("client_id") clientId: String,
                  @Query("grant_type") grantType: String,
                  @Query("code") code: String,
                  @Query("redirect_uri") redirectUrl: String
    ): Call<AuthResponse>
  }

  public interface Api {
    @GET("/v1/me")
    fun me(@Header("Authorization") authToken: String): Call<UberUser>

    @GET("/v1/estimates/time")
    fun timeEstimates(@Header("Authorization") authToken: String,
                      @Query("start_latitude") startLatitude: Double,
                      @Query("start_longitude") startLongitude: Double): Observable<TimeEstimateList>

    @GET("/v1/estimates/price")
    fun priceEstimates(@Header("Authorization") authToken: String,
                       @Query("start_latitude") startLatitude: Double,
                       @Query("start_longitude") startLongitude: Double,
                       @Query("end_latitude") endLatitude: Double,
                       @Query("end_longitude") endLongitude: Double): Observable<PriceEstimateList>
  }

  companion object {
    val LOGIN_BASE_URL = "https://login.uber.com/"
    val API_URL = "https://sandbox-api.uber.com/"
    val SECRET = "SiTDLslIxQOjSzRTRoAN9jllM7RqRr5r_fVeOvp0"
    val ID = "SbShEB9EQK8Kz1NwkUyVMkFdrQtYhRhE"
    val REDIRECT_URL = "https://localhost:8000"
    val GRANT_TYPE = "authorization_code"
    val LOGIN_URL = "${LOGIN_BASE_URL}oauth/authorize?response_type=code&client_id=$ID&scope=profile+request&redirect_uri=https%3A%2F%2Flocalhost:8000"
    val instance = Uber()
  }
}