package com.indrif.vms.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty

class UserProfileActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
    }

    override fun onClick(v: View) {
    }
}
