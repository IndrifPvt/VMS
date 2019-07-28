package com.indrif.vms.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import kotlinx.android.synthetic.main.activity_id_proof_selection.*
import kotlinx.android.synthetic.main.activity_select_purpose.*
import okhttp3.MediaType
import okhttp3.RequestBody

class SelectPurposeActivity : BaseActivty() {
   var selectedPurpose =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_purpose)
        inItData()
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_sign_in -> {
               checkInUser()
            }
        }
    }

    private fun checkInUser() {
        var checkInCheckOutHashMap = intent.getSerializableExtra("checkInCheckOutHashMap") as (HashMap<String, RequestBody>)
        checkInCheckOutHashMap.put("purpose", RequestBody.create(MediaType.parse("text/plain"), selectedPurpose.trim()))
        checkInCheckOutHashMap.put("phoneNumber", RequestBody.create(MediaType.parse("text/plain"), et_contact_no.text.toString().trim()))
        checkInCheckOutHashMap.put("remarks", RequestBody.create(MediaType.parse("text/plain"), et_remarks.text.toString().trim()))
        checkInCheckOutHashMap.put("unit", RequestBody.create(MediaType.parse("text/plain"), tv_select_unit_value.text.toString().trim()))
        checkInCheckOutHashMap.put("level", RequestBody.create(MediaType.parse("text/plain"), tv_select_level_value.text.toString().trim()))
        checkInCheckOutHashMap.put("block", RequestBody.create(MediaType.parse("text/plain"), tv_select_block_value.text.toString().trim()))

    }

    private fun inItData() {
        setAdapter()
    }

    private fun setAdapter() {
        val idProofArray =resources.getStringArray(R.array.purpose_array)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, idProofArray)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        sp_purpose.setAdapter(adapter)
        sp_purpose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                  selectedPurpose = idProofArray[position]
            }

        }
    }

}
