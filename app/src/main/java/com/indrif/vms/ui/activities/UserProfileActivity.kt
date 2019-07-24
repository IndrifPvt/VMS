package com.indrif.vms.ui.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : BaseActivty() {
    private var dob=ArrayList<String>()
    private var id=ArrayList<String>()
    private var employer=ArrayList<String>()
    private var nam=ArrayList<String>()
    var name:String?=""
    var d:String?=null
    var selectedIdProof= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        input_layout_dob.visibility = View.INVISIBLE
        input_layout_employer.visibility = View.INVISIBLE
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        var intent = intent
        var args = intent.getBundleExtra("BUNDLE")
        //    if(args.getString("userComingFrom").equals("IdProofSelectionActivity")){

        //     }else {
        selectedIdProof = args.getString("selectedIdProof")
        nam = args.getStringArrayList("Name")
        id = args.getStringArrayList("ID")
        val byteArray = getIntent().getByteArrayExtra("image")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        et_id_type.setText(selectedIdProof)
        profile_image.setImageBitmap(bmp)
        for (index in nam.indices) {
            name = name + "" + nam.get(index)

        }
        tv_user_name.setText((name!!.split(" "))[0])
        et_name.setText(name)
            if(selectedIdProof == "NRIC")
            {
                for (index in id.indices) {
                    d = id.get(index)
                    d = d + " "
                }
                val separated = d!!.split(".")
                separated[0]
                separated[1]
                var nam = separated[1]
                //  name.replace(0,(stringLength?.minus(4)))
                et_id_no.setText(maskString(nam!!, 0, 6, '*'))
                dob = args.getStringArrayList("DOB")
                var dateofbirth = dob.get(0)
                input_layout_dob.visibility = View.VISIBLE
                et_id_dob.setText(dateofbirth)
            }
            else if(selectedIdProof == "S-PASS")
            {
                et_id_no.setText(maskString(id.get(0)!!, 0, 6, '*'))
                employer = args.getStringArrayList("Employer")
                input_layout_employer.visibility = View.VISIBLE
                et_id_employer.setText(employer.get(0))
            }








    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_back -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
            R.id.btn_next_purpose -> {
                startActivity(Intent(applicationContext, UnderDevelopment::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
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
