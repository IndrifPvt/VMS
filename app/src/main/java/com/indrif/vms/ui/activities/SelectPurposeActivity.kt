package com.indrif.vms.ui.activities

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

class SelectPurposeActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_purpose)
        inItData()
    }

    override fun onClick(v: View) {
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
