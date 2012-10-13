package csci498.cahurley.lunchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "lunchlist.db";
	private static final int SCHEMA_VERSION = 3;
	
	private static final String CREATE_DB_SCHEMA = "CREATE TABLE restaurants (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, address TEXT, type TEXT, notes TEXT, feed TEXT, lat REAL, long REAL);";
	private static final String UPDATE_DB_SCHEMA_1_2 = "ALTER TABLE restaurants ADD COLUMN feed TEXT";
	private static final String UPDATE_DB_SCHEMA_2_3_lat = "ALTER TABLE restaurants ADD COLUMN lat REAL";
	private static final String UPDATE_DB_SCHEMA_2_3_long = "ALTER TABLE restaurants ADD COLUMN long REAL";
	private static final String GET_ALL_FROM_DB = "SELECT _id, name, address, type, notes, feed, lat, long FROM restaurants ORDER BY ";
	private static final String GET_BY_ID_FROM_DB = "SELECT _id, name, address, type, notes, feed, lat, long FROM restaurants WHERE _ID = ?";
	
	public RestaurantHelper(Context context)
	{
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_DB_SCHEMA);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (oldVersion < 2)
		{
			db.execSQL(UPDATE_DB_SCHEMA_1_2);
		}
		
		if (oldVersion < 3)
		{
			db.execSQL(UPDATE_DB_SCHEMA_2_3_lat);
			db.execSQL(UPDATE_DB_SCHEMA_2_3_long);
		}
	}
	
	public Cursor getAll(String orderBy)
	{
		return(getReadableDatabase().rawQuery(GET_ALL_FROM_DB + orderBy, null));
	}
	
	public Cursor getById(String id)
	{
		String[] args = {id};
		
		return(getReadableDatabase().rawQuery(GET_BY_ID_FROM_DB, args));
	}
	
	public void insert(String name, String address, String type, String notes, String feed)
	{
		ContentValues cv = new ContentValues();
		
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type);
		cv.put("notes", notes);
		cv.put("feed", feed);
		
		getWritableDatabase().insert("restaurants", "name", cv);
	}
	
	public void update(String id, String name, String address, String type, String notes, String feed)
	{
		ContentValues cv = new ContentValues();
		String[] args = {id};
		
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type);
		cv.put("notes", notes);
		cv.put("feed", feed);
		
		getWritableDatabase().update("restaurants", cv, "_ID=?", args);
	}
	
	public void updateLocation(String id, double lat, double lon)
	{
		ContentValues cv = new ContentValues();
		String[] args = {id};
		
		cv.put("lat", lat);
		cv.put("long", lon);
		
		getWritableDatabase().update("restaurants", cv, "_ID=?", args);
	}
	
	public String getName(Cursor c)
	{
		return c.getString(1);
	}
	
	public String getAddress(Cursor c)
	{
		return c.getString(2);
	}
	
	public String getType(Cursor c)
	{
		return c.getString(3);
	}
	
	public String getNotes(Cursor c)
	{
		return c.getString(4);
	}
	
	public String getFeed(Cursor c)
	{
		return c.getString(5);
	}
	
	public double getLatitude(Cursor c)
	{
		return c.getDouble(6);
	}
	
	public double getLongitude(Cursor c)
	{
		return c.getDouble(7);
	}
	
}
