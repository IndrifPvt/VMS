package com.indrif.vms.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.models.User
import com.indrif.vms.ui.adapter.HistoryAdapter
import com.indrif.vms.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_check_in_out_detail.*

class CheckInOutDetailActivity : BaseActivty(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private var userlist: ArrayList<User> = ArrayList()
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_detail_back -> {
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
            R.id.iv_history_detail_home -> {
                startActivity(Intent(this, DashBoardActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }

        R.id.iv_filter -> {
            val popup = PopupMenu(this, v)
            popup.setOnMenuItemClickListener(this)// to implement on click event on items of menu
            val inflater = popup.getMenuInflater()
            inflater.inflate(R.menu.filter_menu, popup.getMenu())
            popup.gravity= Gravity.END
            popup.show()
        }
    }

        }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_all -> {
               /* startActivity(Intent(this, ChangePasswordActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)*/
            }
            R.id.menu_check_out ->{}
             //   CommonUtils.showMessagePopup(context, resources.getString(R.string.logout_alert), resources.getString(R.string.logout_alert_msg), R.mipmap.info, clickListner,View.VISIBLE)
            R.id.menu_check_in ->{}
               // CommonUtils.showMessagePopup(context, resources.getString(R.string.logout_alert), resources.getString(R.string.logout_alert_msg), R.mipmap.info, clickListner,View.VISIBLE)

        }
        return true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_out_detail)
        setInitData()
    }

    private fun setInitData() {
        iv_filter.setOnClickListener(this@CheckInOutDetailActivity);

        userlist= getIntent().getSerializableExtra("FILES_TO_SEND") as ArrayList<User>
        if(userlist.size<1){
            tv_no_data_found.visibility = VISIBLE
            rv_checkinhistory.visibility = GONE
        }
        else {
            tv_no_data_found.visibility = GONE
            rv_checkinhistory.visibility = VISIBLE
            val adapter = HistoryAdapter(this, userlist)
            rv_checkinhistory.layoutManager = LinearLayoutManager(this)
            rv_checkinhistory.setAdapter(adapter)
        }
    }


}

