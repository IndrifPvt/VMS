package com.indrif.vms.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.utils.CommonUtils
import com.indrif.vms.utils.Validations
import kotlinx.android.synthetic.main.activity_login.*
import android.app.DatePickerDialog
import java.util.*
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.app.TimePickerDialog
import kotlinx.android.synthetic.main.activity_history.*
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.widget.*
import com.indrif.vms.data.prefs.PreferenceHandler
import com.indrif.vms.models.User
import com.indrif.vms.utils.ApiConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Array
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList






class HistoryActivity : BaseActivty() {
    private var hr: Int = 0
    private var min: Int = 0
    private var datePicker: DatePicker? = null
    private var calendar: Calendar? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private  var count =0;
    private var diff:Long?=null
    var fromdialog:DatePickerDialog?=null
    var todialog:DatePickerDialog?=null
    private var fromcal:Calendar?=null
    private var tocal:Calendar?=null
    private var fromtocal:Calendar?=null
    private var mname:CharSequence =""
    private var fdate:String=""
    private var todate:String=""
    private var Userlist:ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        fromcal = Calendar.getInstance();
        tocal = Calendar.getInstance();
        tocal!!.add(Calendar.DATE,0)
        var c = Calendar.getInstance();
        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        updateTime(hr, min,"initialize");
        calendar = Calendar.getInstance();
        year = calendar!!.get(Calendar.YEAR);
        month = calendar!!.get(Calendar.MONTH);
        day = calendar!!.get(Calendar.DAY_OF_MONTH);
        var monthnames = calendar!!.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        var monname = monthnames.subSequence(0,3)
        showDate(year, month+1, day,monname.toString(),"a");
    }
    private fun utilTime(value: Int): String {
        return if (value < 10) "0$value" else value.toString()
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_back -> {
                finish()
                }

            R.id.iv_history_home -> {
                startActivity(Intent(this, DashBoardActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            R.id.tv_history_from_time_value -> {
                var mTimePicker = TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    hr = selectedHour;
                    min = selectedMinute;
                    updateTime(hr, min,"from");
                    }
                TimePickerDialog(this, mTimePicker, hr, min, false).show();
            }
            R.id.tv_history_to_time_value -> {
                var mTimePicker = TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    hr = selectedHour;
                    min = selectedMinute;
                    updateTime(hr, min,"to");
                }
                TimePickerDialog(this, mTimePicker, hr, min, false).show();
            }
            R.id.tv_history_from_date_value -> {
                var cal = Calendar.getInstance()
                cal.add(Calendar.DATE,-30)
                var myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    fromcal!!.set(arg1,arg2,arg3)
                    diff = fromcal!!.timeInMillis
                    var monthname = fromcal!!.getDisplayName( Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    showDate(arg1, arg2 + 1, arg3,monthname,"from")
                }
                fromdialog = DatePickerDialog(this,
                    myDateListener, year, month, day)

                if (count ==0)
                {
                    fromdialog!!.getDatePicker().setMinDate(cal.timeInMillis);
                    fromdialog!!.getDatePicker().setMaxDate(System.currentTimeMillis());
                }
                else{
                    var dateFormat = SimpleDateFormat("yyyy-MM-dd")
                  // fromdialog!!.getDatePicker().setMinDate(tocal!!.timeInMillis);
                    var d = dateFormat.parse("${tocal!!.get(Calendar.YEAR)}-${tocal!!.get(Calendar.MONTH)}-${tocal!!.get(Calendar.DAY_OF_MONTH)}" )
                  //  fromdialog!!.getDatePicker().setMinDate(tocal!!.timeInMillis);

                    fromdialog!!.getDatePicker().setMaxDate(tocal!!.timeInMillis);
                }

                fromdialog!!.show()

            }
            R.id.tv_history_to_date_value -> {
                var myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    tocal!!.set(arg1,arg2,arg3)
                    fromcal!!.set(arg1,arg2,arg3)
                    var monthname = tocal!!.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                    count =1;
                    showDate(arg1, arg2 + 1, arg3,monthname,"to")
                }
                todialog = DatePickerDialog(this,
                    myDateListener, year, month, day)
                todialog!!.getDatePicker().setMaxDate(System.currentTimeMillis());
                todialog!!.show()
            }
            R.id.btn_history_sign_in -> {
                    val msDiff = tocal!!.timeInMillis - diff!!
                    val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
                    if(daysDiff>29)
                    {
                        Toast.makeText(context, "From Date and To date do not have gap more than 30 days" , Toast.LENGTH_LONG).show()
                    }

                else if(daysDiff<0)
                    {
                        Toast.makeText(context, "From Date cant be aftrer To Date" , Toast.LENGTH_LONG).show()
                    }
                else
                {
                    userhistory()
                }
            }
        }
    }
    private fun updateTime(hours: Int, mins: Int,coming:String) {
        var hours = hours
        var timeSet = ""
        if (hours > 12) {
            hours -= 12
            timeSet = "PM"
        } else if (hours == 0) {
            hours += 12
            timeSet = "AM"
        } else if (hours == 12)
            timeSet = "PM"
        else
            timeSet = "AM"
        var minutes = ""
        if (mins < 10)
            minutes = "0$mins"
        else
            minutes = mins.toString()
        val aTime = StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString()
        if(coming=="from") {
            tv_history_from_time_value.setText(aTime)
        }
        else if(coming == "to")
        {
            tv_history_to_time_value.setText(aTime)
        }
        else
        {
            tv_history_from_time_value.setText(aTime)
            tv_history_to_time_value.setText(aTime)
        }

    }
    private fun showDate(year: Int, month: Int, day: Int,monthname:String,come:String) {

        if(come=="from") {
             mname = monthname.subSequence(0,3)
            fdate =StringBuilder().append(mname).append(" ")
                .append(day).append(", ").append(year).toString()
            tv_history_from_date_value.setText(
                StringBuilder().append(day).append(" ")
                    .append(mname).append(", ").append(year)
            )
        }
        else if(come == "to")
        {
            mname = monthname.subSequence(0,3)
            todate =StringBuilder().append(mname).append(" ")
                .append(day).append(", ").append(year).toString()
            tv_history_to_date_value.setText(
                StringBuilder().append(day).append(" ")
                    .append(mname).append(", ").append(year)
            )
        }
        else
        {
            fromcal = Calendar.getInstance()
            fromcal!!.add(Calendar.DATE,-30)
            diff = fromcal!!.timeInMillis
            var y = fromcal!!.get(Calendar.YEAR)
            var m = fromcal!!.get(Calendar.MONTH+1)
            var d =fromcal!!.get(Calendar.DAY_OF_MONTH)
            var monthnames = fromcal!!.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            var monname = monthnames.subSequence(0,3)
            mname = monthname.subSequence(0,3)
            fdate =StringBuilder().append(monname).append(" ")
                .append(d).append(", ").append(y).toString()
            todate =StringBuilder().append(monname).append(" ")
                .append(day).append(", ").append(year).toString()
            tv_history_to_date_value.setText(
                StringBuilder().append(day).append(" ")
                    .append(mname).append(", ").append(year))
            tv_history_from_date_value.setText(
                StringBuilder().append(d).append(" ")
                    .append(monname).append(", ").append(y))

        }
    }
    private fun userhistory() {
        val mountMap = HashMap<String, String>()
        mountMap.put("fromDate", fdate)
        mountMap.put("toDate",todate)
        mountMap.put("type", "0")
        try {
            showProgressDialog()
            compositeDrawable.add(
                repository.userHistory(mountMap)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgressDialog()
                            if (result.code == ApiConstants.SUCCESS_CODE) {
                                Userlist = result.data.users
                                val intent = Intent(this@HistoryActivity, CheckInOutDetailActivity::class.java)
                                intent.putExtra("FILES_TO_SEND", Userlist)
                                startActivity(intent)
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
