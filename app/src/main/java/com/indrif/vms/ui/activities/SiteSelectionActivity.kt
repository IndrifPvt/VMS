package com.indrif.vms.ui.activities

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.utils.CommonUtils
import com.indrif.vms.utils.dialog.CustomAlertDialogListener
import kotlinx.android.synthetic.main.activity_site_selection.*

class SiteSelectionActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_selection)
        setAdapter()
    }
 private fun setAdapter() {
    val siteArray = resources.getStringArray(R.array.site_array)
     val adapter = ArrayAdapter(this, R.layout.spinner_item, siteArray)
     adapter.setDropDownViewResource(R.layout.spinner_item);
     sp_address.setAdapter(adapter)
     sp_address?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
         override fun onNothingSelected(parent: AdapterView<*>?) {

         }

         override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
             PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_SITE_SELECTED, true)
             PreferenceHandler.writeString(applicationContext, PreferenceHandler.SELECTED_SITE, siteArray[position])
         }

     }
 }
    private fun inItData(){

    }

    override fun onClick(v: View) {
        when (v.id) {
           /* R.id.iv_settings -> {
               CommonUtils.showMessagePopup(context,resources.getString(R.string.logout_alert),resources.getString(R.string.logout_alert_msg), R.mipmap.info, clickListner,View.GONE)
            }*/
            }
    }

   /* private var clickListner: CustomAlertDialogListener = object :
        CustomAlertDialogListener {
        override fun OnClick(dialog: Dialog) {
        }
        override fun OnCallBackClick() {
            finish()
            startActivity(Intent(context, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
        }
    }*/
}
