package com.indrif.vms.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.indrif.vms.data.prefs.PreferenceHandler
import kotlinx.android.synthetic.main.activity_splash.*
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty


class SplashActivity : BaseActivty() {

    private val SPLASHDELAY: Long = 2500
    private var mDelayHandler: Handler? = null

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            if (PreferenceHandler.readBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, false)) {
                 if(PreferenceHandler.readBoolean(applicationContext, PreferenceHandler.IS_SITE_SELECTED, false)){
                     startActivity(Intent(applicationContext, DashBoardActivity::class.java))
                     finish()
                     overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                 }
                else {
                     startActivity(Intent(applicationContext, SiteSelectionActivity::class.java))
                     finish()
                     overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                 }
            } else {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setScaleAnimation(iv_app_logo)

    }
    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            return
        }
        setScaleAnimation(iv_app_logo)
        super.onWindowFocusChanged(hasFocus)
    }*/

    override fun onResume() {
        super.onResume()
        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, SPLASHDELAY)
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    private fun setScaleAnimation(view: View) {
        val anim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.duration = 900
        view.startAnimation(anim)
    }

    override fun onClick(v: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

