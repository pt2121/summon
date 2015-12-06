package com.prt2121.summon

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.squareup.picasso.Picasso

/**
 * Created by pt2121 on 12/5/15.
 */
class RequestActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener {
  private var titleVisible = false
  private var titleContainerVisible = true
  private var titleContainer: RelativeLayout? = null
  private var title: TextView? = null
  private var subtitle: TextView? = null
  private var appBarLayout: AppBarLayout? = null
  private var imageView: ImageView? = null
  private var frameLayout: FrameLayout? = null
  private var toolbar: Toolbar? = null
  private var phoneNumber: String? = null
  private var pictureUri: Uri? = null

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
    subtitle = findViewById(R.id.request_subtitle) as TextView
    titleContainer = findViewById(R.id.request_Layout) as RelativeLayout
    appBarLayout = findViewById(R.id.request_appBarLayout) as AppBarLayout
    imageView = findViewById(R.id.request_mapImageView) as ImageView
    val profileImageView = findViewById(R.id.request_profile_imageView) as ImageView
    frameLayout = findViewById(R.id.request_frameLayout) as FrameLayout
    val messageEditText = findViewById(R.id.message_editText) as EditText
    messageEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if ((event?.action == KeyEvent.ACTION_DOWN) && (event?.keyCode == KeyEvent.KEYCODE_ENTER)) {
          println("enter!")
          return true
        }
        if (actionId == EditorInfo.IME_ACTION_SEND) {
          println("send!")
          return true
        }
        return false
      }

    })

    val screenSize = Point()
    windowManager.defaultDisplay.getSize(screenSize)
    var px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150.toFloat(), resources.displayMetrics)
    findViewById(R.id.request_collapsingToolBarLayout).layoutParams.height = (screenSize.y / 2 + px).toInt()

    val extras: Bundle? = intent.extras
    val nameExtra = extras?.get(NAME_EXTRA)
    val numExtra = extras?.get(PHONE_NUMBER_EXTRA)
    pictureUri = extras?.get(PICTURE_URI_EXTRA) as Uri
    val name = if (nameExtra is String) nameExtra else ""
    phoneNumber = if (numExtra is String) numExtra else ""
    subtitle!!.text = "Bring $name to"

    Picasso.with(this)
        .load(pictureUri)
        .placeholder(R.drawable.contact_placeholder)
        .error(R.drawable.contact_placeholder)
        .transform(CircleTransform())
        .into(profileImageView)
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
    val NAME_EXTRA = "name_extra"
    val PHONE_NUMBER_EXTRA = "phone_number_extra"
    val PICTURE_URI_EXTRA = "picture_uri_extra"
    fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
      val alphaAnimation = if ((visibility == View.VISIBLE)) AlphaAnimation(0f, 1f)
      else AlphaAnimation(1f, 0f)
      alphaAnimation.duration = duration
      alphaAnimation.fillAfter = true
      v.startAnimation(alphaAnimation)
    }
  }
}