package com.prt2121.summon

import org.robovm.apple.foundation.NSUserDefaults

/**
 * Created by pt2121 on 12/1/15.
 */
object TokenStorage : ITokenStorage {
  val s = "TOKEN_KEY"

  override fun save(token: String) {
    val defaults = NSUserDefaults.getStandardUserDefaults()
    defaults.put(s, token)
  }

  override fun retrieve(): String? {
    val defaults = NSUserDefaults.getStandardUserDefaults()
    return defaults.getString(s)
  }
}