package net.anzix.android.celebrate;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EventForm extends Activity {
	private EditText nameField;
	private EditText dateField;
	private Long mRowId;
	private EventAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new EventAdapter(this);
		db.open();

		setContentView(R.layout.event_edit);
		setTitle("Edit");

		nameField = (EditText) findViewById(R.id.name_field);
		dateField = (EditText) findViewById(R.id.date_field);

		Button confirmButton = (Button) findViewById(R.id.confirm);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(EventHelper.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(EventHelper.KEY_ROWID)
					: null;
		}

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});
	}

	private void populateFields() {
		if (mRowId != null) {
			Cursor note = db.fetchNote(mRowId);
			startManagingCursor(note);
			nameField.setText(note.getString(note
					.getColumnIndexOrThrow(EventHelper.KEY_NAME)));
			dateField.setText(note.getString(note
					.getColumnIndexOrThrow(EventHelper.KEY_DATE)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(EventHelper.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String title = nameField.getText().toString();
		String body = dateField.getText().toString();

		if (mRowId == null) {
			long id = db.createNote(title, body);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			 db.updateNote(mRowId, title, body);
		}
	}
}
