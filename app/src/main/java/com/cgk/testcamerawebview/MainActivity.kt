package com.cgk.testcamerawebview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val CAMERA_REQUEST_CODE = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        WebView.setWebContentsDebuggingEnabled(true);

        web_view.settings.apply {
//            allowContentAccess = true
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
        }


        web_view.webViewClient = WebViewClient()
        web_view.webChromeClient = object : WebChromeClient() {
            // Grant permissions for cam
            override fun onPermissionRequest(request: PermissionRequest) {
                Log.d(TAG, "onPermissionRequest")
                runOnUiThread {
                    Log.d(TAG, request.origin.toString())
                    if (request.resources.first() == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                        Log.d(TAG, "GRANTED")
                        request.grant(request.resources)
                    } else {
                        Log.d(TAG, "DENIED")
                        request.deny()
                    }
                }
            }
        }

        btn_reload.setOnClickListener {
            setupWebView()
        }
    }

    private fun setupWebView() {
        Log.d(TAG, "Load page: https://bliulinstorage.z6.web.core.windows.net/")
        web_view.loadUrl("https://bliulinstorage.z6.web.core.windows.net/")
        //web_view.loadUrl("https://www.onlinemictest.com/webcam-test/")
    }


    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission missing")
            makeRequest()
        } else {
            setupWebView()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    setupWebView()
                }
            }
        }
    }
}
