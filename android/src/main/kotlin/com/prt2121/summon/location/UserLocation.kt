package com.prt2121.summon.location

import android.content.Context
import android.location.Location
import android.location.LocationManager

/**
 * Created by pt2121 on 12/6/15.
 */
class UserLocation(context: Context) {
  private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

  /**
   * Returns the most accurate and timely previously detected location.
   *
   * @param maxTime Maximum time before the location is outdated.
   * @return The most accurate and / or timely previously detected location within the {@code
   * maxTime} period.
   */
  fun lastBestLocation(maxTime: Long): Location? {
    val minTime = System.currentTimeMillis() - maxTime
    var bestResult: Location? = null
    var bestAccuracy = Float.MAX_VALUE
    var bestTime = Long.MIN_VALUE

    locationManager.allProviders.forEach {
      val location = locationManager.getLastKnownLocation(it)
      if (location != null) {
        val accuracy = location.accuracy
        val time = location.time
        if ((time > minTime && accuracy < bestAccuracy)) {
          bestResult = location
          bestAccuracy = accuracy
          bestTime = time
        } else if (time < minTime && bestAccuracy == Float.MAX_VALUE && time > bestTime) {
          bestResult = location
          bestTime = time
        }
      }
    }
    return bestResult
  }

}