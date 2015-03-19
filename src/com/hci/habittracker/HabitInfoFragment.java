package com.hci.habittracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class HabitInfoFragment extends Fragment{
	
	DatabaseHandler db;
	String selectedHabitTypeName = "";
	Date dateSelected = new Date();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootview = inflater.inflate(R.layout.fragment_habit_info, container, false);
		
		db = new DatabaseHandler(getActivity());
		
		Bundle args = getArguments();
		if (args != null && args.containsKey("HabitType")){
			selectedHabitTypeName = args.getString("HabitType");
		}
		
		TextView textView_habitName = (TextView) rootview.findViewById(R.id.textView_habitName);
		textView_habitName.setText(selectedHabitTypeName);
		
		Button button_deleteHabitType = (Button) rootview.findViewById(R.id.button_deleteHabitType);
		button_deleteHabitType.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				deleteThisHabitType();
			}
		});
		
		Button button_generateData = (Button) rootview.findViewById(R.id.button_generateData);
		button_generateData.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				generateData();
				showSelectedDateData();
			}
		});
		
		final TextView textView_habitData_dateValue = (TextView) rootview.findViewById(R.id.textView_habitData_dateValue);
		final DatePicker datePicker_habitData = (DatePicker) rootview.findViewById(R.id.datePicker_habitData);
		Calendar c = Calendar.getInstance();
		datePicker_habitData.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				Date chosenDate = new Date();
				chosenDate.setDate(dayOfMonth);
				chosenDate.setMonth(monthOfYear);
				chosenDate.setYear(year-1900);
				
				dateSelected = chosenDate;
				
				showSelectedDateData();
				
			}
		});
		
		final EditText editText_editHabitData = (EditText) rootview.findViewById(R.id.editText_editHabitData);
		Button button_editHabitData = (Button) rootview.findViewById(R.id.button_editHabitData);
		button_editHabitData.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String valueEnteredString = editText_editHabitData.getText().toString();
				int valueEntered = Integer.valueOf(valueEnteredString);
				setHabitDataForDate(valueEntered);
				editText_editHabitData.setText("");
				
				showSelectedDateData();
			}
		});
		
		// Ensure that the input area remains on screen when the keyboard opens
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		//showSelectedDateData();
		
		return rootview;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		showSelectedDateData();
	}
	
	public void showSelectedDateData(){
		TextView textView_habitData_dateValue = (TextView) getActivity().findViewById(R.id.textView_habitData_dateValue);
		
		int valueToSet = getHabitDataForDate(dateSelected);
		if(valueToSet >= 0){
			textView_habitData_dateValue.setText(String.valueOf(valueToSet));
		}else{
			textView_habitData_dateValue.setText("None");
		}
	}
	
	public void deleteThisHabitType(){
		db.deleteHabitTypeByName(selectedHabitTypeName);
        getFragmentManager().beginTransaction().replace(R.id.container, new HabitsFragment()).commit();
	}
	
	public void generateData(){
		db.generateHabitData(selectedHabitTypeName);
	}
	
	public void setHabitDataForDate(int value){
		db.addHabitDataForDate(selectedHabitTypeName, dateSelected, value);
	}
	
	public int getHabitDataForDate(Date date){
		return db.getHabitDataForDate(selectedHabitTypeName, date);
	}
	
}
