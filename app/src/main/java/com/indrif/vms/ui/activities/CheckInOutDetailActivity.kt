package com.indrif.vms.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty

class CheckInOutDetailActivity : BaseActivty() {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_detail_back -> {
                finish()
            }
            R.id.iv_history_detail_home -> {
                startActivity(Intent(this, DashBoardActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out) }
            }
        }

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_out_detail)
    }
}
