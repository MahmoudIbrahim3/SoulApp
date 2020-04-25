package com.saul

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import java.util.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication


/**
 * Created by Mahmoud on 1/1/2018.
 */

class AppController : MultiDexApplication() {

//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(base)
//        MultiDex.install(this)
//    }

    override fun onCreate() {
        super.onCreate()

        mInstance = this
    }

    companion object {
        private val TAG = AppController::class.java.simpleName
        private var mInstance: AppController? = null

        fun getAppContext(): Context {
            return mInstance as Context
        }

        fun changeLang(lang: String) {

            val myLocale = Locale(lang)
            Locale.setDefault(myLocale)
            val config = getAppContext().resources.configuration

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(myLocale)
            } else {
                config.locale = myLocale
            }

            getAppContext().createConfigurationContext(config)
        }

        var lang: String?
            get() {
                val pref = PreferenceManager.getDefaultSharedPreferences(mInstance)
                return pref.getString(Const.PREF_LANG, "")
            }
            set(value) {
                val pref = PreferenceManager
                        .getDefaultSharedPreferences(mInstance)
                val editor = pref.edit()
                editor.putString(Const.PREF_LANG, value)
                editor.apply()
            }
    }
}
