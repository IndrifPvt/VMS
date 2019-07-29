package com.indrif.vms.ui.activities


import android.content.Intent
import android.os.Bundle
import android.view.View

import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty

class CheckOutSelectionActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out_selection)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_selected_check_out_back -> {
                finish()
            }

            R.id.btn_check_out_id -> {
                val intent = Intent(this, CheckOutByIdActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }

            R.id.btn_check_out_scan -> {
                val intent = Intent(this, IdProofSelectionActivity::class.java)
                intent.putExtra("userComingBy", "checkOut")
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }
}
