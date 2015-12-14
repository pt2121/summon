package com.prt2121.summon

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng
import com.pkmmte.view.CircularImageView
import com.prt2121.summon.model.Driver
import com.prt2121.summon.model.RequestRequest
import com.prt2121.summon.model.Vehicle
import com.squareup.picasso.Picasso
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by pt2121 on 12/13/15.
 */
class RequestDetailActivityFragment : Fragment() {

  private var requestId: String? = null
  private var subscription: Subscription? = null
  private var productName: String? = null
  private var productId: String? = null
  private var token: String? = null
  private var start: LatLng? = null
  private var destination: LatLng? = null
  private var animatedAccepted: Boolean = false
  private var animatedCancelled: Boolean = false

  private val detailLayout: View  by bindView(R.id.detailLayout)
  private val statusTextView: TextView  by bindView(R.id.requestStatusTextView)
  private val etaTextView: TextView  by bindView(R.id.etaTextView)
  private val driverNameTextView: TextView  by bindView(R.id.driverNameTextView)
  private val phoneNumberTextView: TextView  by bindView(R.id.phoneNumberTextView)
  private val ratingTextView: TextView  by bindView(R.id.ratingTextView)
  private val profileImage: CircularImageView  by bindView(R.id.profileImageView)
  private val modelTextView: TextView  by bindView(R.id.modelTextView)
  private val licensePlateTextView: TextView  by bindView(R.id.licensePlateTextView)
  private val makeTextView: TextView  by bindView(R.id.makeTextView)
  private val vehicleImageView: CircularImageView  by bindView(R.id.vehicleImageView)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    token = TokenStorage(activity).retrieve()
    if (arguments != null) {
      start = arguments.getParcelable(RequestDetailActivity.START_LOCATION)
      destination = arguments.getParcelable(RequestDetailActivity.DESTINATION_LOCATION)
      productName = arguments.getString(RequestDetailActivity.PRODUCT_NAME)
      productId = arguments.getString(RequestDetailActivity.PRODUCT_ID)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater!!.inflate(R.layout.fragment_request_detail, container, false)
    val icon = Utils.findIcon(productName!!)
    (view.findViewById(R.id.productImageView) as ImageView).setImageResource(icon)
    view.findViewById(R.id.cancel_button).setOnClickListener {
      if (subscription != null && !subscription!!.isUnsubscribed) {
        subscription!!.unsubscribe()
        Uber.instance.cancelRequest(token!!, requestId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              println(it)
              if (!animatedCancelled) {
                animatedCancelled = true
                animateBackgroundColor(detailLayout, "#F44336", "#FFFFFF")
              }
            }, {
              println(it.message)
            })
      }
    }
    val requestRequest = makeRequestRequest()
    Observable.interval(10, TimeUnit.SECONDS, Schedulers.io()).startWith(-1L)
        .flatMap {
          Uber.instance.createRequest(token!!, requestRequest).cache()
        }
        .flatMap {
          requestId = it.requestId
          Uber.instance.getRequestStatus(token!!, requestId!!)
        }
        .doOnNext {
          activity.runOnUiThread {
            statusTextView.text = "Status: " + it.status
          }
        }
        .filter {
          it.status.toLowerCase() == "accepted"
        }
        .take(1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe({ request ->
          if (!animatedAccepted) {
            animatedAccepted = true
            animateBackgroundColor(detailLayout, "#A7FFEB", "#FFFFFF")
          }
          etaTextView.text = "Eta: ${request.eta} minutes"
          setupDriverCard(request.driver)
          setupVehicleCard(request.vehicle)
        }, {
          println(it.message)
        })

    return view
  }

  private fun makeRequestRequest(): RequestRequest {
    return RequestRequest(destination!!.latitude.toString(),
        destination!!.longitude.toString(),
        start!!.latitude.toString(),
        start!!.longitude.toString(),
        productId!!
    )
  }

  private fun setupVehicleCard(vehicle: Vehicle) {
    makeTextView.text = vehicle.make
    modelTextView.text = vehicle.model
    licensePlateTextView.text = vehicle.licensePlate
    Picasso.with(activity).load(vehicle.pictureUrl).placeholder(R.drawable.progress_animation).resize(64, 64).into(vehicleImageView)
  }

  private fun setupDriverCard(driver: Driver) {
    driverNameTextView.text = driver.name
    phoneNumberTextView.text = driver.phoneNumber
    ratingTextView.text = driver.rating.toString()
    Picasso.with(activity).load(driver.pictureUrl).placeholder(R.drawable.progress_animation).resize(64, 64).into(profileImage)
  }

  private fun animateBackgroundColor(view: View, fromColor: String, toColor: String) {
    val from = FloatArray(3)
    val to = FloatArray(3)
    Color.colorToHSV(Color.parseColor(fromColor), from) // from white
    Color.colorToHSV(Color.parseColor(toColor), to) // to red
    val anim = ValueAnimator.ofFloat(0f, 1f) // animate from 0 to 1
    anim.setDuration(1000) // for 1000 ms
    val hsv = FloatArray(3) // transition color
    anim.addUpdateListener { // Transition along each axis of HSV (hue, saturation, value)
      hsv[0] = from[0] + (to[0] - from[0]) * it.animatedFraction
      hsv[1] = from[1] + (to[1] - from[1]) * it.animatedFraction
      hsv[2] = from[2] + (to[2] - from[2]) * it.animatedFraction
      view.setBackgroundColor(Color.HSVToColor(hsv))
    }
    anim.start()
  }

  companion object {
    val TAG = RequestDetailActivityFragment::class.java.simpleName

    fun newInstance(start: LatLng, destination: LatLng, productName: String,
                    productId: String): RequestDetailActivityFragment {
      val fragment = RequestDetailActivityFragment()
      val args = Bundle()
      args.putParcelable(RequestDetailActivity.START_LOCATION, start)
      args.putParcelable(RequestDetailActivity.DESTINATION_LOCATION, destination)
      args.putString(RequestDetailActivity.PRODUCT_NAME, productName)
      args.putString(RequestDetailActivity.PRODUCT_ID, productId)
      fragment.arguments = args
      return fragment
    }
  }
}