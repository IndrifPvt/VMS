package com.indrif.vms.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.ui.adapters.FullScreenImageAdapter

import kotlinx.android.synthetic.main.activity_gallery_full_screen.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class GalleryFullScreenActivity : BaseActivty() {
    lateinit var latestModelList: ArrayList<Uri>
    override fun onClick(v: View) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_full_screen)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tv_logout.visibility= View.GONE
        toolbar_title.text = intent.getStringExtra("toolBarTitle")
        setUpUI()
    }

    fun setUpUI() {
        try {
            if (intent.hasExtra("DataObject")) {
                latestModelList = intent.getStringArrayListExtra("DataObject") as ArrayList<Uri>
                //latestModelList = latestModelResponsePojo.images
                val fullScreenImageAdapter = FullScreenImageAdapter(context, latestModelList)
                viewPager.adapter = fullScreenImageAdapter
               /* viewPager.setA = fullScreenImageAdapter*/
                viewPager.currentItem = intent.getIntExtra("selectedPosition", 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}
