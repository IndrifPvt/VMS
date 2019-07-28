package com.indrif.vms.ui.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import java.io.File
import java.util.ArrayList
import java.util.Comparator
import java.util.HashSet
import java.util.TreeSet

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.data.interfaces.ClickListenerGallery
import com.indrif.vms.utils.CommonUtils
import com.indrif.ui.adapters.GalleryAdapter
import kotlinx.android.synthetic.main.activity_custom_gallery.*

class GalleryActivity : BaseActivty() {
    private val imageList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_gallery)
        populateImagesFromGallery()
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back ->{
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }

            R.id.btn_done ->{
                val selectedImagelist = ArrayList<String>()
                for (i in imageList.indices) {
                    if (!imageList[i].equals("", ignoreCase = true)) {
                        val set = HashSet<String>()
                        if (set.add(imageList[i])) {
                            selectedImagelist.add(imageList[i])
                        }
                    }
                }
                if (selectedImagelist.size > 1)
                    CommonUtils.showSnackbarMessage(context,resources.getString(R.string.max_ten_images),R.color.colorPrimary)
              /*   else if (selectedImagelist.size == 0)
                    CommonUtils.showSnackbarMessage(context,resources.getString(R.string.max_ten_images),R.color.colorPrimary)*/
                else {
                    val intent = Intent()
                    intent.putStringArrayListExtra("list", selectedImagelist)
                    setResult(999, intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }
        }
    }

    private fun populateImagesFromGallery() {
        if (!mayRequestGalleryImages()) {
            return
        }
        val imageUrls = removeDuplicate(loadPhotosFromNativeGallery())
        initializeRecyclerView(imageUrls)
    }

    fun removeDuplicate(str: ArrayList<String>): ArrayList<String> {
        val set = TreeSet(Comparator<String> { o1, o2 ->
            val file = CommonUtils.getImageName(o1)
            val newFile = CommonUtils.getImageName(o2)
            if (file.equals(newFile, ignoreCase = true)) {
                0
            } else 1
        })
        set.addAll(str)

        return ArrayList(set)
    }

    private fun initializeRecyclerView(imageUrls: ArrayList<String>) {
        if (imageUrls.size > 0) {
            val galleryAdapter = GalleryAdapter(context, imageUrls, clickListener)

          /*  val galleryAdapter = GalleryAdapter(context, imageUrls, object : GalleryAdapter.OnImageSelectListener {
               override fun onImageSelect(position: Int, imagesList: ArrayList<String>) {
                    imageList.clear()
                    imageList.addAll(imagesList)
                }
            })*/
            val mNoOfColumns = calculateNoOfColumns(getApplicationContext())

            rv_gallery_images!!.layoutManager = GridLayoutManager(getApplicationContext(), mNoOfColumns)
            rv_gallery_images!!.adapter = galleryAdapter
        } else {
            tv_no_image!!.setVisibility(View.VISIBLE)
            rv_gallery_images!!.visibility = View.GONE
        }
    }

    private var clickListener: ClickListenerGallery = object : ClickListenerGallery {
        override fun onItemClicked(position: Int, imagesList: ArrayList<String>, view: View) {
            imageList.clear()
            imageList.addAll(imagesList)
        }
            override fun onLongItemClicked(position: Int, view: View) {
            }
        }

    fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return (dpWidth / 180).toInt()
    }


    private fun mayRequestGalleryImages(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), REQUEST_FOR_STORAGE_PERMISSION)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_FOR_STORAGE_PERMISSION -> {

                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        populateImagesFromGallery()
                    } else {
                        Toast.makeText(this, "Go to settings and enable permission", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loadPhotosFromNativeGallery(): ArrayList<String> {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val imagecursor = context!!.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, "$orderBy DESC")
        val imageUrls = ArrayList<String>()
        if (imagecursor != null && imagecursor.count > 0) {
            for (i in 0 until imagecursor.count) {
                imagecursor.moveToPosition(i)
                val dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val file = File(imagecursor.getString(dataColumnIndex))
                val size = file.length()
                if (size > 0)
                    imageUrls.add(imagecursor.getString(dataColumnIndex))
            }
            imagecursor.close()
        }
        return imageUrls
    }

    companion object {
        private val REQUEST_FOR_STORAGE_PERMISSION = 999
    }
}
