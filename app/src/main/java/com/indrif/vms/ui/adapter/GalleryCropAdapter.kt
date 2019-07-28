package com.indrif.ui.adapters

import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.indrif.vms.R
import com.indrif.vms.data.interfaces.ClickListenerGallery
import kotlinx.android.synthetic.main.gallery_item.view.*
import java.util.ArrayList

class GalleryCropAdapter(private val mContext: AppCompatActivity, private val galleryImageList: ArrayList<Uri>, private var selectedPosition: Int, var clickListener: ClickListenerGallery) : RecyclerView.Adapter<GalleryCropAdapter.ViewHolder>(){
    private var oldPosition: Int = 0
    private val imageList: ArrayList<String> = ArrayList()
    private var mPosition =selectedPosition

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindItems(position)
    }

    override fun getItemCount(): Int {
        return galleryImageList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int) {
            Glide.with(mContext)
                    .load(galleryImageList[position])
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(itemView.iv_image!!)
       //     mPosition = position
            if (position == selectedPosition) {
                itemView.iv_image!!.setPadding(6, 6, 6, 6)
                itemView.iv_image!!.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryUnslected))
                return
            }
            itemView.iv_image!!.setPadding(0, 0, 0, 0)
                    clickListener.onItemClicked(layoutPosition,imageList,itemView)

        }
    }
    fun setCurrentItem(selectedPos: Int) {
        oldPosition = mPosition
        selectedPosition = selectedPos
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }
}