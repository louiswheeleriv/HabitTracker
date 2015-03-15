package com.hci.habittracker;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HabitInfoFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootview = inflater.inflate(R.layout.fragment_habit_info, container, false);
		
		String habitName = "";
		
		Bundle args = getArguments();
		if (args != null && args.containsKey("HabitType")){
			habitName = args.getString("HabitType");
		}
		
		TextView textView_habitName = (TextView) rootview.findViewById(R.id.textView_habitName);
		
		textView_habitName.setText(habitName);
		
		return rootview;
	}
	
}
