package com.prt2121.summon

import org.robovm.apple.contacts.CNContact
import org.robovm.apple.corelocation.CLGeocoder
import org.robovm.apple.corelocation.CLLocationManager
import org.robovm.apple.corelocation.CLPlacemark
import org.robovm.apple.dispatch.Dispatch
import org.robovm.apple.foundation.Foundation
import org.robovm.apple.foundation.NSArray
import org.robovm.apple.foundation.NSCoder
import org.robovm.apple.foundation.NSError
import org.robovm.apple.mapkit.MKCoordinateRegion
import org.robovm.apple.mapkit.MKMapView
import org.robovm.apple.mapkit.MKMapViewDelegateAdapter
import org.robovm.apple.mapkit.MKUserLocation
import org.robovm.apple.uikit.NSLineBreakMode
import org.robovm.apple.uikit.UILabel
import org.robovm.apple.uikit.UIViewController
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet
import org.robovm.objc.block.VoidBlock2

/**
 * Created by pt2121 on 11/28/15.
 */
@CustomClass("DetailViewController")
class DetailViewController : UIViewController() {

  @IBOutlet
  private val mapView: MKMapView? = null

  @IBOutlet
  private val bringLabel: UILabel? = null

  @IBOutlet
  private val addressLabel: UILabel? = null

  private var contact: CNContact? = null
  private var geocoder: CLGeocoder? = null
  private var placemark: CLPlacemark? = null

  override fun viewDidLoad() {
    geocoder = CLGeocoder()

    if (Foundation.getMajorSystemVersion() >= 8) {
      val locationManager = CLLocationManager()
      locationManager.requestWhenInUseAuthorization()
    }

    mapView!!.delegate = object : MKMapViewDelegateAdapter() {
      override fun didUpdateUserLocation(mapView: MKMapView?, userLocation: MKUserLocation?) {
        // Center the map the first time we get a real location change.
        if ((userLocation!!.coordinate.latitude != 0.0) && (userLocation.coordinate.longitude != 0.0)) {
          Dispatch.once(object : Runnable {
            override fun run() {
              mapView!!.setCenterCoordinate(userLocation.coordinate, true)
              val userRegion = MKCoordinateRegion(userLocation.coordinate, 1000.0, 1000.0)
              mapView.region = userRegion
            }
          })
        }

        // Lookup the information for the current location of the user.
        geocoder!!.reverseGeocodeLocation(mapView!!.userLocation.location,
            object : VoidBlock2<NSArray<CLPlacemark>, NSError> {
              override fun invoke(placemarks: NSArray<CLPlacemark>?, error: NSError?) {
                if (placemarks != null && placemarks.isNotEmpty()) {
                  // If the placemark is not null then we have
                  // at least one placemark. Typically there
                  // will only be
                  // one.
                  placemark = placemarks[0]
                  val coor = placemark!!.location.coordinate
                  println("lat ${coor.latitude}, long ${coor.longitude}")
                  println("address ${placemark!!.address.toString()}")
                  addressLabel!!.lineBreakMode = NSLineBreakMode.WordWrapping
                  addressLabel.numberOfLines = 0
                  addressLabel.text =
                      "${placemark!!.address.street}, " +
                          "${placemark!!.address.city}, " +
                          "${placemark!!.address.state} " +
                          "${placemark!!.address.zip}"
                } else {
                  // Handle the null case if necessary.
                }
              }
            })
      }
    }
  }

  override fun viewWillAppear(animated: Boolean) {
    super.viewWillAppear(animated)
    title = "${contact!!.givenName} ${contact!!.familyName}"
    bringLabel!!.text = "Bring ${contact!!.givenName} to:"
    // if (contact!!.emailAddresses.isNotEmpty()) contact!!.emailAddresses[0].value else ""
    // contact!!.phoneNumbers[0].value.stringValue
  }

  fun setContact(contact: CNContact) {
    this.contact = contact
  }

  override fun encodeRestorableState(coder: NSCoder) {
    super.encodeRestorableState(coder)

    // encode the product
    coder.encodeObject(VIEW_CONTROLLER_CONTACT_KEY, contact)
  }

  override fun decodeRestorableState(coder: NSCoder) {
    super.decodeRestorableState(coder)

    // restore the contact
    contact = coder.decodeObject(VIEW_CONTROLLER_CONTACT_KEY, CNContact::class.java)
  }

  companion object {
    private val VIEW_CONTROLLER_CONTACT_KEY = "ViewControllerContactKey"
  }
}