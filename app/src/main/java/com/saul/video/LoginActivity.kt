package com.saul.video

import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.saul.AppController
import com.saul.Const
import com.saul.R
import com.saul.doctorslist.DoctorsFragment
import com.saul.video.SinchService.StartFailedListener
import com.sinch.android.rtc.SinchError
import kotlinx.android.synthetic.main.login.*

class LoginActivity : BaseActivity(), StartFailedListener {
    private var mLoginButton: Button? = null
    private var mLoginName: EditText? = null
    private var mSpinner: ProgressDialog? = null
    var userName = "mahmoud" //mLoginName.getText().toString();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLang()
        setContentView(R.layout.login)

        setUpLanguage()

        Handler().postDelayed({
            llSplash.visibility = View.GONE
        }, 1500)

        mLoginName = findViewById<View>(R.id.loginName) as EditText
        mLoginButton = findViewById<View>(R.id.loginButton) as Button
        mLoginButton!!.isEnabled = false
        //        mLoginButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginClicked();
//            }
//        });
    }

    private fun setUpLanguage() {
        if (AppController.lang == Const.AR)
            tvLanguage.setText(getString(R.string.english))
        else
            tvLanguage.setText(getString(R.string.arabic))

       tvLanguage.setOnClickListener {
           if (AppController.lang == Const.AR) {
               AppController.changeLang(Const.EN)
               AppController.lang = Const.EN
           }
           else {
               AppController.changeLang(Const.AR)
               AppController.lang = Const.AR
           }
           if(sinchServiceInterface != null)
               sinchServiceInterface.stopClient()
           startActivity(Intent(this, LoginActivity::class.java))
           finish()
       }
    }

    private fun checkLang() {
        if (AppController.lang == "") {
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales.get(0)
            } else {
                Resources.getSystem().configuration.locale
            }

            val deviceLang = locale.language

            if(deviceLang.contains(Const.AR, ignoreCase = true)) {
                AppController.lang = Const.AR
            }
            else if(deviceLang.contains(Const.EN, ignoreCase = true)) {
                AppController.lang = Const.EN
            }
            else
                AppController.lang = Const.EN
        }

        AppController.lang?.let { AppController.changeLang(it) }
    }

    override fun onServiceConnected() {
        mLoginButton!!.isEnabled = true
        sinchServiceInterface.setStartListener(this)
        if (userName != sinchServiceInterface.userName) {
            sinchServiceInterface.stopClient()
        }
        if (!sinchServiceInterface.isStarted) {
            sinchServiceInterface.startClient(userName)
            showSpinner()
        }
    }

    override fun onPause() {
        if (mSpinner != null) {
            mSpinner!!.dismiss()
        }
        super.onPause()
    }

    override fun onStartFailed(error: SinchError) {
//        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        if (mSpinner != null) {
            mSpinner!!.dismiss()
        }
    }

    override fun onStarted() { //        openPlaceCallActivity();
        mSpinner!!.dismiss()

        //        findViewById(R.id.btCovid).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loginClicked();
//            }
//        });
        findViewById<View>(R.id.btEmergency).setOnClickListener { loginClicked() }
        findViewById<View>(R.id.btSelectDoctor).setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    DoctorsFragment::class.java
                )
            )
        }
    }

    private fun loginClicked() { //        String userName = mLoginName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
            return
        }
        if (userName != sinchServiceInterface.userName) {
            sinchServiceInterface.stopClient()
        }
        if (!sinchServiceInterface.isStarted) {
            sinchServiceInterface.startClient(userName)
            showSpinner()
        } else {
            openPlaceCallActivity()
        }
    }

    private fun openPlaceCallActivity() {
        val call =
            sinchServiceInterface.callUserVideo("doctor")
        val mCallId = call.callId
        val mainActivity = Intent(this, CallScreenActivity::class.java)
        mainActivity.putExtra(SinchService.CALL_ID, mCallId)
        startActivity(mainActivity)
        //        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
//        startActivity(mainActivity);
    }

    private fun showSpinner() {
        mSpinner = ProgressDialog(this)
        mSpinner!!.setTitle("Logging in")
        mSpinner!!.setMessage("Please wait...")
        //        mSpinner.show();
    }
}