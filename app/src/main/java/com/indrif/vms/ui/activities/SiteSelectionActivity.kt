package com.indrif.vms.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.indrif.vms.R
import kotlinx.android.synthetic.main.activity_site_selection.*

class SiteSelectionActivity : AppCompatActivity() {
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
 }
}
