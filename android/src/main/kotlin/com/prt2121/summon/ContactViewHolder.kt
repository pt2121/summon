package com.prt2121.summon

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.prt2121.summon.model.Contact
import com.squareup.picasso.Picasso

/**
 * Created by pt2121 on 12/3/15.
 */
class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private var image = itemView.findViewById(R.id.profile_image_view) as ImageView
  private val label = itemView.findViewById(R.id.tv_label) as TextView
  private var contact: Contact? = null

  init {
    itemView.setOnClickListener(object : View.OnClickListener {
      override fun onClick(v: View) {
        if (contact != null) {
          Toast.makeText(
              itemView.context,
              "${contact!!.name} ${contact!!.phoneNumber}",
              Toast.LENGTH_SHORT).show()
        }
      }
    })
  }

  fun bind(contact: Contact) {
    this.contact = contact
    label.text = contact.name
    Picasso.with(itemView.context)
        .load(contact.profilePic)
        .placeholder(R.drawable.contact_placeholder)
        .error(R.drawable.contact_placeholder)
        .transform(CircleTransform())
        .into(image)
  }
}
