package net.anzix.android.celebrate;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class EventList extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private EventAdapter db;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		db = new EventAdapter(this);
		db.open();
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		// Get all of the notes from the database and create the item list
		Cursor c = db.fetchAllEvent();
		startManagingCursor(c);

		String[] from = new String[] { EventHelper.KEY_NAME,
				EventHelper.KEY_DATE };
		int[] to = new int[] { R.id.name, R.id.date };

		SimpleCursorAdapter events = new SimpleCursorAdapter(this,
				R.layout.event_item, c, from, to);
		setListAdapter(events);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, "Add new");
		menu.add(0, DELETE_ID, 0, "Delete all");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			Intent i = new Intent(this, EventForm.class);
			startActivityForResult(i, ACTIVITY_CREATE);
			return true;

		case DELETE_ID:
			db.deleteAll();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, "Delete");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, EventView.class);
		i.putExtra(EventHelper.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			db.deleteNote(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

}