package com.indrif.vms.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList

class ImagePicker {
    companion object {
        private val DEFAULT_MIN_WIDTH_QUALITY = 400        // min pixels
        private val LOG_TAG = "ImagePicker"
        private val TEMP_IMAGE_NAME = "tempImage"
        var minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY
        lateinit var bm: Bitmap
        fun getPickImageIntent(context: Context): Intent? {
            var chooserIntent: Intent? = null

            var intentList: MutableList<Intent> = ArrayList()

            val pickIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoIntent.putExtra("return-data", true)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)))
            intentList = addIntentsToList(context, intentList, pickIntent)
            intentList = addIntentsToList(context, intentList, takePhotoIntent)

            if (intentList.size > 0) {
                chooserIntent = Intent.createChooser(intentList.removeAt(intentList.size - 1),
                        "Choose")
                chooserIntent!!.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray<Parcelable>())
            }

            return chooserIntent
        }

        private fun addIntentsToList(context: Context, list: MutableList<Intent>, intent: Intent): MutableList<Intent> {
            val resInfo = context.packageManager.queryIntentActivities(intent, 0)
            for (resolveInfo in resInfo) {
                val packageName = resolveInfo.activityInfo.packageName
                val targetedIntent = Intent(intent)
                targetedIntent.`package` = packageName
                list.add(targetedIntent)
                Log.d(LOG_TAG, "Intent: " + intent.action + " package: " + packageName)
            }
            return list
        }


        fun getImageFromResult(context: Context, resultCode: Int,
                               imageReturnedIntent: Intent): Bitmap {
            Log.d(LOG_TAG, "getImageFromResult, resultCode: " + resultCode)

            val imageFile = getTempFile(context)
            if (resultCode == Activity.RESULT_OK) {
                val selectedImage: Uri?
                val isCamera = imageReturnedIntent == null ||
                        imageReturnedIntent.data == null ||
                        imageReturnedIntent.data == Uri.fromFile(imageFile)
                if (isCamera) {
                    /** CAMERA  */
                    selectedImage = Uri.fromFile(imageFile)
                } else {
                    /** ALBUM  */
                    selectedImage = imageReturnedIntent!!.data
                }
                Log.d(LOG_TAG, "selectedImage: " + selectedImage!!.path + "===" + imageReturnedIntent)

                bm = getImageResized(context, selectedImage)
                val rotation = getRotation(context, selectedImage, isCamera)
                bm = rotate(bm, rotation)
            }
            return bm
        }


        fun getTempFile(context: Context): File {
            val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME)
            imageFile.parentFile.mkdirs()
            return imageFile
        }

        private fun decodeBitmap(context: Context, theUri: Uri, sampleSize: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inSampleSize = sampleSize

            var fileDescriptor: AssetFileDescriptor? = null
            try {
                fileDescriptor = context.contentResolver.openAssetFileDescriptor(theUri, "r")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            val actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor!!.fileDescriptor, null, options)

            Log.d(LOG_TAG, options.inSampleSize.toString() + " sample method bitmap ... " +
                    actuallyUsableBitmap.width + " " + actuallyUsableBitmap.height)

            return actuallyUsableBitmap
        }

        /**
         * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
         */
        private fun getImageResized(context: Context, selectedImage: Uri): Bitmap {
            var bm: Bitmap? = null
            val sampleSizes = intArrayOf(5, 3, 2, 1)
            var i = 0
            do {
                bm = decodeBitmap(context, selectedImage, sampleSizes[i])
                Log.d(LOG_TAG, "resizer: new bitmap width = " + bm.width)
                i++
            } while (bm!!.width < minWidthQuality && i < sampleSizes.size)
            return bm
        }


        private fun getRotation(context: Context, imageUri: Uri, isCamera: Boolean): Int {
            val rotation: Int
            if (isCamera) {
                rotation = getRotationFromCamera(context, imageUri)
            } else {
                rotation = getRotationFromGallery(context, imageUri)
            }
            Log.d(LOG_TAG, "Image rotation: " + rotation)
            return rotation
        }

        private fun getRotationFromCamera(context: Context, imageFile: Uri): Int {
            var rotate = 0
            try {

                context.contentResolver.notifyChange(imageFile, null)
                val exif = ExifInterface(imageFile.path)
                val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return rotate
        }

        fun getRotationFromGallery(context: Context, imageUri: Uri): Int {
            val columns = arrayOf(MediaStore.Images.Media.ORIENTATION)
            val cursor = context.contentResolver.query(imageUri, columns, null, null, null) ?: return 0

            cursor.moveToFirst()

            val orientationColumnIndex = cursor.getColumnIndex(columns[0])
            return cursor.getInt(orientationColumnIndex)
        }


        private fun rotate(bm: Bitmap, rotation: Int): Bitmap {
            if (rotation != 0) {
                val matrix = Matrix()
                matrix.postRotate(rotation.toFloat())
                return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
            }
            return bm
        }
    }
}
