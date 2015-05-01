package com.hci.habittracker;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpFragment extends ListFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootview = inflater.inflate(R.layout.fragment_help, container, false);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getHelpItems());
		setListAdapter(adapter);
		
		final ListView listView_helpItems = (ListView) rootview.findViewById(android.R.id.list);
		final LinearLayout linearLayout_helpDetail = (LinearLayout) rootview.findViewById(R.id.linearLayout_helpDetail);
		final Button button_helpBack = (Button) rootview.findViewById(R.id.button_helpBack);
		
		button_helpBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v){
				button_helpBack.setVisibility(View.GONE);
				linearLayout_helpDetail.setVisibility(View.GONE);
				listView_helpItems.setVisibility(View.VISIBLE);
			}
		});
		
		return rootview;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		final ListView listView_helpItems = (ListView) getActivity().findViewById(android.R.id.list);
		final LinearLayout linearLayout_helpDetail = (LinearLayout) getActivity().findViewById(R.id.linearLayout_helpDetail);
		final TextView textView_helpTopic = (TextView) getActivity().findViewById(R.id.textView_helpTopic);
		final TextView textView_helpDetail = (TextView) getActivity().findViewById(R.id.textView_helpDetail);
		final Button button_helpBack = (Button) getActivity().findViewById(R.id.button_helpBack);
		
		switch(position){
		case 0:
			textView_helpTopic.setText(getResources().getString(R.string.help_1_q));
			textView_helpDetail.setText(getResources().getString(R.string.help_1_a));
			break;
		case 1:
			textView_helpTopic.setText(getResources().getString(R.string.help_2_q));
			textView_helpDetail.setText(getResources().getString(R.string.help_2_a));
			break;
		case 2:
			textView_helpTopic.setText(getResources().getString(R.string.help_3_q));
			textView_helpDetail.setText(getResources().getString(R.string.help_3_a));
			break;
		}
		
		listView_helpItems.setVisibility(View.GONE);
		linearLayout_helpDetail.setVisibility(View.VISIBLE);
		button_helpBack.setVisibility(View.VISIBLE);

	}
	
	public List<String> getHelpItems(){
		List<String> helpItems = new ArrayList<String>();
		helpItems.add(getResources().getString(R.string.help_1_q));
		helpItems.add(getResources().getString(R.string.help_2_q));
		helpItems.add(getResources().getString(R.string.help_3_q));
		return helpItems;
	}
	
}
