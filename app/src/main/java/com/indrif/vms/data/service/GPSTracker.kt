package com.indrif.vms.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log

class GPSTracker(mmContext: Context) : LocationListener {
    internal var isGPSEnabled = false
    internal var isNetworkEnabled = false
    internal var canGetLocation = false
    internal var location: Location? = null
    internal var latitude: Double = 0.toDouble()
    internal var longitude: Double = 0.toDouble()


    protected var locationManager: LocationManager? = null


    private var mContext: Context = mmContext

    init {
        val m_Location: Location? = getLocation()

    }


    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {

        try {
            locationManager = mContext
                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)

            println("******isGPSEnabled********" + isGPSEnabled)

            isNetworkEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            println("******isNetworkEnabled********" + isNetworkEnabled)

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d("GPS", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)

                            Log.d("location", location!!.toString() + "")
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            } else if (isNetworkEnabled) {
                                locationManager!!.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                                Log.d("Network", "Network Enabled")
                                if (locationManager != null) {
                                    location = locationManager!!
                                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                    Log.d("location", location!!.toString() + "")
                                    if (location != null) {
                                        latitude = location!!.latitude
                                        longitude = location!!.longitude
                                    }
                                }
                            }
                        }
                    }
                } else if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    Log.d("Network", "Network Enabled")
                    if (locationManager != null) {
                        location = locationManager!!
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        Log.d("location", location!!.toString() + "")
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude

                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


        return location
    }

    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        return latitude
    }

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        return longitude
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    override fun onLocationChanged(arg0: Location) {
        // TODO Auto-generated method stub

    }

    override fun onProviderDisabled(arg0: String) {
        // TODO Auto-generated method stub

    }

    override fun onProviderEnabled(arg0: String) {
        // TODO Auto-generated method stub

    }

    override fun onStatusChanged(arg0: String, arg1: Int, arg2: Bundle) {
        // TODO Auto-generated method stub

    }

    companion object {

        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
        private val MIN_TIME_BW_UPDATES = (1000 * 10 * 1).toLong() // 10 seconds
    }


}
