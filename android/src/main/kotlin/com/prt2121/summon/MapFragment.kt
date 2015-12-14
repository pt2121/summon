package com.prt2121.summon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

/**
 * Created by pt2121 on 12/13/15.
 */
class MapFragment : SupportMapFragment() {

  private var dropOffLatLng: LatLng? = null
  private var pickupLatLng: LatLng? = null
  private var dropOffMarker: Marker? = null
  private var pickupMarker: Marker? = null

  override fun onCreateView(inflater: LayoutInflater?, viewGroup: ViewGroup?, bundle: Bundle?): View {
    val v = super.onCreateView(inflater, viewGroup, bundle)
    setUpMap()
    return v
  }

  private fun setUpMap() {
    // GoogleMap will not be available on devices without Google Play service
    if (map != null) {
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(dropOffLatLng, ZOOM))
      dropOffMarker = map.addMarker(MarkerOptions().position(dropOffLatLng).title("Drop-off location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_dark)))
      pickupMarker = map.addMarker(MarkerOptions().position(pickupLatLng).title("Pickup location").icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_pin)))
    }
  }

  fun addDestinationMarker(latLng: LatLng) {
    // GoogleMap will not be available on devices without Google Play service
    if (map != null) {
      dropOffMarker?.remove()
      dropOffMarker = map.addMarker(MarkerOptions().position(latLng).title("Drop-off location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_dark)))
      val markers = ArrayList<Marker>(2)
      markers.add(pickupMarker!!)
      markers.add(dropOffMarker!!)
      val builder = LatLngBounds.Builder()
      for (marker in markers) {
        builder.include(marker.position)
      }
      val bounds = builder.build()
      val padding = convertDpToPixel(activity, 80f).toInt() // offset from edges of the map
      val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
      map.animateCamera(cu)
    }
  }

  companion object {
    val TAG = MapFragment::class.java.simpleName
    private val ZOOM = 12f
    fun newInstance(pickupLatLng: LatLng, dropOffLatLng: LatLng): MapFragment {
      val mapFragment = MapFragment()
      mapFragment.dropOffLatLng = dropOffLatLng
      mapFragment.pickupLatLng = pickupLatLng
      return mapFragment
    }

    // TODO: move to Utils
    fun convertDpToPixel(context: Context, dp: Float): Float {
      val resources = context.resources
      val metrics = resources.displayMetrics
      val px = dp * (metrics.densityDpi / 160f)
      return px
    }
  }
}