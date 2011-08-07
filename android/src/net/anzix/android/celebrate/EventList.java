package net.anzix.android.celebrate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EventList extends ListActivity {
	private static final int ACTIVITY_EDIT = 1;
	private EventAdapter db;
	protected String currentName;
	private static final int INSERT_ID = Menu.FIRST;

	private static final int DATE_DIALOG_ID = 0;
	private static final int NAME_DIALOG_ID = 1;
	private static final int DELETE_ALL = 2;
	private static final int DELETE_ID = 3;
	private static final int EXPORT_ID = 4;
	private static final int IMPORT_ID = 5;
	private File outputFile;

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

		String[] from = new String[] { EventHelper.KEY_NAME, EventHelper.KEY_DATE };
		int[] to = new int[] { R.id.name, R.id.date };

		SimpleCursorAdapter events = new SimpleCursorAdapter(this, R.layout.event_item, c, from, to);
		setListAdapter(events);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, "Add new");
		menu.add(0, DELETE_ALL, 0, "Delete all");
		menu.add(0, EXPORT_ID, 0, "Export");
		menu.add(0, IMPORT_ID, 0, "Import");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String message;
		switch (item.getItemId()) {
		case INSERT_ID:
			// Intent i = new Intent(this, EventForm.class);
			// startActivityForResult(i, ACTIVITY_CREATE);
			showDialog(NAME_DIALOG_ID);

			return true;

		case DELETE_ALL:
			db.deleteAll();
			fillData();
			return true;
		case IMPORT_ID:
			File inputFile = new File(Environment.getExternalStorageDirectory(), "celebrate/export.csv");
			if (!inputFile.exists()) {
				Toast.makeText(this, "No such file " + inputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				return true;
			}
			try {
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(";");
					if (parts.length != 2) {
						message = "Invalid file format";
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						sdf.parse(parts[1]);
						db.createNote(parts[0], parts[1]);
					}
				}
				reader.close();
				fillData();
				message = "Events are imported succesfully";
			} catch (Exception e) {
				message = "Export is failed. " + e.getMessage();
				Log.e("celebrate", "Error on exporting", e);
			}
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

			return true;
		case EXPORT_ID:
			Cursor c = db.fetchAllEvent();
			startManagingCursor(c);
			c.moveToFirst();
			try {
				File outputFile = new File(Environment.getExternalStorageDirectory(), "celebrate/export.csv");
				if (!outputFile.getParentFile().exists()) {
					outputFile.getParentFile().mkdirs();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

				do {
					bw.write(c.getString(c.getColumnIndex(EventHelper.KEY_NAME)));
					bw.write(";");
					bw.write(c.getString(c.getColumnIndex(EventHelper.KEY_DATE)));
					bw.write("\n");
					if (!c.isLast()) {
						c.moveToNext();
					}

				} while (!c.isLast());
				bw.close();
				message = "Events are exported to " + outputFile.getAbsolutePath();
			} catch (IOException e) {
				message = "Export is failed. " + e.getMessage();
				Log.e("celebrate", "Error on exporting", e);
			}
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					saveNewEvent(currentName, year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
					currentName = "";
				}

			};
			return new DatePickerDialog(this, mDateSetListener, 2000, 01, 01);
		case NAME_DIALOG_ID:
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			alert.setView(input);
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					currentName = input.getText().toString().trim();
					showDialog(DATE_DIALOG_ID);

				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.cancel();
					currentName = "";
				}
			});
			return alert.create();
		}
		return null;
	}

	private void saveNewEvent(String currentName, String string) {
		db.createNote(currentName, string);
		fillData();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
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
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			Log.e("CEL", "" + info.id);
			db.deleteNote(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

}