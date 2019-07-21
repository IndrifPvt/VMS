package com.indrif.vms.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivty() {
    private var dob=ArrayList<String>()
    private var Dob=ArrayList<String>()
    private var nam=ArrayList<String>()
    var name:String?=""
    var d:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        var intent = intent
        var args = intent.getBundleExtra("BUNDLE")
        //    if(args.getString("userComingFrom").equals("IdProofSelectionActivity")){

        //     }else {
        dob = args.getStringArrayList("Dob")
        nam = args.getStringArrayList("Name")
        Dob = args.getStringArrayList("DOB")
        val byteArray = getIntent().getByteArrayExtra("image")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        var dateofbirth = Dob.get(0)
        for (index in dob.indices) {
            d = dob.get(index)
            d = d + " "
        }
        for (index in nam.indices) {
            name = name + "" + nam.get(index)

        }
        val separated = d!!.split(".")
        separated[0]
        separated[1]
        var nam = separated[1]
        //  name.replace(0,(stringLength?.minus(4)))
        et_id_no.setText(maskString(nam!!, 0, 6, '*'))
        et_name.setText(name)
        et_id_dob.setText(dateofbirth)
        profile_image.setImageBitmap(bmp)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_back -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
        }
    }

    private fun maskString( strText:String, start:Int, end:Int, maskChar:Char):String
    {
        var startIndex = start
        var endIndex = end
        if(strText == null || strText.equals(""))
            return "";

        if(startIndex < 0)
            startIndex = 0

        if( endIndex > strText.length )
            endIndex = strText.length

        if(startIndex > endIndex)
            throw  Exception("End index cannot be greater than start index");

        var maskLength = endIndex - start

        if(maskLength == 0)
            return strText;

        var sbMaskString =  StringBuilder(maskLength)
        for(i in 1..maskLength) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)+ sbMaskString.toString()+ strText.substring(start + maskLength);
    }
}
