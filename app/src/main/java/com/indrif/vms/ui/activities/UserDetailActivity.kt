package com.indrif.vms.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : BaseActivty() {
    private var dob=ArrayList<String>()
    private var nam=ArrayList<String>()
    var name:String?=""
    var d:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        val intent = intent
        var args = intent.getBundleExtra("BUNDLE")

            dob = args.getStringArrayList("Dob")
            nam = args.getStringArrayList("Name")
            val byteArray = getIntent().getByteArrayExtra("image")
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
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
            tv_dob_value.setText(maskString(nam!!, 0, 6, '*'))
            tv_name_value.setText(name)
            iv_profile.setImageBitmap(bmp)
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


    override fun onClick(v: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
