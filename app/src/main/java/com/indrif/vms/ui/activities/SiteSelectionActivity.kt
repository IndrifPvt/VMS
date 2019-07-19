package com.indrif.vms.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.models.SiteData
import com.indrif.vms.utils.ApiConstants
import com.indrif.vms.utils.CommonUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_site_selection.*

class SiteSelectionActivity : BaseActivty() {
    private var selectedSite = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_selection)
        getSiteList()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_submit_site -> {
                if(selectedSite.equals("SELECT SITE"))
                    CommonUtils.showSnackbarMessage(context, resources.getString(R.string.select_site_message), R.color.colorPrimary)

                else {
                    PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_SITE_SELECTED, true)
                    PreferenceHandler.writeString(applicationContext, PreferenceHandler.SELECTED_SITE, selectedSite)
                    startActivity(Intent(this, DashBoardActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }

        }
    }

  private fun getSiteList(){
      try {
          showProgressDialog()
          compositeDrawable.add(
              repository.getSiteList()
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .subscribe({ result ->
                      try {
                          hideProgressDialog()
                          if (result.code == ApiConstants.SUCCESS_CODE) {
                              Toast.makeText(context,"Length is"+result.data.sites.size,Toast.LENGTH_LONG).show()
                                 setAdapter(result.data.sites)
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

    private fun setAdapter(siteList:List<SiteData>) {
        var siteArray: ArrayList<String> = ArrayList()
        siteArray.add("SELECT SITE")
        for (item in 0 until siteList.size){
            siteArray.add(siteList.get(item).name)
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_item, siteArray)
        adapter.setDropDownViewResource(R.layout.spinner_item);
        sp_address.setAdapter(adapter)
        sp_address?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSite = siteArray[position]
            }

        }
    }
}
