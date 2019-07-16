package com.indrif.vms.ui.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.indrif.vms.R
import com.indrif.vms.data.interfaces.ClickListener
import com.indrif.vms.models.SiteData
import com.indrif.vms.utils.CommonUtils
import kotlinx.android.synthetic.main.spinner_item.view.*

class SiteListAdapter(private val context: Context, private val siteList: List<SiteData>, var clickListener: ClickListener) : RecyclerView.Adapter<SiteListAdapter.ViewHolder>(){
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder?.bindItems(position)      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.spinner_item, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return siteList.size

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int) {
            val siteListObj = siteList[position]

            itemView.tv_sp_item.text = siteListObj.name
            itemView.setOnClickListener(View.OnClickListener {
                clickListener.onItemClicked(adapterPosition)
            })

        }
    }

}