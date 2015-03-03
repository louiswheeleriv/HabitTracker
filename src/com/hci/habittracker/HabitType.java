package com.hci.habittracker;

public class HabitType {

	String name;
	boolean isGoodHabit;
	
	public HabitType(String name, boolean isGoodHabit){
		this.name = name;
		this.isGoodHabit = isGoodHabit;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public boolean isGoodHabit(){
		return isGoodHabit;
	}
	
	public void setIsGoodHabit(boolean isGoodHabit){
		this.isGoodHabit = isGoodHabit;
	}
	
}
