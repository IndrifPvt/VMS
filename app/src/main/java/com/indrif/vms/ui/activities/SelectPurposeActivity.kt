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
        val mountMap = HashMap<String, RequestBody>()

       /* mountMap.put("User_id", RequestBody.create(MediaType.parse("text/plain"), PreferenceHandler.readString(applicationContext, PreferenceHandler.USER_ID, "")))
        mountMap.put("job_id", RequestBody.create(MediaType.parse("text/plain"), jobId))
        mountMap.put("remarks", RequestBody.create(MediaType.parse("text/plain"), et_remarks.getText().toString().trim()))
        mountMap.put("isUpdated", RequestBody.create(MediaType.parse("text/plain"), "true"))  */
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

            }

        }
    }

}
