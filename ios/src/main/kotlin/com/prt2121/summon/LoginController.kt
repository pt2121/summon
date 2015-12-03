package com.prt2121.summon

import org.robovm.apple.foundation.NSError
import org.robovm.apple.foundation.NSURL
import org.robovm.apple.foundation.NSURLRequest
import org.robovm.apple.uikit.*
import org.robovm.objc.annotation.CustomClass
import org.robovm.objc.annotation.IBOutlet

@CustomClass("LoginController")
class LoginController : UIViewController(), UIWebViewDelegate {
  @IBOutlet
  private val webView: UIWebView? = null

  override fun viewDidLoad() {
    super.viewDidLoad()

    configureWebView()
    loadAddressURL()
  }

  override fun viewWillDisappear(animated: Boolean) {
    super.viewWillDisappear(animated)

    UIApplication.getSharedApplication().isNetworkActivityIndicatorVisible = false
  }

  private fun loadAddressURL() {
    val requestURL = NSURL(Uber.LOGIN_URL)
    val request = NSURLRequest(requestURL)
    webView!!.loadRequest(request)
  }

  private fun configureWebView() {
    webView!!.backgroundColor = UIColor.white()
    webView.isScalesPageToFit = true
    webView.dataDetectorTypes = UIDataDetectorTypes.All
  }

  override fun shouldStartLoad(webView: UIWebView, request: NSURLRequest, navigationType: UIWebViewNavigationType): Boolean {
    val urlStr = request.url.absoluteString
    if (urlStr.startsWith(Uber.REDIRECT_URL)) {
      val authCode = request.url.query.split("code=").last()
      Uber.instance.auth(authCode, TokenStorage)
      return false
    } else return true
  }

  override fun didStartLoad(webView: UIWebView) {
    UIApplication.getSharedApplication().isNetworkActivityIndicatorVisible = true
  }

  override fun didFinishLoad(webView: UIWebView) {
    UIApplication.getSharedApplication().isNetworkActivityIndicatorVisible = false
  }

  override fun didFailLoad(webView: UIWebView, error: NSError?) {
    val token = TokenStorage.retrieve()
    val formatString = "<!doctype html><html><style type=\"text/css\">#center { top: 50%%; position:fixed; }</style><body><div id=\"center\" style=\"width: 100%%; text-align: center; font-size: 36pt;\">%s%s</div></body></html>"
    if (token == null) {
      // Report the error inside the web view.
      val errorMessage = "An error occurred:"
      val html = formatString.format(errorMessage, error?.localizedDescription)
      webView.loadHTML(html, null)
      UIApplication.getSharedApplication().isNetworkActivityIndicatorVisible = false
    } else {
      println("TokenStorage ${TokenStorage.retrieve()}")
      //performSegue("ResultScreen", this)
    }

  }

}

