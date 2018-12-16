package com.cenes.application;

import android.app.Application;
import android.os.StrictMode;

import com.cenes.R;
import com.cenes.coremanager.CoreManager;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import io.fabric.sdk.android.Fabric;

/**
 * Created by puneet on 11/8/17.
 */

public class CenesApplication extends Application {

    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    CoreManager coreManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        coreManager = new CoreManager(this);
        sAnalytics = GoogleAnalytics.getInstance(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}
