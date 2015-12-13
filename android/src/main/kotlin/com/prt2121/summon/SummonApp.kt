package com.prt2121.summon

import android.app.Application

/**
 * Created by pt2121 on 12/13/15.
 */
class SummonApp : Application() {

  var user: UberUser? = null

  override fun onCreate() {
    super.onCreate()
    app = this
  }

  companion object {
    var app: SummonApp? = null
  }
}