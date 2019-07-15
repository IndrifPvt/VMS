/*
package com.codlog.utils.fcm

import android.content.ContentValues.TAG
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import com.codlog.data.prefs.PreferenceHandler

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        PreferenceHandler.writeString(applicationContext,PreferenceHandler.PREF_KEY_FCM_TOKEN,refreshedToken)
    }


}
*/
