package com.hci.habittracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class HabitsFragment extends ListFragment {
	
	DatabaseHandler db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootview = inflater.inflate(R.layout.fragment_habits, container, false);

		// Get habit types from database and populate list
		db = new DatabaseHandler(getActivity());
		List<HabitType> habitTypes = db.getAllHabitTypes();

		List<String> habitTypeNames = new ArrayList<String>();
		for (HabitType ht : habitTypes) {
			habitTypeNames.add(ht.getName());
		}
		Collections.sort(habitTypeNames);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, habitTypeNames);
		setListAdapter(adapter);
		
		final Button button_addHabitType_start = (Button) rootview.findViewById(R.id.button_addHabitType_start);
		final EditText editText_addHabitType = (EditText) rootview.findViewById(R.id.editText_addHabitType_name);
		final CheckBox checkbox_goodHabit = (CheckBox) rootview.findViewById(R.id.checkbox_goodHabit);
		final Button button_addHabitType_submit = (Button) rootview.findViewById(R.id.button_addHabitType_submit);
		final Button button_addHabitType_cancel = (Button) rootview.findViewById(R.id.button_addHabitType_cancel);
		final LinearLayout addHabitTypeSection = (LinearLayout) rootview.findViewById(R.id.linearLayout_addHabitType_inner);
		final LinearLayout linearLayout_habitTypes = (LinearLayout) rootview.findViewById(R.id.linearLayout_habitTypes);
		
		// Initial create habit type button clicked
		button_addHabitType_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v){	
				button_addHabitType_start.setVisibility(View.GONE);
				addHabitTypeSection.setVisibility(View.VISIBLE);
				linearLayout_habitTypes.setVisibility(View.GONE);
			}
		});
		
		// Submit button clicked
		button_addHabitType_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				String habitTypeName = editText_addHabitType.getText().toString();
				boolean goodHabit = checkbox_goodHabit.isChecked();
				db.addHabitType(new HabitType(habitTypeName, goodHabit));

				button_addHabitType_start.setVisibility(View.VISIBLE);
				addHabitTypeSection.setVisibility(View.GONE);
				linearLayout_habitTypes.setVisibility(View.VISIBLE);
				
				hideKeyboard();
				
		        getFragmentManager().beginTransaction().replace(R.id.container, new HabitsFragment()).commit();
			}
		});
		
		// Cancel button clicked
		button_addHabitType_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				editText_addHabitType.setText("");
				checkbox_goodHabit.setChecked(false);

				button_addHabitType_start.setVisibility(View.VISIBLE);
				addHabitTypeSection.setVisibility(View.GONE);
				linearLayout_habitTypes.setVisibility(View.VISIBLE);
				
				hideKeyboard();
			}
		});
		
		// Ensure that the input area remains on screen when the keyboard opens
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		return rootview;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		HabitInfoFragment infoFragment = new HabitInfoFragment();
		final Bundle bundle = new Bundle();
		String selectedHabitType = l.getItemAtPosition(position).toString();
		bundle.putString("HabitType", selectedHabitType);
		bundle.putString("Fragment Name", String.valueOf(R.string.habitsInfoFragmentName));
		infoFragment.setArguments(bundle);

		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		//ft.replace(R.id.container, infoFragment);
		ft.replace(((ViewGroup)(getView().getParent())).getId(), infoFragment);
		ft.addToBackStack(null);
		ft.commit();

	}
	
	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(
				getActivity().getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
