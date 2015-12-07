package com.prt2121.summon

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract

/**
 * Created by pt2121 on 12/5/15.
 */
object Utils {
  fun getEmail(context: Context, contactId: Long): String {
    val email = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
        null,
        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
        null, null)
    val emailAddress = if (email.moveToNext()) {
      email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
    } else ""
    email.close()
    return emailAddress
  }

  fun verifyPermissions(grantResults: IntArray): Boolean {
    // At least one result must be checked.
    if (grantResults.size < 1) {
      return false
    }
    // Verify that each required permission has been granted, otherwise return false.
    return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
  }
}