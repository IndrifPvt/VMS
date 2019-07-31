package com.indrif.vms.ui.activities

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.indrif.vms.R
import com.indrif.vms.core.BaseActivty
import com.indrif.vms.utils.AppConstants
import com.indrif.vms.utils.CommonUtils
import com.softuvo.utils.FileUtils.Companion.getUri
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.dialog_photo_select.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class UserProfileActivity : BaseActivty(), View.OnFocusChangeListener {
    private var dob = ArrayList<String>()
    private var id = ArrayList<String>()
    private var employer = ArrayList<String>()
    private var nam = ArrayList<String>()
    var name: String? = ""
    var d: String? = null
    var selectedIdProof = ""
    var idNumberForServer = ""
    private var userChoosenTask: String? = null
    private var galleryImageList: MutableList<String> = ArrayList()
    private var mImageUri: Uri? = null
    private var profileImageUri: Uri? = null
    private var newname = ArrayList<String>()
    private var userComingBy = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        dob.clear()
        id.clear()
        employer.clear()
        nam.clear()
        newname.clear()
        input_layout_dob.visibility = View.INVISIBLE
        input_layout_employer.visibility = View.INVISIBLE
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        var intent = intent
        var args = intent.getBundleExtra("BUNDLE")
        userComingBy = args.getString("userComingBy")
        if (args.getString("userComingFrom") == "IdProofOtherSelection") {
            et_id_type.setText(args.getString("IdType"))
        } else {
            selectedIdProof = args.getString("selectedIdProof") ?: ""
            nam = args.getStringArrayList("Name")
            id = args.getStringArrayList("ID")
            val byteArray = getIntent().getByteArrayExtra("image")
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            et_id_type.setText(selectedIdProof)
            profile_image.setImageBitmap(bmp)
            profileImageUri = getImageUri(this, bmp)
            if (selectedIdProof == "NRIC") {
                for (index in nam.indices) {
                    name = name + " " + nam.get(index)
                }
                var firstname = name!!.split(" ")
                var names = firstname[1]
                tv_user_name.setText(names)
                et_name.setText(name)
                for (index in id.indices) {
                    d = id.get(index)
                    d = d + " "
                }
                val separated = d!!.split(".")
                separated[0]
                separated[1]
                var nam = separated[1]
                //  name.replace(0,(stringLength?.minus(4)))
                et_id_no.setText(maskString(nam!!, 0, 6, '*'))
                dob = args.getStringArrayList("DOB")
                var dateofbirth = dob.get(0)
                input_layout_dob.visibility = View.VISIBLE
                et_id_dob.setText(dateofbirth)
            } else if (selectedIdProof == "S-PASS") {
                for (ind in nam!!.indices) {
                    if (nam.get(ind).contains("Sector") || nam.get(ind).contains(":")) {

                    } else {
                        newname.add(nam!!.get(ind))
                    }
                }
                for (index in newname.indices) {
                    name = name + " " + newname.get(index)
                }
                var firstname = name!!.split(" ")
                if (firstname.size > 0) {
                    var names = firstname[1]
                    tv_user_name.setText(names)
                } else
                    if (firstname.size > 0)
                        tv_user_name.setText(firstname[0])
                et_name.setText(name)
                if (id.size > 0)
                    et_id_no.setText(maskString(id.get(0)!!, 0, 6, '*'))
                employer = args.getStringArrayList("Employer")
                input_layout_employer.visibility = View.VISIBLE
                if (employer.size > 0)
                    et_id_employer.setText(employer.get(0))
            } else if (selectedIdProof == "DRIVING LICENSE") {
                for (ind in nam!!.indices) {
                    if (nam!!.get(ind).contains("Name") || nam.get(ind).contains("Date") || nam.get(ind).contains(":")) {

                    } else {
                        newname.add(nam!!.get(ind))
                    }
                }
                for (index in newname.indices) {
                    name = name + " " + newname.get(index)
                }
                var firstname = name!!.split(" ")
                var names = firstname[1]
                tv_user_name.setText(names ?: "")
                et_name.setText(name ?: "")
                et_id_no.setText(maskString(id.get(0)!!, 0, 6, '*') ?: "")
                dob = args.getStringArrayList("DOB")
                input_layout_dob.visibility = View.VISIBLE
                et_id_dob.setText(dob.get(0) ?: "")
            } else if (selectedIdProof == "WORK PERMIT") {
                for (ind in nam!!.indices) {
                    if (nam!!.get(ind).contains("Name") || nam.get(ind).contains("Sector") || nam.get(ind).contains(":")) {

                    } else {
                        newname.add(nam!!.get(ind))
                    }
                }
                for (index in newname.indices) {
                    name = name + " " + newname.get(index)
                }
                var firstname = name!!.split(" ")
                if(firstname.size>1)
                tv_user_name.setText(firstname[1])
                else
                    tv_user_name.setText(firstname[0])

                et_name.setText(name)
                //   et_id_no.setText(maskString(id.get(0)!!, 0, 6, '*'))
                if(args.getStringArrayList("Employer") != null) {
                    employer = args.getStringArrayList("Employer")
                    et_id_employer.setText(employer.get(0))
                }
                input_layout_employer.visibility = View.VISIBLE
            }
        }
        et_id_no.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            if (et_id_no.text.toString().length > 4)
                et_id_no.setText(maskString(et_id_no.text.toString(), 0, et_id_no.text.toString().length - 4, '*'))
            else
                et_id_no.setText(maskString(et_id_no.text.toString(), 0, et_id_no.text.toString().length - 1, '*'))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_history_back -> {
                dob.clear()
                id.clear()
                employer.clear()
                nam.clear()
                newname.clear()
                finish()
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }

            R.id.profile_image -> {
                selectImage()
            }
            R.id.btn_next_purpose -> {
                if (profileImageUri != null) {
                    val intent = Intent(this, SelectPurposeActivity::class.java)
                    val args = Bundle()
                    args.putString("userComingBy", userComingBy)
                    args.putString("idType", et_id_type.text.toString().trim())
                    args.putString("name", et_name.text.toString().trim())
                    args.putString("idNumber", idNumberForServer)
                    args.putString("dob", et_id_dob.text.toString().trim())
                    args.putString("employer", et_id_employer.text.toString().trim())
                    intent.putExtra("BUNDLE", args)
                    intent.putExtra("imageUri", profileImageUri.toString())
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                } else {
                    CommonUtils.showSnackbarMessage(context, "Please select image", R.color.colorPrimary)
                }
            }
        }
    }

    private fun maskString(strText: String, start: Int, end: Int, maskChar: Char): String {
        idNumberForServer = strText
        var startIndex = start
        var endIndex = end
        if (strText == null || strText.equals(""))
            return "";

        if (startIndex < 0)
            startIndex = 0

        if (endIndex > strText.length)
            endIndex = strText.length

        if (startIndex > endIndex)
            throw  Exception("End index cannot be greater than start index");

        var maskLength = endIndex - start

        if (maskLength == 0)
            return strText;

        var sbMaskString = StringBuilder(maskLength)
        for (i in 1..maskLength) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
    }

    private fun selectImage() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_photo_select)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.animationName
        dialog.setCancelable(true)
        dialog.show()
        dialog.iv_camera.setOnClickListener(View.OnClickListener {
            userChoosenTask = getString(R.string.take_photo)
            permissionCheck(0, 0)
            dialog.dismiss()
        })
        dialog.iv_gallery.setOnClickListener(View.OnClickListener {
            userChoosenTask = getString(R.string.choose_photo)
            permissionCheck(1, 1)
            dialog.dismiss()
        })
        dialog.iv_cancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })

    }

    private fun permissionCheck(check: Int, intentfor: Int) {
        when (check) {
            0 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (CommonUtils.requestAllPermissions(this, AppConstants.REQUEST_STORAGE_PERMISSION_CODE)) {

                    selectionIntent(intentfor)
                }
            } else {
                selectionIntent(intentfor)
            }
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (CommonUtils.requestAllPermissions(this, AppConstants.REQUEST_READ_STORAGE_PERMISSION_CODE)) {
                    selectionIntent(intentfor)
                }
            } else {
                selectionIntent(intentfor)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            AppConstants.REQUEST_STORAGE_PERMISSION_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck(0, 0)
                } else
                    CommonUtils.showSnackbarMessage(
                        context,
                        resources.getString(R.string.permision_denied),
                        R.color.colorPrimary
                    )

            AppConstants.REQUEST_CAMERA_PERMISSION_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask == getString(R.string.take_photo))
                        selectionIntent(0)
                } else
                    CommonUtils.showSnackbarMessage(
                        context,
                        resources.getString(R.string.permision_denied),
                        R.color.colorPrimary
                    )

            AppConstants.REQUEST_READ_STORAGE_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(getString(R.string.choose_photo), ignoreCase = true))
                        selectionIntent(1)
                } else
                    CommonUtils.showSnackbarMessage(
                        context,
                        resources.getString(R.string.permision_denied),
                        R.color.colorPrimary
                    )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.REQUEST_CAMERAINTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        CropImage.activity(mImageUri).start(this)
                    } catch (e: Exception) {
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri = result.uri
                    profileImageUri = resultUri
                    CommonUtils.setImage(context, profile_image, profileImageUri.toString(), R.drawable.dummy_user)

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            }

            AppConstants.GALLERY_INTENT -> {
                if (data != null) {
                    galleryImageList.clear()
                    //  imagesList.clear()
                    galleryImageList = data.getStringArrayListExtra("list")
                    /* if (galleryImageList.size > 1) {
                         val intent = Intent(context, GalleryCropActivity::class.java)
                         val bundle = Bundle()
                         bundle.putSerializable("list", imagesList)
                         bundle.putString("userComingFrom","vehicle_checklist")
                         intent.putExtras(bundle)
                         startActivityForResult(intent, AppConstants.GALLERY_CROP_INTENT)
                         overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                         return
                     } else if */(galleryImageList.size == 1)
                    CropImage.activity(getImageContentUri(context, File(galleryImageList.get(0)))).start(this)
                    return
                }
            }

            AppConstants.GALLERY_CROP_INTENT -> {
                if (data != null) {
                    var imagesList = data.getSerializableExtra("list") as ArrayList<Uri>
                    if (imagesList.size > 0) {
                        profileImageUri = imagesList[0]
                        CommonUtils.setImage(context, profile_image, profileImageUri.toString(), R.drawable.dummy_user)

                    }
                }
            }
        }
    }

    private fun selectionIntent(selected: Int) {
        val intent: Intent
        when (selected) {
            0 -> cameraIntent()
            1 -> {
                intent = Intent(context, GalleryActivity::class.java)
                startActivityForResult(intent, AppConstants.GALLERY_INTENT)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    private fun cameraIntent() {
        try {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            mImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
            startActivityForResult(intent, AppConstants.REQUEST_CAMERAINTENT)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf(filePath), null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            cursor.close()
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
            } else {
                return null
            }
        }
    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        var bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        var path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path)
    }
}