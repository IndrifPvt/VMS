package com.indrif.vms.ui.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.interfaces.ClickListenerGallery
import com.indrif.vms.ui.adapter.GalleryCropAdapterJava
import com.indrif.vms.ui.adapter.GalleryCropSlideShowAdapter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_gallery_crop.*
import kotlin.collections.ArrayList

class GalleryCropActivity : BaseActivty() {
    private var galleryCropAdapter: GalleryCropAdapterJava? = null
    private var galleryImageList: ArrayList<Uri> = ArrayList()
  //  private var imageRemarksList: ArrayList<JobUploadPod> = ArrayList()
    private var selectedPosition = 0
    private var userComingFrom = ""
    private var deladd:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_crop)
        deladd.clear()
        galleryImageList = getIntent().getExtras().get("list") as ArrayList<Uri>
      //  imageRemarksList = getIntent().getExtras().get("list_remark") as ArrayList<JobUploadPod>
        userComingFrom = getIntent().getExtras().get("userComingFrom") as String
        if (userComingFrom.equals("complete_job", true)){
        deladd = getIntent().getExtras().get("delivery_address") as ArrayList<String> }

    }


    override fun onResume() {
        super.onResume()
        setViewPager()
        setRecyclerViewAdapter()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        deladd.clear()
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                deladd.clear()
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }

            R.id.iv_crop ->
                CropImage.activity(galleryImageList!![selectedPosition]).start(this)

            R.id.iv_done -> {
                if (userComingFrom.equals("vehicle_checklist", true)) {
                    deladd.clear()
                    val intent = Intent()
                    intent.putExtra("list", galleryImageList)
                    setResult(787, intent)
                    finish()
                    overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
                }
            }
        }
    }



    private fun setViewPager() {
        pager!!.adapter = GalleryCropSlideShowAdapter(context, galleryImageList)

        pager!!.currentItem = selectedPosition
        pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                rv_images!!.smoothScrollToPosition(selectedPosition)
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
                selectedPosition = arg0
                galleryCropAdapter!!.setCurrentItem(selectedPosition)

            }

            override fun onPageScrollStateChanged(arg0: Int) {
            }
        })
    }

    private fun setRecyclerViewAdapter() {
        /*  galleryCropAdapter = GalleryCropAdapter(this, galleryImageList!!, selectedPosition, clickListener)
          rv_images!!.setHasFixedSize(true)
          val llm = LinearLayoutManager(context)
          llm.orientation = LinearLayoutManager.HORIZONTAL
          rv_images!!.layoutManager = llm

          rv_images!!.adapter = galleryCropAdapter*/

        galleryCropAdapter = GalleryCropAdapterJava(this, galleryImageList, selectedPosition, object : GalleryCropAdapterJava.OnClickImageListener {

            override fun onClickImage(position: Int) {
                selectedPosition = position
                pager.setCurrentItem(selectedPosition)
            }
        })
        rv_images.setHasFixedSize(true)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        rv_images.setLayoutManager(llm)

        rv_images.setAdapter(galleryCropAdapter)
    }

    private var clickListener: ClickListenerGallery = object : ClickListenerGallery {
        override fun onItemClicked(position: Int, imagesList: ArrayList<String>, view: View) {
            selectedPosition = position
            pager!!.currentItem = selectedPosition
        }

        override fun onLongItemClicked(position: Int, view: View) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == -1) {
                    val resultUri = result.uri
                    galleryImageList!!.removeAt(this.selectedPosition)
                    galleryImageList!!.add(this.selectedPosition, resultUri)
                    return
                }
            }
        }
    }
}
