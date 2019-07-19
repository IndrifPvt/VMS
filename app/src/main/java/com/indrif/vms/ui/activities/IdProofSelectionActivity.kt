package com.indrif.vms.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import kotlinx.android.synthetic.main.activity_id_proof_selection.*

class IdProofSelectionActivity : BaseActivty() {
    var selectedIdProof  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_proof_selection)
        setAdapter()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_scan_id -> {
                if(selectedIdProof.equals("SELECT ID TYPE") ||selectedIdProof.equals("OTHER")){
                    val intent = Intent(this, UserDetailActivity::class.java)
                    val args = Bundle()
                    args.putString("userComingFrom", "IdProofSelectionActivity")
                    intent.putExtra("BUNDLE", args)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
                else {
                    var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("selectedId", selectedIdProof)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out) }
            }
        }
    }

    private fun setAdapter() {
        val idProofArray =resources.getStringArray(R.array.id_array)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, idProofArray)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        sp_id_proof.setAdapter(adapter)
        sp_id_proof?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedIdProof = idProofArray[position]

            }

        }
    }
}
