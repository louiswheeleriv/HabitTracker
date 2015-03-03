package com.hci.habittracker;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

public class Habit {

	HabitType habitType;
	Map<Date, Integer> progress;
	
	public Habit(HabitType habitType){
		this.habitType = habitType;
		this.progress = new HashMap<Date, Integer>();
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
