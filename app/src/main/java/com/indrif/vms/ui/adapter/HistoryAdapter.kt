package com.indrif.vms.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indrif.vms.R
import com.indrif.vms.data.interfaces.ClickListener
import com.indrif.vms.models.SiteData
import com.indrif.vms.models.User
import com.indrif.vms.utils.AppConstants
import com.indrif.vms.utils.CommonUtils
import kotlinx.android.synthetic.main.layout_history_search_detail_item.view.*
import kotlinx.android.synthetic.main.spinner_item.view.*

class HistoryAdapter(private val context: Context, private val siteList: ArrayList<User>, var clickListener: ClickListener) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder?.bindItems(position)      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.layout_history_search_detail_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return siteList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int) {
            val siteListObj = siteList[position]
                CommonUtils.setImage(context, itemView.profile_image_view,siteListObj.image, R.drawable.dummy_user)
            itemView.tv_block_value.text = siteList.get(position).block
            itemView.tv_level_value.text = siteList.get(position).level
            itemView.tv_unit_value.text = siteList.get(position).unit
            itemView.setOnClickListener(View.OnClickListener {
                clickListener.onItemClicked(adapterPosition)
            })
        }
    }

}