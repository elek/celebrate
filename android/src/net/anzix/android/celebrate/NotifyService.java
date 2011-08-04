package net.anzix.android.celebrate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.anzix.android.celebrate.api.Celebrate;
import net.anzix.android.celebrate.api.CelebrateFinder;
import net.anzix.android.celebrate.api.Event;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

/**
 * Update widget, and send notifications.
 * 
 * @author elek
 */
public class NotifyService extends Service {
	private List<Event> list = new ArrayList<Event>();
	private EventAdapter db;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		db = new EventAdapter(this);
		db.open();
		notificationService();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void notificationService() {
		Log.e("celebrate", "notify");
		Cursor c = db.fetchAllEvent();
		list.clear();
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				String name = c.getString(c.getColumnIndex(EventHelper.KEY_NAME));
				Date date = null;
				String d = c.getString(c.getColumnIndex(EventHelper.KEY_DATE));
				long id = c.getLong(c.getColumnIndex(EventHelper.KEY_ROWID));
				try {
					date = sdf.parse(d);
				} catch (ParseException e) {
					Log.e("", "Error on parsing date" + d);
					e.printStackTrace();
				}
				list.add(new Event(id, name, date));
				c.moveToNext();
			}
			c.deactivate();
		}
		List<Celebrate> findFeasts = CelebrateFinder.findFeasts(list);
		List<Celebrate> todayFeasts = new ArrayList<Celebrate>();
		Date d = new Date();

		for (Celebrate cel : findFeasts) {
			Log.e("celebrate", "notify" + cel.getDate());
			Log.e("celebrate", "notify" + d);
			Date cd = cel.getDate();
			if (cd.getYear() == d.getYear() && cd.getMonth() == d.getMonth() && cd.getDay() == d.getDay()) {
				todayFeasts.add(cel);
			}
		}
		if (todayFeasts.size() > 0) {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

			CharSequence tickerText = "Celebrate";

			Notification notification = new Notification(R.drawable.info, tickerText, System.currentTimeMillis());

			notification.defaults |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			CharSequence contentTitle = "Celebrate";
			CharSequence contentText = "You can celebrate " + todayFeasts.size() + " events!";

			Intent notificationIntent = new Intent(this, CelebrateList.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
			mNotificationManager.notify(1, notification);

		}

	}
}
