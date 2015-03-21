package com.hci.habittracker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
		
		// Habit name at top
		TextView textView_habitName = (TextView) rootview.findViewById(R.id.textView_habitName);
		textView_habitName.setText(selectedHabitTypeName);
		
		// Control for delete button
		Button button_deleteHabitType = (Button) rootview.findViewById(R.id.button_deleteHabitType);
		button_deleteHabitType.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				deleteThisHabitType();
			}
		});
		
		// Control for generate data button
		Button button_generateData = (Button) rootview.findViewById(R.id.button_generateData);
		button_generateData.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				generateData();
				showSelectedDateData();
			}
		});
		
		// Control for DatePicker
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
		
		// Edit value for date
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
		
		// Edit goal
		final EditText editText_editGoal = (EditText) rootview.findViewById(R.id.editText_editGoal);
		Button button_editGoal = (Button) rootview.findViewById(R.id.button_editGoal);
		button_editGoal.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String valueEnteredString = editText_editGoal.getText().toString();
				int valueEntered = Integer.valueOf(valueEnteredString);
				setGoalForHabit(valueEntered);
				editText_editGoal.setText("");
			}
		});
		
		Map<Date, Integer> progress = getHabitProgress();
		
		if(progress.size() > 0){
			
			DataPoint[] dataPoints = new DataPoint[progress.size()];
			List<Date> dates = new ArrayList<Date>();
			dates.addAll(progress.keySet());
			Collections.sort(dates);
			
			int count = 0;
			Date minDate = new Date();
			
			for(Date dt : dates){
				dataPoints[count] = new DataPoint(dt, progress.get(dt));
				count++;
				
				Log.d("ALERT", "Date = [" + dt + "], value = " + progress.get(dt));
				
				if(minDate.after(dt)){
					minDate = dt;
				}
			}
			
			// Graph
			GraphView graph = (GraphView) rootview.findViewById(R.id.graph);
			TextView textView_noProgressData = (TextView) rootview.findViewById(R.id.textView_noProgressData);
			textView_noProgressData.setVisibility(View.GONE);
			graph.setVisibility(View.VISIBLE);
			
			LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
			graph.addSeries(series);
			
			int goal = getGoalForHabit();
			if(goal >= 0){
				DataPoint[] goalDataPoints = new DataPoint[2];
				goalDataPoints[0] = new DataPoint(minDate, goal);
				goalDataPoints[1] = new DataPoint(new Date(), goal);
				
				LineGraphSeries<DataPoint> goalSeries = new LineGraphSeries<DataPoint>(goalDataPoints);
				goalSeries.setColor(Color.RED);
				graph.addSeries(goalSeries);
			}
			
			// set date label formatter
			graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
			graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
			
			// set manual x bounds to have nice steps
			graph.getViewport().setMinX(minDate.getTime());
			graph.getViewport().setMaxX(new Date().getTime());
			graph.getViewport().setXAxisBoundsManual(true);
			
		}
		
		// Ensure that the input area remains on screen when the keyboard opens
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		return rootview;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		showSelectedDateData();
		showGoalValue();
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
	
	public Map<Date, Integer> getHabitProgress(){
		Habit habit = db.getHabit(selectedHabitTypeName);
		return habit.getProgress();
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
	
	public void setGoalForHabit(int value){
		db.setGoalForHabit(selectedHabitTypeName, value);
		TextView textView_goalValue = (TextView) getActivity().findViewById(R.id.textView_goalValue);
		textView_goalValue.setText(String.valueOf(value));
	}
	
	public int getGoalForHabit(){
		return db.getGoalForHabit(selectedHabitTypeName);
	}
	
	public void showGoalValue(){
		int value = db.getGoalForHabit(selectedHabitTypeName);
		
		TextView textView_goalValue = (TextView) getActivity().findViewById(R.id.textView_goalValue);
		if(value >= 0){
			textView_goalValue.setText(String.valueOf(value));
		}else{
			textView_goalValue.setText("None");
		}
	}
	
}
