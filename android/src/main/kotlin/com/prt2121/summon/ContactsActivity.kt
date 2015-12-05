package com.prt2121.summon

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by pt2121 on 12/4/15.
 */
class ContactsActivity : AppCompatActivity() {
  override protected fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_contacts)
    if (supportFragmentManager.findFragmentByTag(ContactsFragment.TAG) == null) {
      supportFragmentManager.beginTransaction()
          .replace(R.id.fragment_container, ContactsFragment(), ContactsFragment.TAG)
          .commit()
    }
  }
}