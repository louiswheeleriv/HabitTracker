package com.hci.habittracker;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class HabitInfoFragment extends Fragment{
	
	DatabaseHandler db;
	String selectedHabitTypeName = "";

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
		
		return rootview;
	}
	
	public void deleteThisHabitType(){
		db.deleteHabitTypeByName(selectedHabitTypeName);
        getFragmentManager().beginTransaction().replace(R.id.container, new HabitsFragment()).commit();
	}
	
}
