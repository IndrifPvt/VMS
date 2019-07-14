package com.indrif.vms.core

import android.support.v4.app.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indrif.vms.data.api.ApiService
import com.indrif.vms.utils.CallProgressWheel
import com.indrif.vms.utils.CommonUtils
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var rootview: View
    val compositeDrawable: CompositeDisposable = CompositeDisposable()
    val repository = ApiService.Factory.create()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootview = inflater.inflate(getLayoutId(), container, false)
        mContext = activity!!.applicationContext
        return rootview
    }

    abstract fun getLayoutId(): Int;
    abstract fun onClick(v: View);

    fun isNetworkConnected(): Boolean {
        return CommonUtils.isInternetConnection(mContext)
    }

    fun showProgressDialog() {
        CallProgressWheel.showLoadingDialog(mContext)
    }

    fun hideProgressDialog() {
        CallProgressWheel.dismissLoadingDialog()
    }

    override fun onDestroy() {
        hideProgressDialog()
        compositeDrawable.clear()
        super.onDestroy()
    }
}