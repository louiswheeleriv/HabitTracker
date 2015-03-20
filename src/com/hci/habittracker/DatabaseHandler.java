package com.hci.habittracker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "habitTrackerDB";
	
	private static final String TABLE_HABITTYPES = "habitTypes";
	private static final String TABLE_HABITS = "habits";
	private static final String TABLE_GOALS = "goals";
	
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	
	private static final String KEY_HABITTYPE_ID = "habitType_id";
	private static final String KEY_DATE = "date";
	private static final String KEY_VALUE = "value";

	private static final String KEY_GOAL = "goal";
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("ALERT", "DatabaseHandler.onCreate()");
		
		String CREATE_HABITTYPES_TABLE = "CREATE TABLE " + TABLE_HABITTYPES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_GOAL + " INTEGER" + ")";
		
		String CREATE_HABITS_TABLE = "CREATE TABLE " + TABLE_HABITS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_HABITTYPE_ID + " INTEGER NOT NULL,"
				+ KEY_DATE + " DATE NOT NULL," + KEY_VALUE + " INTEGER NOT NULL" + ")";
		
		String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_GOALS + "("
				+ KEY_HABITTYPE_ID + " INTEGER PRIMARY KEY," + KEY_GOAL + " INTEGER" + ")";
		
		db.execSQL(CREATE_HABITTYPES_TABLE);
		db.execSQL(CREATE_HABITS_TABLE);
		db.execSQL(CREATE_GOALS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITTYPES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
		onCreate(db);
	}
	
	/*
	 * All CRUD Operations (Create, Read, Update, Delete)
	 */
	
	/*
	 * Functions for HabitType
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
	
	public int deleteHabitType(HabitType habitType){
		SQLiteDatabase db = this.getWritableDatabase();
		int numAffected = db.delete(TABLE_HABITTYPES, KEY_ID + " = ?", new String[] {String.valueOf(habitType.getId())});
		db.close();
		return numAffected;
	}
	
	public int deleteHabitTypeByName(String name){
		SQLiteDatabase db = this.getWritableDatabase();
		int numAffected = db.delete(TABLE_HABITTYPES, KEY_NAME + " = ?", new String[] {name});
		db.close();
		return numAffected;
	}
	
	public void deleteAllHabitTypes(){
		List<HabitType> habitTypeList = getAllHabitTypes();
		for(HabitType ht : habitTypeList){
			deleteHabitType(ht);
		}
	}
	
	/*
	 * Functions for Habit
	 */
	
	public void addHabitDataForDate(String habitTypeName, Date date, int value){
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor typeCursor = db.query(TABLE_HABITTYPES, new String[] {KEY_ID, KEY_NAME}, KEY_NAME + " = ?",
				new String[] {habitTypeName}, null, null, null, null);
		
		typeCursor.moveToFirst();
		int habitTypeId = typeCursor.getInt(0);
		
		// First clear any data for that habit type and date
		int year = date.getYear() + 1900;
		int month = date.getMonth();
		int day = date.getDate();
		String dateString = year + "-" + month + "-" + day;
		db.delete(TABLE_HABITS, KEY_HABITTYPE_ID + " = ? AND " + KEY_DATE + " = ?", new String[] {String.valueOf(habitTypeId), dateString});
		
		ContentValues values = new ContentValues();
		values.put(KEY_HABITTYPE_ID, habitTypeId);
		
		values.put(KEY_DATE, dateString);
		values.put(KEY_VALUE, value);
		
		db.insert(TABLE_HABITS, null, values);
		db.close();		
	}
	
	public Habit getHabit(HabitType habitType){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_HABITS, new String[] {KEY_ID, KEY_HABITTYPE_ID, KEY_DATE, KEY_VALUE}, KEY_HABITTYPE_ID + "=?",
				new String[] {String.valueOf(habitType.getId())}, null, null, null, null);
		
		Map<Date, Integer> progress = new HashMap<Date, Integer>();
		DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
		
		do {
			String dateString = cursor.getString(2);
		    Date date;
			try {
				date = df.parse(dateString);
				progress.put(date, cursor.getInt(3));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} while (cursor.moveToNext());
		
		Habit habit = new Habit(habitType, progress);
		
		Log.d("HABIT INFO", "HABIT INFO");
		for(Date dt : progress.keySet()){
			Log.d(dt.toString(), progress.get(dt).toString());
		}
		
		return habit;
	}
	
	public int getHabitDataForDate(String habitTypeName, Date date){
		SQLiteDatabase db = this.getReadableDatabase();
		int value = -1;
		
		Cursor typeCursor = db.query(TABLE_HABITTYPES, new String[] {KEY_ID, KEY_NAME}, KEY_NAME + " = ?",
				new String[] {habitTypeName}, null, null, null, null);
		typeCursor.moveToFirst();
		int habitTypeId = typeCursor.getInt(0);
		
		int year = date.getYear() + 1900;
		int month = date.getMonth();
		int day = date.getDate();
		String dateString = year + "-" + month + "-" + day;
		
		Cursor cursor = db.query(TABLE_HABITS, new String[] {KEY_ID, KEY_HABITTYPE_ID, KEY_DATE, KEY_VALUE},
				KEY_HABITTYPE_ID + " = ? " + "AND " + KEY_DATE + " = ?",
				new String[] {String.valueOf(habitTypeId), dateString}, null, null, null, null);
		
		Log.d("ALERT", "Query: WHERE habitTypeId = " + habitTypeId + " AND date = " + dateString);
		
		try{
			cursor.moveToFirst();
			value = cursor.getInt(3);
			
			Log.d("ALERT", "habitTypeId: " + habitTypeId);
			Log.d("ALERT", "Date: " + date.toString());
			Log.d("ALERT", "Value: " + value);
			
		} catch (Exception e){
			Log.d("ERROR", e.getMessage());
		}
		
		Log.d("DATE DATA", String.valueOf(value));
		
		return value;
	}
	
	public int deleteHabitData(HabitType habitType){
		SQLiteDatabase db = this.getWritableDatabase();
		int numAffected = db.delete(TABLE_HABITS, KEY_HABITTYPE_ID + " = ?", new String[] {String.valueOf(habitType.getId())});
		db.close();
		return numAffected;
	}
	
	public void generateHabitData(String habitTypeName){
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor typeCursor = db.query(TABLE_HABITTYPES, new String[] {KEY_ID, KEY_NAME}, KEY_NAME + "=?", 
				new String[] {habitTypeName}, null, null, null, null);
		
		if(typeCursor != null){
			typeCursor.moveToFirst();
			int habitTypeId = typeCursor.getInt(0);
			
			Log.d("ALERT", "habitTypeId = " + habitTypeId);
			
			Map<Date, Integer> progress = new HashMap<Date, Integer>();
			Calendar cal = Calendar.getInstance();
			Date today = new Date();
			cal.setTime(today);

			progress.put(cal.getTime(), 3);
			cal.add(Calendar.DATE, -1);
			progress.put(cal.getTime(), 2);
			cal.add(Calendar.DATE, -1);
			progress.put(cal.getTime(), 1);
			
			for(Date dt : progress.keySet()){
				Log.d("ALERT", "Date: " + dt.toString());
				Log.d("ALERT", "Value: " + progress.get(dt));
			}
			
			Habit habit = new Habit(new HabitType(habitTypeId, habitTypeName), progress);
			addHabitDataForDate(habit.getHabitType().getName(), cal.getTime(), habit.getProgress().get(cal.getTime()));
			cal.add(Calendar.DATE, 1);
			addHabitDataForDate(habit.getHabitType().getName(), cal.getTime(), habit.getProgress().get(cal.getTime()));
			cal.add(Calendar.DATE, 1);
			addHabitDataForDate(habit.getHabitType().getName(), cal.getTime(), habit.getProgress().get(cal.getTime()));
			
		}else{
			return;
		}
	}
	
	public int deleteHabitDataByType(HabitType habitType){
		SQLiteDatabase db = this.getWritableDatabase();
		int numAffected = db.delete(TABLE_HABITS, KEY_HABITTYPE_ID + " = ?", new String[] {String.valueOf(habitType.getId())});
		db.close();
		return numAffected;
	}
	
	public void deleteAllHabitData(){
		List<HabitType> habitTypeList = getAllHabitTypes();
		for(HabitType ht : habitTypeList){
			deleteHabitDataByType(ht);
		}
	}
	
	public void setGoalForHabit(String habitTypeName, int goalValue){
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor typeCursor = db.query(TABLE_HABITTYPES, new String[] {KEY_ID, KEY_NAME}, KEY_NAME + " = ?",
				new String[] {habitTypeName}, null, null, null, null);
		
		typeCursor.moveToFirst();
		int habitTypeId = typeCursor.getInt(0);
		
		db.delete(TABLE_GOALS, KEY_HABITTYPE_ID + " = ?", new String[] {String.valueOf(habitTypeId)});
		ContentValues values = new ContentValues();
		values.put(KEY_HABITTYPE_ID, habitTypeId);
		values.put(KEY_GOAL, goalValue);
		db.insert(TABLE_GOALS, null, values);
		db.close();
	}
	
	public int getGoalForHabit(String habitTypeName){
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor typeCursor = db.query(TABLE_HABITTYPES, new String[] {KEY_ID, KEY_NAME}, KEY_NAME + " = ?",
				new String[] {habitTypeName}, null, null, null, null);
		
		typeCursor.moveToFirst();
		int habitTypeId = typeCursor.getInt(0);
		
		Cursor cursor = db.query(TABLE_GOALS, new String[] {KEY_HABITTYPE_ID, KEY_GOAL}, KEY_HABITTYPE_ID + " = ?",
				new String[] {String.valueOf(habitTypeId)}, null, null, null, null);
		cursor.moveToFirst();
		try{
			return cursor.getInt(1);
		}catch(Exception e){
			return -1;
		}
		
	}
	
	public void deleteAllData(){
		deleteAllHabitData();
		deleteAllHabitTypes();
	}
	
	public void createGoalsTable(){
		SQLiteDatabase db = this.getWritableDatabase();
		String CREATE_GOALS_TABLE = "CREATE TABLE " + TABLE_GOALS + "("
				+ KEY_HABITTYPE_ID + " INTEGER PRIMARY KEY," + KEY_GOAL + " INTEGER" + ")";
		
		db.execSQL(CREATE_GOALS_TABLE);
	}
	
}
