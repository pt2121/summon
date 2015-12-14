package com.prt2121.summon

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.prt2121.summon.model.Estimate
import java.util.concurrent.TimeUnit

/**
 * Created by pt2121 on 12/13/15.
 */
class EstimateAdapter(val estimates: List<Estimate>, val listener: EstimateAdapter.Listener) : RecyclerView.Adapter<EstimateAdapter.ViewHolder>() {

  override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
    val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_estimate, viewGroup, false)
    return ViewHolder(v)
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
    val estimate = estimates[i]
    viewHolder.nameTextView.text = estimate.displayName

    val icon = Utils.findIcon(estimate.displayName)

    viewHolder.vehicleImageView.setImageResource(icon)
    viewHolder.costTextView.text = estimate.priceEstimate
    val seconds = estimate.timeEstimate
    val minute = TimeUnit.SECONDS.toMinutes(seconds.toLong()) - (TimeUnit.SECONDS.toHours(seconds.toLong()) * 60)
    val second = TimeUnit.SECONDS.toSeconds(seconds.toLong()) - (TimeUnit.SECONDS.toMinutes(seconds.toLong()) * 60)
    viewHolder.timeTextView.text = "in %2d:%02d mins".format(minute, second)
    viewHolder.requestButton.setOnClickListener(object : View.OnClickListener {
      override fun onClick(v: View) {
        Log.d(TAG, estimate.displayName)
        listener.onRequest(viewHolder.vehicleImageView, estimate)
      }
    })
  }

  override fun getItemCount(): Int {
    return estimates.size
  }

  interface Listener {
    fun onRequest(imageView: View, estimate: Estimate)
  }

  class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val vehicleImageView: ImageView = v.findViewById(R.id.vehicleImageView) as ImageView
    val nameTextView: TextView = v.findViewById(R.id.nameTextView) as TextView
    val costTextView: TextView = v.findViewById(R.id.costTextView) as TextView
    val timeTextView: TextView = v.findViewById(R.id.timeTextView) as TextView
    val requestButton: Button = v.findViewById(R.id.requestButton) as Button
  }

  companion object {
    val TAG = EstimateAdapter::class.java.simpleName
  }
}