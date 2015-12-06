package com.prt2121.summon

import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by pt2121 on 12/5/15.
 */
class RequestActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {
  private var titleVisible = false
  private var titleContainerVisible = true
  private var titleContainer: LinearLayout? = null
  private var title: TextView? = null
  private var appBarLayout: AppBarLayout? = null
  private var imageView: ImageView? = null
  private var frameLayout: FrameLayout? = null
  private var toolbar: Toolbar? = null

  override public fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_request)
    bindActivity()
    toolbar!!.title = ""
    appBarLayout!!.addOnOffsetChangedListener(this)
    setSupportActionBar(toolbar)
    startAlphaAnimation(title!!, 0, View.INVISIBLE)
    initParallaxValues()
  }

  private fun bindActivity() {
    toolbar = findViewById(R.id.main_toolbar) as Toolbar
    title = findViewById(R.id.request_toolbar_title) as TextView
    titleContainer = findViewById(R.id.request_linearLayout) as LinearLayout
    appBarLayout = findViewById(R.id.request_appBarLayout) as AppBarLayout
    imageView = findViewById(R.id.request_mapImageView) as ImageView
    frameLayout = findViewById(R.id.request_frameLayout) as FrameLayout

    val screenSize = Point()
    windowManager.defaultDisplay.getSize(screenSize)
    var px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150.toFloat(), resources.displayMetrics)
    findViewById(R.id.request_collapsingToolBarLayout).layoutParams.height = (screenSize.y / 2 + px).toInt()
  }

  private fun initParallaxValues() {
    val detailsParams = imageView!!.layoutParams as CollapsingToolbarLayout.LayoutParams
    val backgroundParams = frameLayout!!.layoutParams as CollapsingToolbarLayout.LayoutParams
    detailsParams.parallaxMultiplier = 0.9f
    backgroundParams.parallaxMultiplier = 0.3f
    imageView!!.layoutParams = detailsParams
    frameLayout!!.layoutParams = backgroundParams
  }

  override fun onOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {
    val maxScroll = appBarLayout.totalScrollRange
    val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()
    handleAlphaOnTitle(percentage)
    handleToolbarTitleVisibility(percentage)
  }

  private fun handleToolbarTitleVisibility(percentage: Float) {
    if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
      if (!titleVisible) {
        startAlphaAnimation(title!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
        titleVisible = true
      }
    } else {
      if (titleVisible) {
        startAlphaAnimation(title!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
        titleVisible = false
      }
    }
  }

  private fun handleAlphaOnTitle(percentage: Float) {
    if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
      if (titleContainerVisible) {
        startAlphaAnimation(titleContainer!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
        titleContainerVisible = false
      }
    } else {
      if (!titleContainerVisible) {
        startAlphaAnimation(titleContainer!!, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
        titleContainerVisible = true
      }
    }
  }

  companion object {
    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val ALPHA_ANIMATIONS_DURATION = 200
    fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
      val alphaAnimation = if ((visibility == View.VISIBLE)) AlphaAnimation(0f, 1f)
      else AlphaAnimation(1f, 0f)
      alphaAnimation.duration = duration
      alphaAnimation.fillAfter = true
      v.startAnimation(alphaAnimation)
    }
  }
}