package com.indrif.vms.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.utils.ApiConstants
import com.indrif.vms.utils.CommonUtils
import com.indrif.vms.utils.Validations
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*




class LoginActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        et_email.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !Validations.isValidEmail(et_email.text.toString().trim())) {
                et_email.setError(resources.getString(R.string.err_msg_email_address))
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_forgot_password -> {
                startActivity(Intent(this, SiteSelectionActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }

            R.id.btn_sign_in -> {
                if (Validations.isValidEmail(et_email.text.toString().trim())) {
                    if (isNetworkConnected())
                        loginUser()
                    else
                        CommonUtils.showSnackbarMessage(context, resources.getString(R.string.err_msg_internet), R.color.colorPrimary)
                } else
                    CommonUtils.showSnackbarMessage(context, resources.getString(R.string.err_msg_email_address), R.color.colorPrimary)
            }
        }
    }

    private fun loginUser() {
        val mountMap = HashMap<String, String>()
        mountMap.put("user_email", et_email.text.toString().trim())
        mountMap.put("user_password", et_password.text.toString().trim())
        mountMap.put("device_token", "")
        try {
            showProgressDialog()
            compositeDrawable.add(
                    repository.loginUser(mountMap)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                try {
                                    hideProgressDialog()
                                    if (result.code == ApiConstants.SUCCESS_CODE) {
                                        CommonUtils.showSnackbarMessage(context, result.data.status, R.color.colorPrimary)
                                        PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, true)
                                        PreferenceHandler.writeString(applicationContext, PreferenceHandler.USER_ID, result.data.user_id)
                                      //  startActivity(Intent(this, DashBoardActivity::class.java))
                                        finish()
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
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

