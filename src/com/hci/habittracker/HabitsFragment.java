package com.hci.habittracker;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HabitsFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootview = inflater.inflate(R.layout.fragment_habits, container, false);

		// Get habit types from database and populate list
		DatabaseHandler db = new DatabaseHandler(getActivity());
		List<HabitType> habitTypes = db.getAllHabitTypes();

		List<String> habitTypeNames = new ArrayList<String>();
		for (HabitType ht : habitTypes) {
			habitTypeNames.add(ht.getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, habitTypeNames);
		setListAdapter(adapter);

		return rootview;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		HabitInfoFragment infoFragment = new HabitInfoFragment();
		final Bundle bundle = new Bundle();
		String selectedHabitType = l.getItemAtPosition(position).toString();
		bundle.putString("HabitType", selectedHabitType);
		infoFragment.setArguments(bundle);

		final FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, infoFragment);
		ft.addToBackStack(null);
		ft.commit();

	}

}
