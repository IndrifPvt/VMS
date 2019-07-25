package com.indrif.vms.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.indrif.vms.R
import com.indrif.vms.models.User
import com.indrif.vms.utils.CommonUtils

class FilterAdapter(private val context: Context, private val userList: ArrayList<User>,  var clickListener: ItemClickListener) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>(), Filterable {
    private var userSearchList: List<User>? = null
    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_block_value: TextView
        var tv_level_value: TextView
        var tv_unit_value: TextView
        var profile_image_view: ImageView

        init {
            profile_image_view = view.findViewById(R.id.profile_image_view)
            tv_block_value = view.findViewById(R.id.tv_block_value)
            tv_level_value = view.findViewById(R.id.tv_level_value)
            tv_unit_value = view.findViewById(R.id.tv_unit_value)


            view.setOnClickListener(View.OnClickListener {
                clickListener.onItemClicked(userSearchList!![adapterPosition])
            })
        }
    }
    init {
        this.userSearchList = userList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_history_search_detail_item, parent, false)
        return FilterViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val user = userSearchList!![position]
        CommonUtils.setImage(context, holder.profile_image_view,user.image, R.drawable.dummy_user)
        holder.tv_block_value.text = user.block
        holder.tv_level_value.text = user.level
        holder.tv_unit_value.text = user.unit

    }
    override fun getItemCount(): Int {
        return userSearchList!!.size
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                userSearchList = if (charString.isEmpty()) {
                    userList
                } else {
                    val filteredList = ArrayList<User>()
                    for (row in userList) {
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = userSearchList
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                userSearchList = filterResults.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }
    interface ItemClickListener {
        fun onItemClicked(user: User)
    }
}