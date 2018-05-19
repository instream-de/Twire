package com.sebastianrask.bettersubscription;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;


/**
 * Created by SebastianRask on 20-02-2016.
 */
public class PocketPlaysApplication extends MultiDexApplication {
	private Tracker mTracker;
	public static boolean isCrawlerUpdate = false; //ToDo remember to disable for crawler updates

	@Override
	public void onCreate() {
		super.onCreate();
		initNotificationChannels();

		if (!BuildConfig.DEBUG) {
			try {
				Fabric.with(this, new Crashlytics());

				final Fabric fabric = new Fabric.Builder(this)
						.kits(new Crashlytics())
						.debuggable(true)
						.build();
				Fabric.with(fabric);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	/**
	 * Gets the default {@link Tracker} for this {@link Application}.
	 * @return tracker
	 */
	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mTracker = analytics.newTracker(R.xml.global_tracker);
			mTracker.enableAdvertisingIdCollection(true);
		}

		return mTracker;
	}

	private void initNotificationChannels() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || notificationManager == null) {
			return;
		}

		notificationManager.createNotificationChannel(
				new NotificationChannel(getString(R.string.live_streamer_notification_id), "New Streamer is live", NotificationManager.IMPORTANCE_DEFAULT)
		);

		notificationManager.createNotificationChannel(
				new NotificationChannel(getString(R.string.stream_cast_notification_id), "Stream Playback Control", NotificationManager.IMPORTANCE_DEFAULT)
		);
	}
}