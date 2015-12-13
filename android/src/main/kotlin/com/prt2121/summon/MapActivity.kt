package com.prt2121.summon

/**
 * Created by pt2121 on 12/13/15.
 */
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MapsActivity : FragmentActivity() {
  val dropOffAddress: EditText by bindView(R.id.dropOffAddressEditText)
  val estimateButton: Button by bindView(R.id.estimateButton)
  private var dropOffLatLng: LatLng? = null
  private var subscription: Subscription? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_maps)

    val extras = intent?.extras
    val pickupLatLng = extras?.get(PICKUP_LATLNG_EXTRA) ?: LatLng(40.750572, -73.995713)
    dropOffLatLng = if (extras?.get(DROPOFF_LATLNG_EXTRA) != null) extras?.get(DROPOFF_LATLNG_EXTRA) as LatLng else LatLng(40.7234175, -74.3093816)
    if (extras?.get(DROPOFF_ADDRESS_EXTRA) != null) {
      dropOffAddress.setText(extras?.getString(DROPOFF_ADDRESS_EXTRA))
    } else {
      dropOffAddress.setText("")
    }

    updateFragment(
        MapFragment.newInstance(pickupLatLng as LatLng, dropOffLatLng as LatLng),
        MapFragment.TAG)

    dropOffAddress.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          val imm = v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
          imm.hideSoftInputFromWindow(v.windowToken, 0)
          subscription = Observable.just(getLocationFromAddress(this@MapsActivity, v.text.toString()))
              .subscribeOn(Schedulers.newThread())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe({
                dropOffLatLng = it
                val f = supportFragmentManager.findFragmentByTag(MapFragment.TAG) as MapFragment
                f.addDestinationMarker(it!!)
              }, {
                Log.e(TAG, it.message)
              })
        }
        return false
      }

    })

    estimateButton.setOnClickListener(object : View.OnClickListener {
      override fun onClick(v: View) {
        if (dropOffLatLng == null) {
          Toast.makeText(this@MapsActivity, "Please enter your location", Toast.LENGTH_LONG).show()
        } else {
          Uber.instance.api
              .timeEstimates(TokenStorage(this@MapsActivity).retrieve()!!, 40.750572, -73.995713)
              .subscribeOn(Schedulers.io())
              .subscribe({
                it.times.forEach { println("${it.display_name} ${it.estimate}") }
              }, {
                println(it.message)
              })

          Uber.instance.api
              .priceEstimates(TokenStorage(this@MapsActivity).retrieve()!!, 40.750572, -73.995713, 40.7234175, -74.3093816)
              .subscribeOn(Schedulers.io())
              .subscribe({
                it.prices.forEach { println("${it.display_name} ${it.estimate}") }
              }, {
                println(it.message)
              })
        }
      }
    })
  }

  protected fun updateFragment(fragment: Fragment, fragmentName: String) {
    val ft = supportFragmentManager.beginTransaction()
    ft.replace(R.id.content_frame, fragment, fragmentName)
    ft.commitAllowingStateLoss()
  }

  fun getLocationFromAddress(context: Context, addressStr: String): LatLng? {
    val coder = Geocoder(context)
    val address: List<Address>
    var latLng: LatLng? = null
    try {
      address = coder.getFromLocationName(addressStr, 5)
      if (address == null) {
        return null
      }
      val location = address[0]
      latLng = LatLng(location.latitude, location.longitude)
    } catch (ex: Exception) {
      ex.printStackTrace()
    }
    return latLng
  }

  override fun onDestroy() {
    super.onDestroy()
    if (subscription != null && !subscription!!.isUnsubscribed) {
      subscription?.unsubscribe()
    }
  }

  companion object {
    val TAG = MapsActivity::class.java.simpleName
    val PICKUP_LATLNG_EXTRA = "pickup_latlng_extra"
    val DROPOFF_LATLNG_EXTRA = "dropoff_latlng_extra"
    val DROPOFF_ADDRESS_EXTRA = "dropoff_address_extra"
  }
}