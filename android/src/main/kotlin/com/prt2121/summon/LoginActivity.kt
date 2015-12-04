package com.prt2121.summon

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    window.requestFeature(Window.FEATURE_PROGRESS)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val webView = findViewById(R.id.web_view) as WebView
    webView.settings.javaScriptEnabled = true

    webView.setWebChromeClient(object : WebChromeClient() {
      override fun onProgressChanged(view: WebView, progress: Int) {
        setProgress(progress * 1000)
      }
    })

    webView.setWebViewClient(UberWebViewClient())
    webView.loadUrl(Uber.LOGIN_URL)
  }

  private inner class UberWebViewClient : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
      return checkRedirect(url)
    }

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
      if (!checkRedirect(failingUrl)) {
        Toast.makeText(this@LoginActivity, description, Toast.LENGTH_SHORT).show()
      }
    }

    private fun checkRedirect(url: String): Boolean {
      if (url.startsWith(Uber.REDIRECT_URL)) {
        val uri = Uri.parse(url)
        val authCode = uri.getQueryParameter("code")
        Uber.instance.auth(authCode, TokenStorage(this@LoginActivity)) { success ->
          println("SUCCESS!!! authCode $authCode")
        }
        return true
      }
      return false
    }
  }
}
