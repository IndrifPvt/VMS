package com.indrif.vms.ui.activities

import android.app.Dialog
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
import kotlinx.android.synthetic.main.activity_forgot_paasword.*
import java.util.HashMap

class ForgotPaaswordActivity : BaseActivty() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_paasword)
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back ->{
                finish()
            overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)}

            R.id.btn_submit -> {
                if (Validations.isValidEmail(et_forgot_email.text.toString().trim())) {
                    if (isNetworkConnected())
                        resetPassword()
                    else
                        CommonUtils.showSnackbarMessage(context, resources.getString(R.string.err_msg_internet), R.color.colorPrimary)
                } else
                    CommonUtils.showSnackbarMessage(context, resources.getString(R.string.err_msg_email_address), R.color.colorPrimary)
            }
        }
    }

    private var clickListner: CustomAlertDialogListener = object :
        CustomAlertDialogListener {
        override fun OnClick(dialog: Dialog) {
        }
        override fun OnCallBackClick() {
            finish()
            overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
        }
    }

    private fun resetPassword() {
        val mountMap = HashMap<String, String>()
        mountMap.put("user_email", et_forgot_email.text.toString().trim())
        try {
            showProgressDialog()
            compositeDrawable.add(
                    repository.resetPassword(mountMap)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                try {
                                    hideProgressDialog()
                                    if (result.code == ApiConstants.SUCCESS_CODE) {
                                        PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, false)
                                        CommonUtils.showMessagePopup(context, resources.getString(R.string.success_msg), resources.getString(R.string.password_reset_msg), R.mipmap.success, clickListner,View.GONE)
                                        finish()
                                        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
                                    } else
                                        CommonUtils.showSnackbarMessage(context, result.data.status, R.color.colorPrimary)

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

}
