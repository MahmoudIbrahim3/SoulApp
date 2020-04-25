package com.saul

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.saul.video.BaseActivity
import com.saul.video.CallScreenActivity
import com.saul.video.SinchService
import com.saul.video.SinchService.StartFailedListener
import com.sinch.android.rtc.SinchError
import com.sinch.android.rtc.calling.Call
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), StartFailedListener {

    private var mSpinner: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun showSpinner() {
        mSpinner = ProgressDialog(this)
        mSpinner?.setTitle("Logging in")
        mSpinner?.setMessage("Please wait...")
        mSpinner?.show()
    }

    override fun onServiceConnected() {
        sinchServiceInterface.setStartListener(this)

        val userName = "mahmoud"

        if (userName != sinchServiceInterface.userName) {
            sinchServiceInterface.stopClient()
        }

        if (!sinchServiceInterface.isStarted) {
            sinchServiceInterface.startClient(userName)
            showSpinner()
        }
    }

    private fun initViews() {
        centerImage.setOnClickListener {
            callButtonClicked()
            content.startRippleAnimation()
        }
    }

    private fun stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient()
        }
        finish()
    }

    private fun callButtonClicked() {
        val userName = "doctor"
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show()
            return
        }
        val call: Call = getSinchServiceInterface().callUserVideo(userName)
        val callId = call.callId
        val callScreen = Intent(this, CallScreenActivity::class.java)
        callScreen.putExtra(SinchService.CALL_ID, callId)
        startActivity(callScreen)
    }

    override fun onPause() {
        if (mSpinner != null) {
            mSpinner!!.dismiss()
        }
        super.onPause()
    }

    override fun onStarted() {
        if (mSpinner != null) {
            mSpinner!!.dismiss()
        }
    }

    override fun onStartFailed(error: SinchError) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        if (mSpinner != null) {
            mSpinner!!.dismiss()
        }
    }
}
