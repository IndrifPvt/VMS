package com.indrif.vms.data.service

import android.app.Service
import android.content.Intent
import android.os.AsyncTask
import android.os.IBinder
import android.widget.Toast
import com.indrif.vms.data.service.GPSTracker
import java.util.*


class UserlocationUpdateService : Service() {

    private val status = false
    // //////////////////Location////////////////////

    internal var currentLat = "0.0"
    internal var currentLong = "0.0"
    private val c_lat = "0.0"
    private val c_long = "0.0"

    private var timer: Timer? = null
    internal var UnreadNotificationCount = "0"

    private var gpsTracker: GPSTracker? = null
    internal var hashMap: HashMap<String, String>? = null


    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this@UserlocationUpdateService, "Service Start",Toast.LENGTH_LONG).show()
        gpsTracker = GPSTracker(this@UserlocationUpdateService)

        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {

            override fun run() {
                DriverUpdateDetails()
//                gpsTrackerCredentials().execute()
            }
        }, 0, (10 * 1000).toLong())

        return Service.START_STICKY
    }


    private inner class gpsTrackerCredentials : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg arg0: String): String? {

            return null
        }

        override fun onPostExecute(unused: String) {
            super.onPostExecute(unused)
            DriverUpdateDetails()
        }
    }


    private fun DriverUpdateDetails() {
        try {
            currentLat = java.lang.Double.toString(gpsTracker!!.getLocation()!!.latitude)
            currentLong = java.lang.Double.toString(gpsTracker!!.getLocation()!!.longitude)

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }


    override fun onDestroy() {

        super.onDestroy()

        Toast.makeText(this@UserlocationUpdateService, "Service Stop",Toast.LENGTH_LONG).show()
        println("-------------Service Stop-----------")
        try {
            timer!!.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
