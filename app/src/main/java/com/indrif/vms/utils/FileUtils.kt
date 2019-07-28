package com.softuvo.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import com.indrif.vms.R
import java.io.File
import java.io.FileFilter
import java.io.InputStream
import java.text.DecimalFormat
import java.util.*

class FileUtils {
    companion object {
        /** TAG for log messages.  */
        val TAG = "FileUtils"
        private val DEBUG = false // Set to true to enable logging

        val MIME_TYPE_AUDIO = "audio/*"
        val MIME_TYPE_TEXT = "text/*"
        val MIME_TYPE_IMAGE = "image/*"
        val MIME_TYPE_VIDEO = "video/*"
        val MIME_TYPE_APP = "application/*"

        val HIDDEN_PREFIX = "."

        /**
         * Gets the extension of a file name, like ".png" or ".jpg".
         *
         * @param uri
         * @return Extension including the dot("."); "" if there is no extension;
         * null if uri was null.
         */
        fun getExtension(uri: String?): String? {
            if (uri == null) {
                return null
            }

            val dot = uri!!.lastIndexOf(".")
            return if (dot >= 0) {
                uri!!.substring(dot)
            } else {
                // No extension.
                ""
            }
        }

        /**
         * @return Whether the URI is a local one.
         */
        fun isLocal(url: String?): Boolean {
            return if (url != null && !url!!.startsWith("http://") && !url!!.startsWith("https://")) {
                true
            } else false
        }

        /**
         * @return True if Uri is a MediaStore Uri.
         * @author paulburke
         */
        fun isMediaUri(uri: Uri?): Boolean {
            return "media".equals(uri!!.getAuthority(), ignoreCase = true)
        }

        /**
         * Convert File into Uri.
         *
         * @param file
         * @return uri
         */
        fun getUri(file: File?): Uri? {
            return if (file != null) {
                Uri.fromFile(file)
            } else null
        }

        /**
         * Returns the path only (without file name).
         *
         * @param file
         * @return
         */
        fun getPathWithoutFilename(file: File?): File? {
            if (file != null) {
                if (file!!.isDirectory()) {
                    // no file to be split off. Return everything
                    return file
                } else {
                    val filename = file!!.getName()
                    val filepath = file!!.getAbsolutePath()

                    // Construct path without file name.
                    var pathwithoutname = filepath.substring(0,
                            filepath.length - filename.length)
                    if (pathwithoutname.endsWith("/")) {
                        pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
                    }
                    return File(pathwithoutname)
                }
            }
            return null
        }

        /**
         * @return The MIME type for the given file.
         */
        fun getMimeType(file: File): String {

            val extension = getExtension(file.getName())

            return if (extension!!.length > 0) MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension!!.substring(1)) else "application/octet-stream"

        }

        /**
         * @return The MIME type for the give Uri.
         */
//        fun getMimeType(context: Context, uri: Uri): String {
//            val file = File(getPath(context, uri))
//            return getMimeType(file)
//        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is [LocalStorageProvider].
         * @author paulburke
         */
        fun isLocalStorageDocument(uri: Uri): Boolean {
            return false //LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         * @author paulburke
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.getAuthority()
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         * @author paulburke
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.getAuthority()
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         * @author paulburke
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.getAuthority()
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.getAuthority()
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Drive.
         */
        private fun isGoogleDriveUri(uri: Uri): Boolean {
            return "com.google.android.apps.docs.storage" == uri.getAuthority()
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         * @author paulburke
         */
        fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                          selectionArgs: Array<String>?): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
                if (cursor != null && cursor!!.moveToFirst()) {
                    if (DEBUG)
                        DatabaseUtils.dumpCursor(cursor)

                    val column_index = cursor!!.getColumnIndexOrThrow(column)
                    return cursor!!.getString(column_index)
                }
            } finally {
                if (cursor != null)
                    cursor!!.close()
            }
            return null
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         * @author paulburke
         */
        fun getDataColumnGoogleDrive(context: Context, uri: Uri?, data: Intent): String? {
            var result: String = ""
            var cursor: Cursor? = null
            var inputStream: InputStream
            try {

                val returnUri = data.getData()
//                val mimeType = context.contentResolver.getType(returnUri)
                val returnCursor = context.contentResolver.query(returnUri, null, null, null, null)
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                returnCursor.moveToFirst()
                result = returnCursor.getString(nameIndex)//filename

                inputStream = context.contentResolver.openInputStream(uri)
                result = downloadFileToFolder(context, inputStream, context.resources.getString(R.string.hint_google_drive), result)
//                try {
////                    val folder = File(Environment
////                            .getExternalStorageDirectory().toString() + File.separator+context.resources.getString(R.string.app_name))
////                    if (!folder.exists()) {
////                        folder.mkdirs();
////                    }
////                    val out = File(folder.absolutePath+File.separator+
////                             result)
////                   if(!out.exists()){
////                       out.copyInputStreamToFile(inputStream)
////                   }
////
////                    result = out.absolutePath
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    inputStream.close()
//                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (cursor != null)
                    cursor!!.close()
            }
            return result
        }

