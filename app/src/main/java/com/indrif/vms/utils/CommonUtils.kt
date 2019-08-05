package com.indrif.vms.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.indrif.vms.R
import com.indrif.vms.data.api.ApiService
import com.indrif.vms.utils.dialog.CustomAlertDialog
import com.indrif.vms.utils.dialog.CustomAlertDialogListener
import com.indrif.vms.utils.dialog.Icon
import io.reactivex.disposables.CompositeDisposable
import java.io.*
import java.net.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object {
        var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
        val compositeDrawable: CompositeDisposable = CompositeDisposable()
        val repository = ApiService.Factory.create()
        fun getImeiNumber(context: Context): String {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
        }

        fun showSnackbarMessage(context: Context, message: String, color: Int) {
            val snackbar = Snackbar.make((context as Activity).findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, color))
            snackbar.show()
        }


        fun showToastMessage(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun setImage(applicationContext: Context, imageView: ImageView, mOutputPath: String?, placeholder: Int) {
            Glide.with(applicationContext)
                    .load(mOutputPath)
                    .apply(RequestOptions().placeholder(placeholder).error(placeholder))
                    .into(imageView)

        }
         fun getImageUri(context: Context, inImage: Bitmap): Uri {
            var bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            var path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path)
        }

        fun showMessagePopup(context: Context, title: String, message: String, icon: Int, clickListner: CustomAlertDialogListener, visibility: Int) {
            try {
                CustomAlertDialog.Builder(context as Activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeBtnVisibility(visibility)
                        .setPositiveBtnText(context.resources.getString(android.R.string.ok))
                        .isCancellable(false)
                        .setIcon(icon, Icon.Visible)
                        .OnPositiveClicked(object : CustomAlertDialogListener {
                            override fun OnClick(dialog: Dialog) {
                                dialog.dismiss()
                                clickListner.OnCallBackClick()
                            }

                            override fun OnCallBackClick() {

                            }
                        })
                        .OnNegativeClicked(object : CustomAlertDialogListener {
                            override fun OnCallBackClick() {
                            }

                            override fun OnClick(dialog: Dialog) {
                                dialog.dismiss()
                            }
                        })
                        .build()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }



        fun hideKeyboard(applicationContext: Context, mView: View?) {
            if (mView != null) {
                val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mView.windowToken, 0)
            }
        }

        fun isInternetConnection(context: Context): Boolean {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        @Throws(IOException::class)
        fun createImageFile(): File {
            // Create an image file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "Camera")

            return File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir      /* directory */
            )
        }

        fun getImageName(url: String): String {
            return url.substring(url.lastIndexOf('/') + 1, url.length)
        }




        fun checkAndRequestPermission(activity: Activity, permission: String, REQUEST_CODE: Int): Boolean {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(permission),
                        REQUEST_CODE)
                return false
            }
        }

        fun requestAllPermissions(activity: Activity,  permissionCode:Int): Boolean {
            var res: Boolean = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Marshmallow+
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),permissionCode)
                } else {
                    res = true
                }
            } else {
                // Pre-Marshmallow
                res = true
            }


            return res
        }

        fun getImageUriFromBitmap(inContext: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Contact", null);
            return Uri.parse(path);
        }

        fun getRealPathFromURI(contentURI: Uri, context: Context): String {
            var result: String
            var cursor: Cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) {
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                var idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }


        // Method to save an bitmap to a file
        fun bitmapToFile(context: Context,bitmap:Bitmap): Uri {
            // Get the context wrapper
            val wrapper = ContextWrapper(context)

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
            file = File(file,"${UUID.randomUUID()}.jpg")

            try{
                // Compress the bitmap and save in jpg format
                val stream:OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                stream.flush()
                stream.close()
            }catch (e:IOException){
                e.printStackTrace()
            }

            // Return the saved bitmap uri
            return Uri.parse(file.absolutePath)
        }

        /*fun saveImage(imageUrl: String, destinationFile: String) {
            val url = URL(imageUrl)
            val ist = url.openStream()
            val os = FileOutputStream(destinationFile)

            val b = ByteArray(2048)
            var length: Int

            while ((length = ist.read(b)) != -1) {
                os.write(b, 0, length)
            }

            ist.close()
            os.close()
        }*/
        fun isVideoFile(path: String): Boolean {
            val mimeType = URLConnection.guessContentTypeFromName(path)
            return mimeType != null && mimeType.startsWith("video")
        }

        @SuppressLint("SimpleDateFormat")
        fun datePicker(context: Context, tv_date_time: TextView): String {
            var date_time = ""
            var date_time1 = ""
            var mYear: Int = 0
            var mMonth: Int = 0
            var mDay: Int = 0
            var date = ""
            var date1 = ""

            val c = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(context,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        date_time = (monthOfYear + 1).toString() + " " + dayOfMonth.toString()
                        //*************Call Time Picker Here ********************
                        c.set(year, monthOfYear, dayOfMonth)
                        val format = SimpleDateFormat("MMMM dd ")
                        date = format.format(c.time)
                        val format1 = SimpleDateFormat("mm/dd/yyyy ")
                        date1 = format1.format(c.time)
                        date_time1 =
                            timePicker(context, date, tv_date_time, date1)
                    }, mYear, mMonth, mDay)
            datePickerDialog.show()
            return date_time1
        }

        fun timePicker(context: Context, date: String, tv_date_time: TextView, date1: String): String {
            var dateTime: String = date1
            var mHour: Int = 0
            var mMinute: Int = 0
            // Get Current Time
            val c = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        val ampm = if (hourOfDay < 12) "AM" else "PM"
                        mHour = hourOfDay
                        mMinute = minute

                        dateTime = date1 + " " + hourOfDay + ":" + minute
                        tv_date_time.text = (date + " " + hourOfDay + ":" + minute + " " + ampm)

                    }, mHour, mMinute, false)
            timePickerDialog.show()
            return dateTime
        }

        fun getCurrentDateTime(): String {
            var c = Calendar.getInstance().getTime()
            val df = SimpleDateFormat(AppConstants.DATE_TIME_FORMAT)
           var formattedDate = df.format(c)
            return formattedDate;
        }

        fun convertStringDateToDate(dateString: String): Date {
            var date: Date? = null
            val format = SimpleDateFormat(AppConstants.DATE_FORMAT)
            try {
                date = format.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return date!!
        }

        fun convertDateToStringDate(date:Date): String {
            var dateString = ""
            val dateFormat = SimpleDateFormat(AppConstants.DATE_FORMAT)
            try {
                dateString = dateFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return dateString
        }


        fun CapitaliseFirstWord(editText: EditText) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    val Capitlise = s.toString()
                    if (Capitlise.length >= 1 && Capitlise.substring(0, 1).toUpperCase() != Capitlise.substring(0, 1)) {
                        editText.setText(Capitlise.substring(0, 1).toUpperCase() + Capitlise.substring(1))
                        editText.setSelection(Capitlise.length)
                    }
                }
            })
        }

        fun convertDateFormat(dateToConvert: String): String {
            val inputPattern = AppConstants.DATE_FORMAT
//            val outputPattern = "yyyy-MMM-dd HH:mm:ss"
            val outputPattern = AppConstants.DATE_FORMAT_FOR_SERVER
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String = ""
            try {
                date = inputFormat.parse(dateToConvert)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        fun convertDateFormatToNumber(dateToConvert: String): String {
            val inputPattern = AppConstants.DATE_FORMAT
//            val outputPattern = "yyyy-MMM-dd HH:mm:ss"
            val outputPattern = AppConstants.DATE_FORMAT_FOR_SERVER
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String = ""
            try {
                date = inputFormat.parse(dateToConvert)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }



        fun setClickEffect(mContext: Context, view: View) {
            val animation = AnimationUtils.loadAnimation(mContext, R.anim.click_effect_animation)
            view.startAnimation(animation)
        }

    }
}