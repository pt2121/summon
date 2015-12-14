package com.prt2121.summon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.model.LatLng
import com.prt2121.summon.model.Estimate

/**
 * Created by pt2121 on 12/13/15.
 */
class UberEstimateActivity : AppCompatActivity(), EstimateAdapter.Listener {
  private var pickup: LatLng? = null
  private var dropOff: LatLng? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_uber_estimate)

    supportActionBar?.elevation = 0f // remove actionbar shadow

    if (intent != null) {
      pickup = intent.getParcelableExtra(PICKUP_LATLNG_EXTRA)
      dropOff = intent.getParcelableExtra(DROPOFF_LATLNG_EXTRA)
      val uberEstimateHeadFragment = UberEstimateFragment.newInstance(pickup!!, dropOff!!)
      updateFragment(uberEstimateHeadFragment, UberEstimateFragment.TAG)
    }
  }

  override fun onResume() {
    super.onResume()
  }

  protected fun updateFragment(fragment: Fragment, fragmentName: String) {
    val ft = supportFragmentManager.beginTransaction()
    ft.replace(R.id.content_estimate, fragment, fragmentName)
    ft.commitAllowingStateLoss()
  }

  override fun onRequest(imageView: View, estimate: Estimate) {

  }

  companion object {
    val PICKUP_LATLNG_EXTRA = "pickup_latLng"
    val DROPOFF_LATLNG_EXTRA = "drop_off_latLng"

    fun start(context: Context, pickupLatLng: LatLng, dropOffLatLng: LatLng) {
      val intent = Intent(context, UberEstimateActivity::class.java)
      intent.putExtra(PICKUP_LATLNG_EXTRA, pickupLatLng)
      intent.putExtra(DROPOFF_LATLNG_EXTRA, dropOffLatLng)
      context.startActivity(intent)
    }
  }
}