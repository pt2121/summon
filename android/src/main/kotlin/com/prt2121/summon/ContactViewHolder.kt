package com.prt2121.summon

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.prt2121.summon.model.Contact
import com.squareup.picasso.Picasso

/**
 * Created by pt2121 on 12/3/15.
 */
class ContactViewHolder(itemView: View, listener: ContactViewHolder.ClickListener) : RecyclerView.ViewHolder(itemView) {

  interface ClickListener {
    fun onItemViewClick(view: View, contact: Contact)
  }

  private val profileImageView = itemView.findViewById(R.id.profile_image_view) as ImageView
  private val titleTextView = itemView.findViewById(R.id.contact_title) as TextView
  private val numberTextView = itemView.findViewById(R.id.phone_number_text_view) as TextView
  private var contact: Contact? = null

  init {
    itemView.setOnClickListener(object : View.OnClickListener {
      override fun onClick(v: View) {
        if (contact != null) {
          listener.onItemViewClick(v, contact!!)
        }
      }
    })
  }

  fun bind(contact: Contact) {
    this.contact = contact
    titleTextView.text = contact.name
    numberTextView.text = contact.phoneNumber
    Picasso.with(itemView.context)
        .load(contact.profilePic)
        .placeholder(R.drawable.contact_placeholder)
        .error(R.drawable.contact_placeholder)
        .transform(CircleTransform())
        .into(profileImageView)
  }
}
