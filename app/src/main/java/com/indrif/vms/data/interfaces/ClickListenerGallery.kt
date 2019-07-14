package com.indrif.vms.data.interfaces

import android.view.View
import java.util.ArrayList


interface ClickListenerGallery {
    fun onItemClicked(position: Int, imagesList:ArrayList<String>, view: View)
    fun onLongItemClicked(position: Int,view: View)
}