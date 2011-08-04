package net.anzix.android.celebrate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anzix.android.celebrate.api.CelebrateFinder;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class EventView extends ListActivity {
	private Long mRowId;
	private EventAdapter db;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_detail_list);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(EventHelper.KEY_ROWID)
					: null;
		}
		db = new EventAdapter(this);
		db.open();

		Cursor c = db.fetchNote(mRowId);

		c.moveToFirst();

		try {
			String name = c.getString(c.getColumnIndex(EventHelper.KEY_NAME));
			Date date = sdf.parse(c.getString(c
					.getColumnIndex(EventHelper.KEY_DATE)));

			String[] from = new String[] { "text", "comment" };
			int[] to = new int[] { R.id.text, R.id.comment };

			Date now = new Date();
			List<Map<String, String>> values = new ArrayList<Map<String, String>>();
			addText(values, name, "Name");
			addText(values,sdf.format(date),"Date");
			float monthNo = CelebrateFinder.getMonthNo(date, now);
			addText(values, ""+monthNo / 12, "Age in years");
			addText(values, ""+monthNo, "Age in months");
			addText(values, ""+CelebrateFinder.getDayNo(date, now), "Age in days");

			SimpleAdapter events = new SimpleAdapter(this, values,
					R.layout.event_detail_item, from, to);
			setListAdapter(events);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void addText(List<Map<String, String>> values, String name,
			String string) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("text", name);
		m.put("comment", string);
		values.add(m);

	}
}
