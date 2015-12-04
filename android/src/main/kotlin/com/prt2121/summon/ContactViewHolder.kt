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
  private var mImage: ImageView
  private val mLabel: TextView
  private var mBoundContact: Contact? = null

  init {
    mImage = itemView.findViewById(R.id.profile_image_view) as ImageView
    mLabel = itemView.findViewById(R.id.tv_label) as TextView
    itemView.setOnClickListener(object : View.OnClickListener {
      override fun onClick(v: View) {
        if (mBoundContact != null) {
          Toast.makeText(
              itemView.context,
              "Hi, I'm " + mBoundContact!!.name,
              Toast.LENGTH_SHORT).show()
        }
      }
    })
  }

  fun bind(contact: Contact) {
    mBoundContact = contact
    mLabel.text = contact.name
    Picasso.with(itemView.context)
        .load(contact.profilePic)
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .transform(CircleTransform())
        .into(mImage)
  }
}
