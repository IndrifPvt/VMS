package com.indrif.vms.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.utils.ApiConstants
import com.indrif.vms.utils.CommonUtils
import com.indrif.vms.utils.Validations
import com.indrif.vms.utils.dialog.CustomAlertDialogListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_check_out_by_id.*

class CheckOutByIdActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out_by_id)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back ->{
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)}

            R.id.btn_submit -> {
                if (!Validations.isNullOrEmpty(et_id_number)) {
                    if (isNetworkConnected())
                        checkOutUser(et_id_number.text.trim().toString())
                    else
                        CommonUtils.showSnackbarMessage(context, resources.getString(R.string.err_msg_internet), R.color.colorPrimary)
                } else
                    CommonUtils.showSnackbarMessage(context, resources.getString(R.string.err_msg_valid_id), R.color.colorPrimary)
            }
        }
    }

    private fun checkOutUser(idNumber:String) {
        val mountMap = HashMap<String, String>()
        mountMap.put("idNumber", idNumber)
        mountMap.put("siteId", PreferenceHandler.readString(applicationContext, PreferenceHandler.SITE_ID, ""))
        mountMap.put("checkOutTime",CommonUtils.getCurrentDateTime().trim())
        try {
            showProgressDialog()
            compositeDrawable.add(
                repository.checkOutUser(mountMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgressDialog()
                            if (result.code == ApiConstants.SUCCESS_CODE) {
                                hideProgressDialog()
                                CommonUtils.showMessagePopup(this, result.message, result.data.status , R.mipmap.success, clickListner,View.GONE)
                            } else{
                                hideProgressDialog()
                                CommonUtils.showSnackbarMessage(this, result.data.status, R.color.colorPrimary)
                            }

                        } catch (e: Exception) {
                            hideProgressDialog()
                            e.printStackTrace() }
                    }, { error ->
                        hideProgressDialog()
                        error.printStackTrace()
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace() }
    }

    private var clickListner: CustomAlertDialogListener = object :
        CustomAlertDialogListener {
        override fun OnClick(dialog: Dialog) {
        }
        override fun OnCallBackClick() {
            val intent = Intent(applicationContext, DashBoardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("EXIT", true)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
        }
    }
}
