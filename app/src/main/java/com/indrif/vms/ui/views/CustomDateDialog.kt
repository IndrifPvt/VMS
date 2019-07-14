package com.indrif.vms.ui.views

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.TextView


import com.indrif.vms.R

import java.util.Calendar

class CustomDateDialog private constructor() {
    companion object {
        val instance = CustomDateDialog()
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var datepicker: DatePicker? = null

    fun DatePicker(mContext: Context, dateDialogListener: DateDialogListener) {
        val dialog: Dialog
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog = Dialog(mContext, R.style.MyDatePickerDialogTheme)
        } else
            dialog = Dialog(mContext, R.style.MyDatePickerStyle)
        dateDialogListener.setDialogContext(dialog)
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_date_picker)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.animationName
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        datepicker = dialog.findViewById(R.id.datepicker)
        datepicker!!.minDate = Calendar.getInstance().timeInMillis - 1000
        val tv_ok = dialog.findViewById<TextView>(R.id.tv_ok)
        val tv_cancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        tv_ok.setOnClickListener {
            val day = datepicker!!.dayOfMonth
            val month = datepicker!!.month
            val year = datepicker!!.year

            if (checkDate(day, month, year)) {
                dateDialogListener.onOkayClick(day, month, year)
            } else {
                //   SnackbarUtil.showWarningShortSnackbarTopDialog(dialog, "Please Select Date from Today");
            }
        }
        tv_cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun checkDate(day: Int, month: Int, year: Int): Boolean {
        val c = Calendar.getInstance()

        // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val today = c.time

        // reuse the calendar to set user specified date
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val dateSpecified = c.time

        return if (dateSpecified.before(today))
            false
        else
            true
    }


    interface DateDialogListener {
        fun setDialogContext(context: Dialog)

        fun onOkayClick(date: Int, month: Int, year: Int)
    }

    }
}

