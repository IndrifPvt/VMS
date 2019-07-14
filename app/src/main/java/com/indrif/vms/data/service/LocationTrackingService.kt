package com.indrif.vms.data.service
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.location.Geocoder
import com.indrif.vms.data.prefs.PreferenceHandler
import java.util.*


class LocationTrackingService : Service() {

    var locationManager: LocationManager? = null

    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        context =applicationContext
        if (locationManager == null)
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                INTERVAL,
                DISTANCE, locationListeners[1])
        } catch (e: SecurityException) {
            Log.e(TAG, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Network provider does not exist", e)
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                INTERVAL,
                DISTANCE, locationListeners[0])
        } catch (e: SecurityException) {
            Log.e(TAG, "Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "GPS provider does not exist", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (i in 0..locationListeners.size) {
                try {
                    locationManager?.removeUpdates(locationListeners[i])
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to remove location listeners")
                }
            }
    }


    companion object {
        var context:Context?=null;
        val TAG = "LocationTrackingService"

        val INTERVAL = 1000.toLong() // In milliseconds
        val DISTANCE = 10.toFloat() // In meters

        val locationListeners = arrayOf(
            LTRLocationListener(LocationManager.GPS_PROVIDER),
            LTRLocationListener(LocationManager.NETWORK_PROVIDER)
        )

        class LTRLocationListener(provider: String) : android.location.LocationListener {

            val lastLocation = Location(provider)

            override fun onLocationChanged(location: Location?) {
                try {
                    lastLocation.set(location)
                    PreferenceHandler.writeString(context as Context, PreferenceHandler.PREF_KEY_LAT,lastLocation.latitude.toString())
                    PreferenceHandler.writeString(context as Context, PreferenceHandler.PREF_KEY_LNG,lastLocation.longitude.toString())
                    Log.e("===", "" + lastLocation.latitude)
                    Log.e("===", "" + lastLocation.longitude)
                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(context, Locale.getDefault())

                    addresses = geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    val city = addresses[0].getLocality()
                    val state = addresses[0].getAdminArea()
                    val country = addresses[0].getCountryName()
                    PreferenceHandler.writeString(context as Context, PreferenceHandler.PREF_KEY_ADDRESS,city+","+state+","+country)
                    val postalCode = addresses[0].getPostalCode()
                    val knownName = addresses[0].getFeatureName() // Only if available else return NULL
                    Log.e("===", "" + addresses)
                }catch (e:Exception){e.printStackTrace()}
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

        }
    }

}