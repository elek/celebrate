package net.anzix.android.celebrate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventHelper extends SQLiteOpenHelper {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_DATE = "date";

	private static final String TAG = "celebrate.eventhelper";
	private static final String DATABASE_NAME = "data";
	public  static final String DATABASE_TABLE = "events";
	private static final int DATABASE_VERSION = 2;
	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (_id integer primary key autoincrement, "
			+ "name text not null, date date not null);";

	EventHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);
	}
}
