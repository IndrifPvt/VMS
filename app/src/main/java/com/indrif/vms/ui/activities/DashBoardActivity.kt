package com.indrif.vms.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import kotlinx.android.synthetic.main.activity_selected_site.*

class DashBoardActivity : BaseActivty() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_site)
        inItData()
    }
    private fun inItData(){
        tv_site_name.text = PreferenceHandler.readString(applicationContext, PreferenceHandler.SELECTED_SITE, "")
    }

    override fun onClick(v: View) {

    }
}
