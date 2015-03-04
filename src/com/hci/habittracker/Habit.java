package com.hci.habittracker;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

public class Habit {

	private int id;
	private HabitType habitType;
	private Map<Date, Integer> progress;
	
	public Habit(HabitType habitType){
		this.habitType = habitType;
		this.progress = new HashMap<Date, Integer>();
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public HabitType getHabitType(){
		return habitType;
	}
	
	public void setHabitType(HabitType habitType){
		this.habitType = habitType;
	}
	
	public Map<Date, Integer> getProgress(){
		return progress;
	}
	
	public void setProgress(Map<Date, Integer> progress){
		this.progress = progress;
	}
	
}
