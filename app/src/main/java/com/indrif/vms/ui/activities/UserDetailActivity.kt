package com.indrif.vms.ui.activities

import android.content.Intent
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
           val maskedId = if (userDetailsObj.idNumber.length > 4)
                maskString(userDetailsObj.idNumber, 0,userDetailsObj.idNumber.length - 4 ,'*')
            else
                maskString(userDetailsObj.idNumber, 0,userDetailsObj.idNumber.length - 1 ,'*')
            tv_user_name.text = userDetailsObj.name
            tv_mask_id.text = maskedId
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
        when (v.id) {
            R.id.iv_user_det_back -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
            R.id.iv_user_det_home -> {
                val i = Intent(applicationContext, DashBoardActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
        }

    }
    private fun maskString(strText: String, start: Int, end: Int, maskChar: Char): String {
        var startIndex = start
        var endIndex = end
        if (strText.equals(""))
            return "";

        if (startIndex < 0)
            startIndex = 0

        if (endIndex > strText.length)
            endIndex = strText.length

        if (startIndex > endIndex)
            throw  Exception("End index cannot be greater than start index");

        val maskLength = endIndex - start

        if (maskLength == 0)
            return strText;

        val sbMaskString = StringBuilder(maskLength)
        for (i in 1..maskLength) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
    }
}

