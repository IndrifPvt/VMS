package com.indrif.vms

import android.app.Application
/*import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric*/

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // fabric
      //  Fabric.with(this, Crashlytics())
        //fb
       /* FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/
    }
}

