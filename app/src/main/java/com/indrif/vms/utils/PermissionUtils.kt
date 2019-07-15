package com.indrif.vms.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class PermissionUtils {

    companion object {
        val MY_PERMISSIONS_REQUEST_ALL = 0
        val MY_PERMISSIONS_REQUEST_WRITE_READ = 1
        val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2
        val MY_PERMISSIONS_REQUEST_CAMERA = 3
        val MY_PERMISSIONS_REQUEST_LOCATION = 4
        val RECORD_REQUEST_CODE = 101
        fun setupAudioPermissions(activity: Activity):Boolean {
            var res: Boolean = false
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_REQUEST_CODE
                )
                res=false
            }else{
                res=true
            }
            return res
        }

        fun checkPermissionStorage(activity: Activity): Boolean {
            var res: Boolean = false
            if (Build.VERSION.SDK_INT >= M) {
                // Marshmallow+
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_READ
                    )
                } else {
                    res = true
                }
            } else {
                // Pre-Marshmallow
                res = true
            }


            return res
        }

        fun checkPermissionCamera(activity: Activity): Boolean {
            var res: Boolean = false
            if (Build.VERSION.SDK_INT >= M) {
                // Marshmallow+
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)//                        , Manifest.permission.FLASHLIGHT
                            , MY_PERMISSIONS_REQUEST_CAMERA
                    )
                } else {
                    res = true
                }
            } else {
                // Pre-Marshmallow
                res = true
            }


            return res
        }

        fun checkPermissionLocation(activity: Activity): Boolean {
            var res: Boolean = false
            if (Build.VERSION.SDK_INT >= M) {
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION
                    )
                    res = false
                } else {
                    res = true
                }
            }else {
                res = true
            }
            return res
        }


        fun requestAllPermissions(activity: Activity): Boolean {
            var res: Boolean = false
            if (Build.VERSION.SDK_INT >= M) {
                // Marshmallow+
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                } else {
                    res = true
                }
            } else {
                // Pre-Marshmallow
                res = true
            }


            return res
        }

        fun checkPermissionReadContacts(activity: Activity): Boolean {
            var res: Boolean = false
            if (Build.VERSION.SDK_INT >= M) {
                // Marshmallow+
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                } else {
                    res = true
                }
            } else {
                // Pre-Marshmallow
                res = true
            }
            return res
        }
    }
}
