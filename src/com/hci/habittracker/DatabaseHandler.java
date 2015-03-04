package com.hci.habittracker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "habitTrackerDB";
	
	private static final String TABLE_HABITTYPES = "habitTypes";
	private static final String TABLE_HABITS = "habits";
	
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_HABITTYPES_TABLE = "CREATE TABLE " + TABLE_HABITTYPES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
		db.execSQL(CREATE_HABITTYPES_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITTYPES);
		onCreate(db);
	}
	
	/*
	 * All CRUD Operations (Create, Read, Update, Delete)
	 */
	
	public void addHabitType(HabitType habitType){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, habitType.getName());
		
		db.insert(TABLE_HABITTYPES, null, values);
		db.close();		
	}
	
	public HabitType getHabitType(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_HABITTYPES, new String[] {KEY_ID, KEY_NAME}, KEY_ID + "=?",
				new String[] {String.valueOf(id)}, null, null, null, null);
		
		if(cursor != null)
			cursor.moveToFirst();
		
		String name = cursor.getString(1);
		
		HabitType habitType = new HabitType(Integer.parseInt(cursor.getString(0)), name);
		return habitType;
	}
	
	public List<HabitType> getAllHabitTypes(){
		List<HabitType> habitTypeList = new ArrayList<HabitType>();
		
		String selectQuery = "SELECT * FROM " + TABLE_HABITTYPES;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()){
			do{
				HabitType habitType = new HabitType();
				habitType.setId(Integer.parseInt(cursor.getString(0)));
				habitType.setName(cursor.getString(1));
				habitTypeList.add(habitType);
			}while(cursor.moveToNext());
		}
		
		return habitTypeList;
	}
	
	public int getHabitTypesCount(){
		String countQuery = "SELECT * FROM " + TABLE_HABITTYPES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		
		return cursor.getCount();
	}
	
	public int updateHabitType(HabitType habitType){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, habitType.getName());
		
		return db.update(TABLE_HABITTYPES, values, KEY_ID + " = ?",
				new String[] {String.valueOf(habitType.getId())});
	}
	
	public void deleteHabitType(HabitType habitType){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_HABITTYPES, KEY_ID + " = ?", new String[] {String.valueOf(habitType.getId())});
		db.close();
	}
	
	public void deleteAllHabitTypes(){
		List<HabitType> habitTypeList = getAllHabitTypes();
		for(HabitType ht : habitTypeList){
			deleteHabitType(ht);
		}
	}
	
}
