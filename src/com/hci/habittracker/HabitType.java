package com.hci.habittracker;

public class HabitType {

	private int id;
	private String name;
	
	public HabitType(){
		
	}
	
	public HabitType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public HabitType(String name){
		this.name = name;
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
	
}
