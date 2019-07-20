package com.indrif.vms.core

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.indrif.vms.R
import com.indrif.vms.data.api.ApiService
import com.indrif.vms.utils.CallProgressWheel
import com.indrif.vms.utils.CommonUtils

import io.reactivex.disposables.CompositeDisposable

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


abstract class BaseActivty : AppCompatActivity() {
    lateinit var context: Context
    val compositeDrawable: CompositeDisposable = CompositeDisposable()
    val repository = ApiService.Factory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        getFbKeyHash()
    }

    abstract fun onClick(v: View)

    fun isNetworkConnected(): Boolean {
        return CommonUtils.isInternetConnection(context)
    }

    fun showProgressDialog() {
        CallProgressWheel.showLoadingDialog(context)
    }

    fun hideProgressDialog() {
        CallProgressWheel.dismissLoadingDialog()
    }

    fun hideKeyboard(applicationContext: Context, mView: View?) {
        if (mView != null) {
            val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mView.windowToken, 0)
        }
    }
    override fun onDestroy() {
        hideProgressDialog()
        compositeDrawable.clear()
        super.onDestroy()
    }

    fun getFbKeyHash() {
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
    }
}