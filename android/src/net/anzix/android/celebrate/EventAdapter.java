package net.anzix.android.celebrate;

import static net.anzix.android.celebrate.EventHelper.DATABASE_TABLE;
import static net.anzix.android.celebrate.EventHelper.KEY_DATE;
import static net.anzix.android.celebrate.EventHelper.KEY_NAME;
import static net.anzix.android.celebrate.EventHelper.KEY_ROWID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EventAdapter {
	private SQLiteDatabase db;
	private Context context;

	public EventAdapter(Context context) {
		super();
		this.context = context;
	}

	public EventAdapter open() throws SQLException {
		EventHelper helper = new EventHelper(context);
		db = helper.getWritableDatabase();
		return this;
	}

	public long createNote(String name, String date) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(EventHelper.KEY_NAME, name);
		initialValues.put(EventHelper.KEY_DATE, date.toString());

		return db.insert(EventHelper.DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteNote(long rowId) {
		return db.delete(EventHelper.DATABASE_TABLE, EventHelper.KEY_ROWID
				+ "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllEvent() {
		return db.query(EventHelper.DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_DATE }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchNote(long rowId) throws SQLException {

		Cursor mCursor =

		db.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_NAME,
				KEY_DATE }, KEY_ROWID + "=" + rowId, null, null, null, null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateNote(long rowId, String name, String date) {
		ContentValues args = new ContentValues();
		args.put(EventHelper.KEY_NAME, name);
		args.put(EventHelper.KEY_DATE, date);
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public void deleteAll() {
		db.delete(DATABASE_TABLE, null, null);		
	}

}
