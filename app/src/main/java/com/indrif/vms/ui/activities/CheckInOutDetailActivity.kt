package com.indrif.vms.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.models.User
import com.indrif.vms.ui.adapter.HistoryAdapter
import kotlinx.android.synthetic.main.activity_check_in_out_detail.*

class CheckInOutDetailActivity : BaseActivty() {
    private var userlist:ArrayList<User> = ArrayList()
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_detail_back -> {
                finish()
            }
            R.id.iv_history_detail_home -> {
                startActivity(Intent(this, DashBoardActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_out_detail)
        userlist= getIntent().getSerializableExtra("FILES_TO_SEND") as ArrayList<User>
        val adapter = HistoryAdapter(this, userlist)
        rv_checkinhistory.layoutManager =LinearLayoutManager(this)
        rv_checkinhistory.setAdapter(adapter)
    }

}

