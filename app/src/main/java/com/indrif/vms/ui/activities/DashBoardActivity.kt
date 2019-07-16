package com.indrif.vms.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import kotlinx.android.synthetic.main.activity_selected_site.*
import android.view.MenuItem
import android.widget.PopupMenu
import com.indrif.vms.utils.CommonUtils
import com.indrif.vms.utils.dialog.CustomAlertDialogListener




class DashBoardActivity : BaseActivty(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_site)
        iv_selected_site_setting.setOnClickListener(this@DashBoardActivity);
        inItData()
    }
    fun showMenu(v: View) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener(this@DashBoardActivity)// to implement on click event on items of menu
        val inflater = popup.getMenuInflater()
        inflater.inflate(R.menu.selectedsitemenu, popup.getMenu())
        popup.gravity=Gravity.END
        popup.show()
    }
    private fun inItData(){
        tv_site_name.text = PreferenceHandler.readString(applicationContext, PreferenceHandler.SELECTED_SITE, "")
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return if (item != null) {
            when (item.getItemId()) {
                R.id.changepassword -> {
                    startActivity(Intent(this, ChangePasswordActivity::class.java))
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                    return true
                }


                else -> return true
            }
        } else {
            return true
        }
    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_pswd -> {
                startActivity(Intent(this, IdProofSelectionActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            R.id.menu_logout -> {

                CommonUtils.showMessagePopup(context, resources.getString(R.string.logout_alert), resources.getString(R.string.logout_alert_msg), R.mipmap.success, clickListner,View.GONE)

            }
        }
        return  true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_selected_site_back -> {
                finish()
            }
            R.id.iv_selected_site_setting -> {
                showMenu(v)
            }
            R.id.btn_check_in -> {
                startActivity(Intent(this, IdProofSelectionActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }

            R.id.btn_check_out -> {
                startActivity(Intent(this, CheckOutActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            R.id.btn_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

        private var clickListner: CustomAlertDialogListener = object :
            CustomAlertDialogListener {
            override fun OnClick(dialog: Dialog) {
            }
            override fun OnCallBackClick() {
                PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, false)
                PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_SITE_SELECTED, false)
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("EXIT", true)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
        }

}
