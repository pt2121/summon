package com.prt2121.summon

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng

/**
 * Created by pt2121 on 12/13/15.
 */
class RequestDetailActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_request_detail)
    // remove actionbar shadow
    supportActionBar?.elevation = 0f
    if (intent != null) {
      val productName = intent.getStringExtra(PRODUCT_NAME)
      val productId = intent.getStringExtra(PRODUCT_ID)
      val start = intent.getParcelableExtra<LatLng>(START_LOCATION)
      val destination = intent.getParcelableExtra<LatLng>(DESTINATION_LOCATION)
      val f = RequestDetailActivityFragment.newInstance(start, destination, productName, productId)
      title = productName
      updateFragment(f, RequestDetailActivityFragment.TAG)
    }
  }

  protected fun updateFragment(fragment: Fragment, fragmentName: String) {
    val ft = supportFragmentManager.beginTransaction()
    ft.replace(R.id.content_detail, fragment, fragmentName)
    ft.commitAllowingStateLoss()
  }

  companion object {
    val PRODUCT_NAME = "product_name"
    val PRODUCT_ID = "product_id"
    val START_LOCATION = "start_location"
    val DESTINATION_LOCATION = "destination_location"
  }

}