package com.prt2121.summon

import android.app.Application
import com.prt2121.summon.model.UberUser

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