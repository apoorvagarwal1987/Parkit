package com.applications.googlemapapi;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBTools extends SQLiteOpenHelper{
	@SuppressWarnings("unused")
	private final static String TAG = "DB Tools";

	public DBTools(Context applicationContext) {
        
        // Call use the database or to create it	         
        super(applicationContext, "carparking.db", null, 1);	         
    }

	@Override
	public void onCreate(SQLiteDatabase database) {
		 String query = "CREATE TABLE parkinginfo ( parkingId INTEGER PRIMARY KEY, latitude REAL, " +
	                "longitude REAL, parkingtime INTEGER)";  
		 // Executes the query provided as long as the query isn't a select
		 // or if the query doesn't return any data         
		 database.execSQL(query);	
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		String query = "DROP TABLE IF EXISTS parkinginfo";		        
        // Executes the query provided as long as the query isn't a select
        // or if the query doesn't return any data
		database.execSQL(query);
		onCreate(database);
		
	}

	public void insertParkingInfo(ParkInformation parkInformation) {
        
        // Open a database for reading and writing         
        SQLiteDatabase database = this.getWritableDatabase();
         
        // Stores key value pairs being the column name and the data
        // ContentValues data type is needed because the database
        // requires its data type to be passed         
        ContentValues values = new ContentValues();         
        values.put("latitude", parkInformation.getLatitude());
        values.put("longitude", parkInformation.getLongitude());
        values.put("parkingtime", parkInformation.getParkingtime()); 
        // Inserts the data in the form of ContentValues into the
        // table name provided         
        database.insert("parkinginfo", null, values);
         
        // Release the reference to the SQLiteDatabase object         
        database.close();
    }	
	
	public ArrayList<ParkInformation> getParkingInfo(int queryDay) {	
         
        // Open a database for reading only
		ArrayList<ParkInformation> parkingHistory = new ArrayList<ParkInformation>();
		int day;
		Calendar cal = Calendar.getInstance();
        SQLiteDatabase database = this.getReadableDatabase();
         
        String selectQuery = "SELECT * FROM parkinginfo";
         
        // rawQuery executes the query and returns the result as a Cursor         
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do { 
            	cal.setTimeInMillis(cursor.getLong(3));
            	day = cal.get(Calendar.DAY_OF_WEEK);
            	if (queryDay==day){
	            	ParkInformation parkInformation = new ParkInformation();
	            	parkInformation.setLatitude(cursor.getDouble(1));
	            	parkInformation.setLongitude(cursor.getDouble(2));
	            	parkInformation.setParkingtime(cursor.getLong(3));
	            	parkingHistory.add(parkInformation);
            	}
            } while (cursor.moveToNext());
        }                  
    return parkingHistory;
    }  
	
	public ArrayList<ParkInformation> getLatestParkingLocation(int result) {	
        
        // Open a database for reading only
		ArrayList<ParkInformation> parkingHistory = new ArrayList<ParkInformation>();
		
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM parkinginfo order by parkingtime DESC LIMIT " + result;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
	        do {            	
	        	ParkInformation parkInformation = new ParkInformation();
	        	parkInformation.setLatitude(cursor.getDouble(1));
	        	parkInformation.setLongitude(cursor.getDouble(2));
	        	parkInformation.setParkingtime(cursor.getLong(3));
	        	parkingHistory.add(parkInformation);
	        } while (cursor.moveToNext());
        }
    return parkingHistory;
    }  
}
