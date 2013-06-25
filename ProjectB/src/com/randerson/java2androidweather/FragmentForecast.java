package com.randerson.java2androidweather;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentForecast extends Fragment{
	
	// create a null textview objects for the forecast data
	public static TextView headerText;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_content_forecastweather, container);
		
		// set the header textview text
		headerText = (TextView) v.findViewById(R.id.forcastheader);
		headerText.setText("Forecast");
		
		return v;
	}
	
	
}
