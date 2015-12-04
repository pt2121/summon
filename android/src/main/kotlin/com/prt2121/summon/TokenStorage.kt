package com.prt2121.summon

import android.preference.PreferenceManager

/**
 * Created by pt2121 on 12/3/15.
 */
class TokenStorage(val context: android.content.Context) : ITokenStorage {
  val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
  val s = "TOKEN_KEY"

  override fun save(token: String) {
    val editor = sharedPref.edit()
    editor.putString(s, token)
    editor.commit()
  }

  override fun retrieve(): String? {
    return sharedPref.getString(s, "")
  }
}