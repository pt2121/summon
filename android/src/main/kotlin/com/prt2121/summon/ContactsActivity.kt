package com.prt2121.summon

import android.Manifest.permission.READ_CONTACTS
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout

/**
 * Created by pt2121 on 12/4/15.
 */
class ContactsActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
  val rootLayout: RelativeLayout by bindView(R.id.contact_root_layout)

  override protected fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_contacts)
    if (supportFragmentManager.findFragmentByTag(ContactsFragment.TAG) == null) {
      supportFragmentManager.beginTransaction()
          .replace(R.id.fragment_container, ContactsFragment(), ContactsFragment.TAG)
          .commit()
    }
    requestPermission(READ_CONTACTS, REQUEST_CONTACT_PERMISSION, "Allow Summon to read your contacts.")
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
      REQUEST_CONTACT_PERMISSION -> {
        showRequestPermissionResult(grantResults, "You can grant Contacts permission in Settings app.")
        val f = supportFragmentManager.findFragmentByTag(ContactsFragment.TAG)
        if(f is ContactsFragment) {
          f.initLoader()
        }
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  private fun showRequestPermissionResult(grantResults: IntArray, noMessage: String, yesMessage: String = "Thanks!") {
    if (Utils.verifyPermissions(grantResults)) {
      Snackbar.make(rootLayout, yesMessage, Snackbar.LENGTH_SHORT).show()
    } else {
      Snackbar.make(rootLayout, noMessage, Snackbar.LENGTH_SHORT).show()
    }
  }

  private fun requestPermission(permission: String, code: Int, message: String) {
    if (ContextCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this@ContactsActivity, permission)) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("OK", object : View.OnClickListener {
              override fun onClick(view: View) {
                ActivityCompat.requestPermissions(this@ContactsActivity, arrayOf(permission), code)
              }
            }).show()
      } else {
        ActivityCompat.requestPermissions(this, arrayOf(permission), code)
      }
    }
  }

  companion object {
    private val REQUEST_CONTACT_PERMISSION = 1
  }
}