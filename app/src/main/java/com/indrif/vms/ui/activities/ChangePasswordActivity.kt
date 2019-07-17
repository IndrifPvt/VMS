package com.indrif.vms.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.utils.Validations
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_login.*

class ChangePasswordActivity : BaseActivty() {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_change_password_back -> {
                finish();
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

    }
}
