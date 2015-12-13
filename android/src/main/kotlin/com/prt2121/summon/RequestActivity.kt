package com.prt2121.summon

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.SEND_SMS
import android.Manifest.permission.READ_SMS
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.prt2121.summon.location.UserLocation
import com.squareup.picasso.Picasso
import java.util.*

/**
 * Created by pt2121 on 12/5/15.
 */
class RequestActivity : AppCompatActivity(),
    AppBarLayout.OnOffsetChangedListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    OnMapReadyCallback {

  val titleContainer: RelativeLayout by bindView(R.id.request_Layout)
  val title: TextView by bindView(R.id.request_toolbar_title)
  val addressTextView: TextView by bindView(R.id.request_address)
  val subtitle: TextView by bindView(R.id.request_subtitle)
  val appBarLayout: AppBarLayout by bindView(R.id.request_appBarLayout)
  val imageView: MapView by bindView(R.id.request_mapImageView)
  val frameLayout: FrameLayout by bindView(R.id.request_frameLayout)
  val toolbar: Toolbar by bindView(R.id.main_toolbar)
  val rootLayout: LinearLayout by bindView(R.id.request_root_layout)
  val messageEditText: EditText by bindView(R.id.message_editText)

  private var myNumber: String = ""
  private var phoneNumber: String? = null
  private var pictureUri: Uri? = null
  private var titleVisible = false
  private var titleContainerVisible = true
  private var googleMap: GoogleMap? = null
  private var latLng: LatLng? = null
  private var name: String? = null

  override public fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_request)
    bindActivity()
    toolbar.title = ""
    appBarLayout.addOnOffsetChangedListener(this)
    setSupportActionBar(toolbar)
    startAlphaAnimation(title, 0, View.INVISIBLE)
    initParallaxValues()
    requestPermission(ACCESS_FINE_LOCATION, REQUEST_LOCATION_PERMISSION, "Allow Summon to get your current location.") {
      val loc = UserLocation(this).lastBestLocation(30 * 60 * 1000) // 30 minutes
      if (loc != null) {
        latLng = LatLng(loc.latitude, loc.longitude)
        addressTextView.text = getAddressString(latLng!!)
      }
    }

    imageView.onCreate(null)
    imageView.getMapAsync(this)

    requestPermission(READ_SMS, REQUEST_PHONE_NUMBER_PERMISSION, "Allow Summon to get your phone number.") {
      val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
      myNumber = telephonyManager.line1Number
    }
  }

  private fun sendSms(phoneNumber: String, message: String) {
    requestPermission(SEND_SMS, REQUEST_SMS_PERMISSION, "Allow Summon to send SMS.") {
      val names = when {
        name.isNullOrBlank() -> "" to ""
        " " in name!! -> {
          val ns = name!!.split(" ")
          ns[0] to ns[1]
        }
        else -> "" to ""
      }
      println("fromUser ${SummonApp.app!!.user!!.firstName}, ${SummonApp.app!!.user!!.lastName}, ${myNumber}")
      val fromUser = User(SummonApp.app!!.user!!.firstName, SummonApp.app!!.user!!.lastName, myNumber)
      val toUser = User(names.first, names.second, phoneNumber)
      val invite = Invite(null,
          fromUser,
          toUser,
          "${latLng?.latitude}, ${latLng?.longitude}",
          addressTextView.text.toString(),
          message)

      InviteApi.instance.invite(invite) { invite ->
        println(invite.toString())
        val m = "$message http://prt2121.github.io/invite-app/index.html?id=${invite?._id}"
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, m, null, null)
      }
    }
  }

  private fun bindActivity() {
    val profileImageView = findViewById(R.id.request_profile_imageView) as ImageView
    messageEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
      override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if ((event?.action == KeyEvent.ACTION_DOWN) && (event?.keyCode == KeyEvent.KEYCODE_ENTER)) {
          sendSms(phoneNumber!!, v!!.text.toString())
          return true
        }
        if (actionId == EditorInfo.IME_ACTION_SEND) {
          sendSms(phoneNumber!!, v!!.text.toString())
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
    name = if (nameExtra is String) nameExtra else ""
    phoneNumber = if (numExtra is String) numExtra else ""
    title.text = name
    subtitle.text = "Bring $name to"

    Picasso.with(this)
        .load(pictureUri)
        .placeholder(R.drawable.contact_placeholder)
        .error(R.drawable.contact_placeholder)
        .transform(CircleTransform())
        .into(profileImageView)
  }

  private fun requestPermission(permission: String, code: Int, message: String, successAction: () -> Unit) {
    if (ContextCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this@RequestActivity, permission)) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("OK", object : View.OnClickListener {
              override fun onClick(view: View) {
                ActivityCompat.requestPermissions(this@RequestActivity, arrayOf(permission), code)
              }
            }).show()
      } else {
        ActivityCompat.requestPermissions(this, arrayOf(permission), code)
      }
    } else {
      successAction()
    }
  }

  private fun getAddressString(latLng: LatLng): String {
    val geocoder = Geocoder(this, Locale.getDefault())
    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
    if (addresses.isNotEmpty()) {
      val maxLine = addresses[0].maxAddressLineIndex - 1
      var addressStr = ""
      for (i in 0..maxLine) {
        addressStr = addressStr.plus(" " + addresses[0].getAddressLine(i))
      }
      return addressStr.trim()
    }
    return "Your location"
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
      REQUEST_LOCATION_PERMISSION -> {
        showRequestPermissionResult(grantResults, "You can grant Location permission in Settings app.")
        if (Utils.verifyPermissions(grantResults)) {
          val loc = UserLocation(this).lastBestLocation(30 * 60 * 1000) // 30 minutes
          if (loc != null) {
            latLng = LatLng(loc.latitude, loc.longitude)
            addressTextView.text = getAddressString(latLng!!)
          }
        }
      }
      REQUEST_SMS_PERMISSION -> {
        showRequestPermissionResult(grantResults, "You can grant SMS permission in Settings app.")
        if (Utils.verifyPermissions(grantResults)) {
          val sms = SmsManager.getDefault()
          sms.sendTextMessage(phoneNumber, null, messageEditText.text.toString(), null, null)
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

  override fun onMapReady(googleMap: GoogleMap) {
    this.googleMap = googleMap
    MapsInitializer.initialize(this)
    googleMap.uiSettings.isMapToolbarEnabled = false
    if (latLng != null) {
      updateMapContents(latLng!!)
    }
  }

  protected fun updateMapContents(latLng: LatLng) {
    googleMap?.clear()
    googleMap?.addMarker(MarkerOptions().position(latLng))
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
    googleMap?.moveCamera(cameraUpdate)
  }

  private fun initParallaxValues() {
    val detailsParams = imageView.layoutParams as CollapsingToolbarLayout.LayoutParams
    val backgroundParams = frameLayout.layoutParams as CollapsingToolbarLayout.LayoutParams
    detailsParams.parallaxMultiplier = 0.9f
    backgroundParams.parallaxMultiplier = 0.3f
    imageView.layoutParams = detailsParams
    frameLayout.layoutParams = backgroundParams
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
        startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
        titleVisible = true
      }
    } else {
      if (titleVisible) {
        startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
        titleVisible = false
      }
    }
  }

  private fun handleAlphaOnTitle(percentage: Float) {
    if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
      if (titleContainerVisible) {
        startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION.toLong(), View.INVISIBLE)
        titleContainerVisible = false
      }
    } else {
      if (!titleContainerVisible) {
        startAlphaAnimation(titleContainer, ALPHA_ANIMATIONS_DURATION.toLong(), View.VISIBLE)
        titleContainerVisible = true
      }
    }
  }

  companion object {
    private val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
    private val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f
    private val ALPHA_ANIMATIONS_DURATION = 200
    private val REQUEST_LOCATION_PERMISSION = 0
    private val REQUEST_SMS_PERMISSION = 1
    private val REQUEST_PHONE_NUMBER_PERMISSION = 2
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