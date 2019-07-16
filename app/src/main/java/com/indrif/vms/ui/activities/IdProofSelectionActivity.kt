package com.indrif.vms.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty


class IdProofSelectionActivity : BaseActivty() {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_scan_id -> {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_proof_selection)
    }
}
