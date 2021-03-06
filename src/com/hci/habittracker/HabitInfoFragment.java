package com.hci.habittracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HabitInfoFragment extends Fragment{
	
	DatabaseHandler db;
	String selectedHabitTypeName = "";
	Date selectedDate = new Date();
	
	HabitType selectedHabitType;
	
	@Override
	public void onCreate(Bundle save){
		super.onCreate(save);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootview = inflater.inflate(R.layout.fragment_habit_info, container, false);
		
		this.setRetainInstance(true);
		
		db = new DatabaseHandler(getActivity());
		
		Bundle args = getArguments();
		if (args != null && args.containsKey("HabitType")){
			selectedHabitTypeName = args.getString("HabitType");
			selectedHabitType = db.getHabitType(selectedHabitTypeName);
			
			if(args.containsKey("Selected Date")){
				int[] dateArray = args.getIntArray("Selected Date");
				int year = dateArray[0];
				int month = dateArray[1];
				int day = dateArray[2];
								
				Date chosenDate = new Date();
				chosenDate.setDate(day);
				chosenDate.setMonth(month);
				chosenDate.setYear(year-1900);
				
				selectedDate = chosenDate;
			}
		}
		
		// Habit name at top
		TextView textView_habitName = (TextView) rootview.findViewById(R.id.textView_habitName);
		textView_habitName.setText(selectedHabitTypeName);
		if(selectedHabitType.isGoodHabit()){
			textView_habitName.setTextColor(Color.parseColor("#00CC00"));
		}else{
			textView_habitName.setTextColor(Color.parseColor("#CC0000"));
		}
		
		// Control for delete button
		final Button button_deleteHabitType = (Button) rootview.findViewById(R.id.button_deleteHabitType);
		final LinearLayout linearLayout_delete = (LinearLayout) rootview.findViewById(R.id.linearLayout_delete);
		final Button button_deleteYes = (Button) rootview.findViewById(R.id.button_deleteYes);
		final Button button_deleteNo = (Button) rootview.findViewById(R.id.button_deleteNo);
		
		button_deleteHabitType.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				//deleteThisHabitType();
				button_deleteHabitType.setVisibility(View.GONE);
				linearLayout_delete.setVisibility(View.VISIBLE);
			}
		});
		
		button_deleteYes.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				deleteThisHabitType();
			}
		});
		
		button_deleteNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				linearLayout_delete.setVisibility(View.GONE);
				button_deleteHabitType.setVisibility(View.VISIBLE);
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
				
				selectedDate = chosenDate;
				
				showSelectedDateData();
				
			}
		});
		
		datePicker_habitData.setOnTouchListener(new OnTouchListener(){
			@Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle DatePicker touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
		});
		
		// Control for data editing
		final EditText editText_editHabitData = (EditText) rootview.findViewById(R.id.editText_editHabitData);
		final Button button_editHabitData_edit = (Button) rootview.findViewById(R.id.button_editHabitData_edit);
		button_editHabitData_edit.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String label = (String) button_editHabitData_edit.getText();
				if(label.equals("Set Value")){
					LinearLayout linearLayout_habitData_edit = (LinearLayout) getActivity().findViewById(R.id.linearLayout_habitData_edit);
					linearLayout_habitData_edit.setVisibility(View.VISIBLE);
					button_editHabitData_edit.setText(R.string.button_editHabitData_cancel);
				}else if(label.equals("Cancel")){
					LinearLayout linearLayout_habitData_edit = (LinearLayout) getActivity().findViewById(R.id.linearLayout_habitData_edit);
					editText_editHabitData.setText("");
					linearLayout_habitData_edit.setVisibility(View.GONE);
					button_editHabitData_edit.setText(R.string.button_editHabitData_edit);
					hideKeyboard();
				}
			}
		});
		
		// Edit value for date
		Button button_editHabitData_save = (Button) rootview.findViewById(R.id.button_editHabitData_save);
		button_editHabitData_save.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String valueEnteredString = editText_editHabitData.getText().toString();
				int valueEntered = Integer.valueOf(valueEnteredString);
				setHabitDataForDate(valueEntered);
				editText_editHabitData.setText("");
				
				hideKeyboard();
				showSelectedDateData();
				reloadFragment();
			}
		});
		
		// Erase value for date
		Button button_editHabitData_erase = (Button) rootview.findViewById(R.id.button_editHabitData_erase);
		button_editHabitData_erase.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				removeHabitDataForDate();
				editText_editHabitData.setText("");
				hideKeyboard();
				showSelectedDateData();
				reloadFragment();
			}
		});
		
		// Control for goal editing
		final EditText editText_editGoal = (EditText) rootview.findViewById(R.id.editText_editGoal);
		final Button button_editGoal_edit = (Button) rootview.findViewById(R.id.button_editGoal_edit);
		button_editGoal_edit.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String label = (String) button_editGoal_edit.getText();
				if(label.equals("Set Value")){
					LinearLayout linearLayout_goal_edit = (LinearLayout) getActivity().findViewById(R.id.linearLayout_goal_edit);
					linearLayout_goal_edit.setVisibility(View.VISIBLE);
					button_editGoal_edit.setText(R.string.button_editGoal_label_cancel);
				}else if(label.equals("Cancel")){
					LinearLayout linearLayout_goal_edit = (LinearLayout) getActivity().findViewById(R.id.linearLayout_goal_edit);
					editText_editGoal.setText("");
					linearLayout_goal_edit.setVisibility(View.GONE);
					button_editGoal_edit.setText(R.string.button_editGoal_label_edit);
					hideKeyboard();
				}
			}
		});
		
		// Edit goal
		Button button_editGoal_save = (Button) rootview.findViewById(R.id.button_editGoal_save);
		button_editGoal_save.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String valueEnteredString = editText_editGoal.getText().toString();
				int valueEntered = Integer.valueOf(valueEnteredString);
				setGoalForHabit(valueEntered);
				editText_editGoal.setText("");
				hideKeyboard();
				reloadFragment();
			}
		});
		
		// Erase goal data
		Button button_editGoal_erase = (Button) rootview.findViewById(R.id.button_editGoal_erase);
		button_editGoal_erase.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				setGoalForHabit(-1);
				editText_editGoal.setText("");
				hideKeyboard();
				reloadFragment();
			}
		});
		
		drawGraph(rootview);
		
		// Ensure that the input area remains on screen when the keyboard opens
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		return rootview;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		determineDate();
		showSelectedDateData();
		showGoalValue();
	}
	
	public void determineDate(){
		DatePicker datePicker_habitData = (DatePicker) getActivity().findViewById(R.id.datePicker_habitData);
		datePicker_habitData.updateDate(selectedDate.getYear() + 1900, selectedDate.getMonth(), selectedDate.getDate());
	}
	
	public void showSelectedDateData(){
		TextView textView_habitData_dateValue = (TextView) getActivity().findViewById(R.id.textView_habitData_dateValue);
		
		int valueToSet = getHabitDataForDate(selectedDate);
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
	
	public void setHabitDataForDate(int value){
		db.addHabitDataForDate(selectedHabitTypeName, selectedDate, value);
	}
	
	public void removeHabitDataForDate(){
		db.removeHabitDataForDate(selectedDate);
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
	
	public void drawGraph(View rootview){
		Map<Date, Integer> progress = getHabitProgress();
		
		// Remove empty dates
		List<Date> datesToRemove = new ArrayList<Date>();
		for(Date dt : progress.keySet()){
			if(progress.get(dt) < 0){
				datesToRemove.add(dt);
			}
		}
		
		for(Date dt : datesToRemove){
			progress.remove(dt);
		}
		
		if(isGoalMet()){
			TextView textView_goalMet = (TextView) rootview.findViewById(R.id.textView_goalMet);
			textView_goalMet.setVisibility(View.VISIBLE);
		}
		
		if(progress.size() == 0){
			TextView textView_noProgressData = (TextView) rootview.findViewById(R.id.textView_noProgressData);
			textView_noProgressData.setText(R.string.text_noProgressData);
		} else if(progress.size() == 1){
			TextView textView_noProgressData = (TextView) rootview.findViewById(R.id.textView_noProgressData);
			textView_noProgressData.setText(R.string.text_insufficientProgressData);
		} else if(progress.size() > 1){
			
			DataPoint[] dataPoints = new DataPoint[progress.size()];
			List<Date> dates = new ArrayList<Date>();
			dates.addAll(progress.keySet());
			Collections.sort(dates);
			
			// Generate datapoint array and determine min and max X values
			int count = 0;
			Date minDate = new Date();
			Date maxDate = dates.get(0);
			
			for(Date dt : dates){
				int value = progress.get(dt);
				if(value >= 0){				
					dataPoints[count] = new DataPoint(dt, value);
					count++;
						
					if(minDate.after(dt)){
						minDate = dt;
					}
					
					if(maxDate.before(dt)){
						maxDate = dt;
					}
				}
			}
			
			// Create graph
			GraphView graph = (GraphView) rootview.findViewById(R.id.graph);
			TextView textView_noProgressData = (TextView) rootview.findViewById(R.id.textView_noProgressData);
			textView_noProgressData.setVisibility(View.GONE);
			graph.setVisibility(View.VISIBLE);
			
			// Determine min and max Y values
			int lowestValue = (int) dataPoints[0].getY();
			int highestValue = (int) dataPoints[0].getY();
			
			for(int i = 0; i < dataPoints.length; i++){
				Date dt = new Date();
				dt.setTime((long) dataPoints[i].getX());
				if(dataPoints[i].getY() < lowestValue){
					lowestValue = (int) dataPoints[i].getY();
				}
				if(dataPoints[i].getY() > highestValue){
					highestValue = (int) dataPoints[i].getY();
				}
			}
			
			// Add series to graph
			LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
			graph.addSeries(series);
			
			// Add goal line
			int goal = getGoalForHabit();
			if(goal >= 0){
				DataPoint[] goalDataPoints = new DataPoint[2];
				goalDataPoints[0] = new DataPoint(minDate, goal);
				goalDataPoints[1] = new DataPoint(maxDate, goal);
				
				if(goal < lowestValue){
					lowestValue = goal;
				}else if(goal > highestValue){
					highestValue = goal;
				}
				
				LineGraphSeries<DataPoint> goalSeries = new LineGraphSeries<DataPoint>(goalDataPoints);
				goalSeries.setColor(Color.RED);
				graph.addSeries(goalSeries);
			}
			
			// Set date label formatter
			graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
			
			// Determine orientation and number of labels
			int numLabels = 3;
			Configuration config = getResources().getConfiguration();
			if(config.orientation == config.ORIENTATION_LANDSCAPE){
				numLabels = 5;
			}
			graph.getGridLabelRenderer().setNumHorizontalLabels(numLabels);
			
			// Set X and Y bounds
			graph.getViewport().setMinX(minDate.getTime());
			graph.getViewport().setMaxX(maxDate.getTime());
			graph.getViewport().setXAxisBoundsManual(true);
			
			graph.getViewport().setMinY(lowestValue);
			graph.getViewport().setMaxY(highestValue);
			graph.getViewport().setYAxisBoundsManual(true);
			
		}
	}
	
	public boolean isGoalMet(){
		int goal = getGoalForHabit();
		Habit selectedHabit = db.getHabit(selectedHabitTypeName);
		List<Date> datesTracked = new ArrayList<Date>();
		Map<Date, Integer> progress = selectedHabit.getProgress();
		datesTracked.addAll(progress.keySet());
		Collections.sort(datesTracked);
		boolean isGoodHabit = selectedHabitType.isGoodHabit();
		
		if(isGoodHabit){
			if(progress.size() > 0){
				if(progress.get(datesTracked.get(datesTracked.size() - 1)) >= goal && goal != -1){
					return true;
				}else{
					return false;
				}
			}
		}else{
			if(progress.size() > 0){
				if(progress.get(datesTracked.get(datesTracked.size() - 1)) <= goal && goal != -1){
					return true;
				}else{
					return false;
				}
			}
		}
		
		return false;
		
	}
	
	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(
				getActivity().getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public void reloadFragment(){
		HabitInfoFragment infoFragment = new HabitInfoFragment();
		final Bundle bundle = new Bundle();
		bundle.putString("HabitType", selectedHabitTypeName);
		
		DatePicker datePicker_habitData = (DatePicker) getActivity().findViewById(R.id.datePicker_habitData);
		int year = datePicker_habitData.getYear();
		int month = datePicker_habitData.getMonth();
		int day = datePicker_habitData.getDayOfMonth();
		bundle.putIntArray("Selected Date", new int[] {year, month, day});
		infoFragment.setArguments(bundle);

		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, infoFragment);
		//ft.addToBackStack(null);
		ft.commit();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);	    
	    reloadFragment();
	}
	
}
