package com.indrif.ui.adapters

import android.content.Context
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.indrif.vms.R
import com.indrif.vms.utils.CommonUtils


class FullScreenImageAdapter(private val context: Context, private val itemsData: List<Uri>) : PagerAdapter() {
    private val inflater: LayoutInflater

    init {

        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val layout = inflater.inflate(R.layout.full_screen_image_view, collection, false) as ViewGroup
        val dataCheck = itemsData[position].toString()
        try {
            var ivSelectedImage = layout.findViewById<ImageView>(R.id.iv_selected_img)
            CommonUtils.setImage(context, ivSelectedImage,dataCheck, R.mipmap.bg_main)
        //    ivSelectedImage.set(ImageSource.uri("/sdcard/DCIM/DSCM00123.JPG"));

            collection.addView(layout)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return itemsData.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}
