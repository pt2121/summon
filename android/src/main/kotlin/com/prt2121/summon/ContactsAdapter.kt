package com.prt2121.summon

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.prt2121.summon.model.Contact

/**
 * Created by pt2121 on 12/3/15.
 */
class ContactsAdapter(private val cursor: Cursor) : RecyclerView.Adapter<ContactViewHolder>() {

  private val nameColumnIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
  private val idColumnIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts._ID)
  private val hasPhoneNumberIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
  private var context: Context? = null

  override fun onCreateViewHolder(parent: ViewGroup, pos: Int): ContactViewHolder {
    context = parent.context
    val listItemView = LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false)

    return ContactViewHolder(listItemView)
  }

  override fun onBindViewHolder(contactViewHolder: ContactViewHolder, pos: Int) {
    cursor.moveToPosition(pos)
    val contactName = cursor.getString(nameColumnIndex)
    val contactId = cursor.getLong(idColumnIndex)

    val hasPhone = if (cursor.getString(hasPhoneNumberIndex) == "1") true else false
    val phoneNumber = if (hasPhone && context != null) {
      getPhoneNumber(contactId)
    } else ""

    println("phoneNumber " + phoneNumber)
    val c = Contact(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId), contactName)
    contactViewHolder.bind(c)
  }

  private fun getPhoneNumber(contactId: Long): String? {
    val phones = context!!.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
        null, null)
    val phoneNumber = if (phones.moveToNext()) {
      phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
    } else ""
    phones.close()
    return phoneNumber
  }

  override fun getItemCount(): Int {
    return cursor.count
  }
}