        fun downloadFileToFolder(context: Context, inputStream: InputStream, folderName: String, filename: String): String {
            var result: String = ""
            try {
                val folder = File(Environment
                        .getExternalStorageDirectory().toString() + File.separator + context.resources.getString(R.string.app_name) + File.separator + folderName)
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                val out = File(folder.absolutePath + File.separator +
                        filename)
                if (!out.exists()) {
                    out.copyInputStreamToFile(inputStream)
                }

                result = out.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream.close()
            }
            return result
        }

        fun File.copyInputStreamToFile(inputStream: InputStream) {
            inputStream.use { input ->
                this.outputStream().use { fileOut ->
                    input.copyTo(fileOut)
                }
            }
        }

        fun InputStream.toFile(path: String) {
            use { input ->
                File(path).outputStream().use { input.copyTo(it) }
            }
        }

        /**
         * Get a file path from a Uri. This will get the the path for Storage Access
         * Framework Documents, as well as the _data field for the MediaStore and
         * other file-based ContentProviders.<br></br>
         * <br></br>
         * Callers should check whether the path is local before assuming it
         * represents a local file.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @see .isLocal
         * @see .getFile
         * @author paulburke
         */
        fun getPath(context: Context, uri: Uri, data: Intent): String? {

            if (DEBUG)
                Log.d(TAG + " File -",
                        "Authority: " + uri.getAuthority() +
                                ", Fragment: " + uri.getFragment() +
                                ", Port: " + uri.getPort() +
                                ", Query: " + uri.getQuery() +
                                ", Scheme: " + uri.getScheme() +
                                ", Host: " + uri.getHost() +
                                ", Segments: " + uri.getPathSegments().toString()
                )

            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // LocalStorageProvider
                if (isLocalStorageDocument(uri)) {
                    // The path is the id
                    return DocumentsContract.getDocumentId(uri)
                } else if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {

                    // Return the remote address
                    return if (isGoogleDriveUri(uri)) {
                        Log.e("======", "fhffffhf" + uri.getLastPathSegment() + "=====" + uri.path)
//                        uri.path
                        getDataColumnGoogleDrive(context, uri, data)
                    } else {
                        getDataColumnGoogleDrive(context, uri, data)
                    }

                }// MediaProvider
                // DownloadsProvider
                // ExternalStorageProvider
            } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {

                // Return the remote address
                return if (isGooglePhotosUri(uri) || isGoogleDriveUri(uri)) uri.getLastPathSegment() else getDataColumn(context, uri, null, null)

            } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
                return uri.getPath()
            }// File
            // MediaStore (and general)

            return null
        }


        fun getFile(context: Context, uri: Uri?): File? {
            if (uri != null) {
                val path = getPath(context, uri)
                if (path != null && isLocal(path)) {
                    return File(path)
                }
            }
            return null
        }

        fun getPath(context: Context, uri: Uri): String? {
            if (DEBUG)
                Log.d(TAG + " File -",
                        "Authority: " + uri.authority +
                                ", Fragment: " + uri.fragment +
                                ", Port: " + uri.port +
                                ", Query: " + uri.query +
                                ", Scheme: " + uri.scheme +
                                ", Host: " + uri.host +
                                ", Segments: " + uri.pathSegments.toString()
                )

            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // LocalStorageProvider
                if (isLocalStorageDocument(uri)) {
                    // The path is the id
                    return DocumentsContract.getDocumentId(uri)
                } else if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return (Environment.getExternalStorageDirectory()).toString() + "/" + split[1]
                    }

                } else if (isDownloadsDocument(uri)) {

                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]

                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }// MediaProvider
                // DownloadsProvider
                // ExternalStorageProvider
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }// File
            // MediaStore (and general)

            return null
        }

        /**
         * Get the file size in a human-readable string.
         *
         * @param size
         * @return
         * @author paulburke
         */
        fun getReadableFileSize(size: Int): String {
            val BYTES_IN_KILOBYTES = 1024
            val dec = DecimalFormat("###.#")
            val KILOBYTES = " KB"
            val MEGABYTES = " MB"
            val GIGABYTES = " GB"
            var fileSize = 0f
            var suffix = KILOBYTES

            if (size > BYTES_IN_KILOBYTES) {
                fileSize = (size / BYTES_IN_KILOBYTES).toFloat()
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES
                    if (fileSize > BYTES_IN_KILOBYTES) {
                        fileSize = fileSize / BYTES_IN_KILOBYTES
                        suffix = GIGABYTES
                    } else {
                        suffix = MEGABYTES
                    }
                }
            }
            return (dec.format(fileSize) + suffix).toString()
        }

        /**
         * Attempt to retrieve the thumbnail of given File from the MediaStore. This
         * should not be called on the UI thread.
         *
         * @param context
         * @param file
         * @return
         * @author paulburke
         */
