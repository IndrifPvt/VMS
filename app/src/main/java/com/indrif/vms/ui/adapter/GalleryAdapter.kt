package com.indrif.ui.adapters

import android.content.Context

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.indrif.vms.R
import com.indrif.vms.data.interfaces.ClickListenerGallery
import com.indrif.vms.utils.AppConstants
import com.indrif.vms.utils.CommonUtils
import kotlinx.android.synthetic.main.list_gallery_images_items.view.*
import java.util.ArrayList

class GalleryAdapter(private val context: Context, private val mList: List<String>, var clickListener: ClickListenerGallery) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>(){
    private val tempImageList: ArrayList<String>
    private val checkUncheckImageList: ArrayList<Boolean>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list_gallery_images_items, parent, false)
        return ViewHolder(v)
    }
    init {

        tempImageList = ArrayList(mList.size)
        checkUncheckImageList = ArrayList(mList.size)
        for (i in mList.indices) {
            checkUncheckImageList.add(false)
            tempImageList.add("")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindItems(position)
    }

    override fun getItemCount(): Int {
        return mList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int) {

            val imageUrl = mList[position]

            CommonUtils.setImage(context,itemView.iv_gallery_image,imageUrl,R.mipmap.bg_main)
            setScaleAnimation(itemView.iv_gallery_image!!)

            itemView.cb_image!!.isChecked = checkUncheckImageList[position]
            itemView.cb_image.setChecked(checkUncheckImageList[position])
            itemView.image_container.setOnClickListener(View.OnClickListener {
                if (checkUncheckImageList[layoutPosition]) {
                    checkUncheckImageList[layoutPosition] = false
                    if (tempImageList.size > 0) {
                        if (layoutPosition < tempImageList.size) {
                            tempImageList[layoutPosition] = ""
                        }
                    }

                    itemView.cb_image!!.isChecked = false
                    Log.v("Hi deleted position", layoutPosition.toString() + "")
                    clickListener.onItemClicked(adapterPosition,tempImageList,itemView)
                } else {
                    checkUncheckImageList[layoutPosition] = true
                    tempImageList[layoutPosition] = mList[layoutPosition]

                    itemView.cb_image!!.isChecked = true
                    Log.v("Hi insert at", layoutPosition.toString() + "")
                    clickListener.onItemClicked(adapterPosition,tempImageList,itemView)
                }
            })

        }
    }

    private fun setScaleAnimation(view: View) {
        val anim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.duration = AppConstants.FADE_DURATION.toLong()
        view.startAnimation(anim)
    }
}