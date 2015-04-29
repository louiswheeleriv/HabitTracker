package com.hci.habittracker;

public class HabitType {

	private int id;
	private String name;
	private boolean goodHabit = true;
	
	public HabitType(){
		
	}
	
	public HabitType(int id, String name, boolean goodHabit){
		this.id = id;
		this.name = name;
		this.goodHabit = goodHabit;
	}
	
	public HabitType(String name, boolean goodHabit){
		this.name = name;
		this.goodHabit = goodHabit;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public boolean isGoodHabit(){
		return goodHabit;
	}
	
	public void setGoodHabit(boolean goodHabit){
		this.goodHabit = goodHabit;
	}
	
}