//        fun getThumbnail(context: Context, file: File): Bitmap? {
//            return getThumbnail(context, getUri(file), getMimeType(file))
//        }

        /**
         * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
         * should not be called on the UI thread.
         *
         * @param context
         * @param uri
         * @return
         * @author paulburke
         */
//        fun getThumbnail(context: Context, uri: Uri): Bitmap? {
//            return getThumbnail(context, uri, getMimeType(context, uri))
//        }
//
//        /**
//         * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
//         * should not be called on the UI thread.
//         *
//         * @param context
//         * @param uri
//         * @param mimeType
//         * @return
//         * @author paulburke
//         */
//        fun getThumbnail(context: Context, uri: Uri?, mimeType: String): Bitmap? {
//            if (DEBUG)
//                Log.d(TAG, "Attempting to get thumbnail")
//
//            if (!isMediaUri(uri)) {
//                Log.e(TAG, "You can only retrieve thumbnails for images and videos.")
//                return null
//            }
//
//            var bm: Bitmap? = null
//            if (uri != null) {
//                val resolver = context.getContentResolver()
//                var cursor: Cursor? = null
//                try {
//                    cursor = resolver.query(uri!!, null, null, null, null)
//                    if (cursor!!.moveToFirst()) {
//                        val id = cursor!!.getInt(0)
//                        if (DEBUG)
//                            Log.d(TAG, "Got thumb ID: " + id)
//
//                        if (mimeType.contains("video")) {
//                            bm = MediaStore.Video.Thumbnails.getThumbnail(
//                                    resolver,
//                                    id.toLong(),
//                                    MediaStore.Video.Thumbnails.MINI_KIND, null)
//                        } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {
//                            bm = MediaStore.Images.Thumbnails.getThumbnail(
//                                    resolver,
//                                    id.toLong(),
//                                    MediaStore.Images.Thumbnails.MINI_KIND, null)
//                        }
//                    }
//                } catch (e: Exception) {
//                    if (DEBUG)
//                        Log.e(TAG, "getThumbnail", e)
//                } finally {
//                    if (cursor != null)
//                        cursor!!.close()
//                }
//            }
//            return bm
//        }

        //        /**
//         * File and folder comparator. TODO Expose sorting option method
//         *
//         * @author paulburke
//         */
        var sComparator: Comparator<File> = object : Comparator<File> {
            override fun compare(f1: File, f2: File): Int {
                // Sort alphabetically by lower case, which is much cleaner
                return f1.getName().toLowerCase().compareTo(
                        f2.getName().toLowerCase())
            }
        }


        /**
         * File (not directories) filter.
         *
         * @author paulburke
         */
        var sFileFilter: FileFilter = object : FileFilter {
            override fun accept(file: File): Boolean {
                val fileName = file.getName()
                // Return files only (not directories) and skip hidden files
                return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX)
            }
        }

        /**
         * Folder (directories) filter.
         *
         * @author paulburke
         */
        var sDirFilter: FileFilter = object : FileFilter {
            private val okFileExtensions = arrayOf("jpg", "png", "gif")

            override fun accept(file: File): Boolean {
                val fileName = file.getName()
//                // Return directories only and skip hidden directories
                return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX)
            }
        }

        /**
         * Get the Intent for selecting content to be used in an Intent Chooser.
         *
         * @return The intent for opening a file with Intent.createChooser()
         * @author paulburke
         */
        fun createGetContentIntent(): Intent {
            // Implicitly allow the user to select a particular kind of data
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            // The MIME data type filter
            intent.type = "*/*"
            // Only return URIs that can be opened with ContentResolver
            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            return intent
        }
    }
}