package com.randerson.java2androidweather;

import com.randerson.interfaces.FragmentParams;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentForecast extends Fragment {
	
	// create a null textview objects for the forecast data
	public TextView headerText;
	
	// the current view object
	public static View view;
	
	private FragmentParams parentActivity;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		if (activity instanceof FragmentParams)
		{
			parentActivity = (FragmentParams) activity;
		}
		else
		{
			throw new ClassCastException(activity.toString() + " must implement required methods");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_content_forecastweather, container);
		
		// set the header textview text
		headerText = (TextView) view.findViewById(R.id.forcastheader);
		headerText.setText("Forecast");
		
		// pass the view into the parent
		parentActivity.receiveTableView(view);
		
		return view;
	}	
	
}
