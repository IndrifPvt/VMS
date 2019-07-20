package com.indrif.vms.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_under_development.*

class UnderDevelopment : BaseActivty() {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back_btn -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_under_development)
        Glide.with(applicationContext)
            .load(R.drawable.appdevelopment)
            .into(iv_load_gif)
    }
}
