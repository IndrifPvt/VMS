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
import android.widget.DatePicker
import android.app.DatePickerDialog
import android.widget.EditText
import java.util.*
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.widget.TimePicker
import android.app.TimePickerDialog
import kotlinx.android.synthetic.main.activity_history.*


class HistoryActivity : BaseActivty() {
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
               /* val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
                val minute = mcurrentTime.get(Calendar.MINUTE)
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(this@HistoryActivity,
                    TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                        tv_history_from_time_value.setText(
                            "$selectedHour:$selectedMinute"
                        )
                    }, hour, minute, true
                )//Yes 24 hour time
                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
*/
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

    }
}
