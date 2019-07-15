package com.indrif.vms.data.interfaces

import android.view.View


interface ClickListener {
    fun onItemClicked(position: Int, view: View)
    fun onLongItemClicked(position: Int,view: View)
}