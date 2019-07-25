package com.indrif.vms.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.PopupMenu
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.interfaces.ClickListener
import com.indrif.vms.models.User
import com.indrif.vms.ui.adapter.HistoryAdapter
import com.indrif.vms.utils.ApiConstants
import com.indrif.vms.utils.CommonUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_check_in_out_detail.*
import java.util.HashMap

class CheckInOutDetailActivity : BaseActivty(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private var userlist: ArrayList<User> = ArrayList()
    lateinit var mountMap: HashMap<String, String>
    var fromDate = ""
    var toDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_out_detail)
        setInitData()
    }

    private fun setInitData() {
        mountMap = getIntent().getSerializableExtra("mountMap") as HashMap<String, String>
        fromDate = mountMap.get("fromDate")!!
        toDate = mountMap.get("toDate")!!
        userhistory()
        iv_filter.setOnClickListener(this@CheckInOutDetailActivity);

    }

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
                mountMap.clear()
                mountMap.put("fromDate", fromDate)
                mountMap.put("toDate",toDate)
                mountMap.put("type", "0")
                userhistory()

            }
            R.id.menu_check_out ->{
                mountMap.clear()
                mountMap.put("fromDate", fromDate)
                mountMap.put("toDate",toDate)
                mountMap.put("type", "1")
                userhistory()
            }
            R.id.menu_check_in ->{
                mountMap.clear()
                mountMap.put("fromDate", fromDate)
                mountMap.put("toDate",toDate)
                mountMap.put("type", "2")
                userhistory()
            }
        }
        return true

    }

    private var clickListener: ClickListener = object : ClickListener {
        override fun onItemClicked(position: Int) {
            try {
                val intent = Intent(this@CheckInOutDetailActivity, UserDetailActivity::class.java)
                intent.putExtra("userDetails", userlist.get(position))
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun userhistory() {
        try {
            showProgressDialog()
            compositeDrawable.add(
                repository.userHistory(mountMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgressDialog()
                            if (result.code == ApiConstants.SUCCESS_CODE) {
                                userlist = result.data.users
                                if(userlist.size<1){
                                    tv_no_data_found.visibility = VISIBLE
                                    rv_checkinhistory.visibility = GONE
                                }
                                else {
                                    tv_no_data_found.visibility = GONE
                                    rv_checkinhistory.visibility = VISIBLE
                                    val adapter = HistoryAdapter(this, userlist, clickListener)
                                    rv_checkinhistory.layoutManager = LinearLayoutManager(this)
                                    rv_checkinhistory.setAdapter(adapter)
                                }
                            } else
                                CommonUtils.showSnackbarMessage(context, result.data.status, R.color.colorPrimary)

                        } catch (e: Exception) {
                            hideProgressDialog()
                            e.printStackTrace()
                        }
                    }, { error ->
                        hideProgressDialog()
                        error.printStackTrace()
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

