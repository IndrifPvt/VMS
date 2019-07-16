package com.indrif.vms.ui.activities

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_selected_site_back -> {
                finish();
            }
            R.id.iv_selected_site_setting -> {
                showMenu(v);
            }
            R.id.btn_check_in -> {
                startActivity(Intent(this, IdProofSelectionActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out) }

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
}
