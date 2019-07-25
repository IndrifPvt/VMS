package com.indrif.vms.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.models.User
import com.indrif.vms.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : BaseActivty() {
    lateinit var  userDetailsObj: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setInitdata()
    }

    private fun setInitdata() {
        if(getIntent().getSerializableExtra("userDetails") != null) {
            userDetailsObj = getIntent().getSerializableExtra("userDetails") as User
            CommonUtils.setImage(context, profile_image_detail, userDetailsObj.image, R.drawable.dummy_user)
            tv_mask_id.text = userDetailsObj.idNumber
            tv_id_type.text = userDetailsObj.idType
            tv_contact.text = userDetailsObj.phoneNumber
            tv_purpose.text = userDetailsObj.purpose
            tv_check_in.text = userDetailsObj.checkInTime
            tv_check_out.text = userDetailsObj.checkOutTime
            tv_block.text = userDetailsObj.block
            tv_level.text = userDetailsObj.level
            tv_unit.text = userDetailsObj.unit
            tv_remarks.text = userDetailsObj.remarks
        }
    }


    override fun onClick(v: View) {
    }
}

