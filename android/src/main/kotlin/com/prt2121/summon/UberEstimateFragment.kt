package com.prt2121.summon

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.prt2121.summon.model.Estimate
import com.prt2121.summon.model.PriceEstimateList
import com.prt2121.summon.model.TimeEstimateList
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by pt2121 on 12/13/15.
 */
class UberEstimateFragment : Fragment() {

  private var recyclerView: RecyclerView? = null
  private var adapter: RecyclerView.Adapter<EstimateAdapter.ViewHolder>? = null
  private var recyclerLayoutManager: RecyclerView.LayoutManager? = null
  private var subscription: Subscription? = null
  private var pickup: LatLng? = null
  private var destination: LatLng? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (arguments != null) {
      pickup = arguments.getParcelable(ARG_PICKUP)
      destination = arguments.getParcelable(ARG_DESTINATION)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater!!.inflate(R.layout.fragment_uber_estimate, container, false)
    recyclerLayoutManager = LinearLayoutManager(activity)
    recyclerView = view.findViewById(R.id.estimate_recycler_view) as RecyclerView
    recyclerView!!.setHasFixedSize(true)
    recyclerView!!.layoutManager = recyclerLayoutManager
    recyclerView!!.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
    return view
  }

  override fun onResume() {
    super.onResume()
    val token = TokenStorage(activity).retrieve()
    val timeEstimateObservable = Uber.instance.api.timeEstimates(token!!, pickup!!.latitude, pickup!!.longitude)
    val priceEstimateListObservable = Uber.instance.api.priceEstimates(token,
        pickup!!.latitude,
        pickup!!.longitude,
        destination!!.latitude,
        destination!!.longitude)
    subscription = Observable.zip(timeEstimateObservable, priceEstimateListObservable) { timeEstimateList: TimeEstimateList, priceEstimateList: PriceEstimateList ->
      getEstimates(timeEstimateList, priceEstimateList)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          setEstimate(it, activity as EstimateAdapter.Listener)
        }, {
          Log.e(TAG, it.message)
        })
  }

  fun setEstimate(estimates: List<Estimate>, listener: EstimateAdapter.Listener) {
    adapter = EstimateAdapter(estimates, listener)
    recyclerView?.adapter = adapter
  }

  override fun onDestroy() {
    super.onDestroy()
    if (subscription != null && !subscription!!.isUnsubscribed) {
      subscription?.unsubscribe()
    }
  }

  private fun getEstimates(timeEstimateList: TimeEstimateList, priceEstimateList: PriceEstimateList): List<Estimate> {
    val prices = priceEstimateList.prices
    val times = timeEstimateList.times
    return prices
        .filter { it.currency_code != null }
        .map { price ->
          val time = times.find { time ->
            time.product_id == price.product_id
          }
          val t = if (time != null) time.estimate else -1
          Estimate(price.product_id, price.currency_code!!, price.localized_display_name, price.display_name, price.estimate, price.low_estimate, price.high_estimate, price.surge_multiplier, t)
        }
        .filter { it.timeEstimate >= 0 }
  }

  companion object {
    val TAG = UberEstimateFragment::class.java.simpleName
    val ARG_DESTINATION = "destination"
    val ARG_PICKUP = "pickup"
    fun newInstance(pickup: LatLng, destination: LatLng): UberEstimateFragment {
      val fragment = UberEstimateFragment()
      val args = Bundle()
      args.putParcelable(ARG_PICKUP, pickup)
      args.putParcelable(ARG_DESTINATION, destination)
      fragment.arguments = args
      return fragment
    }
  }
}