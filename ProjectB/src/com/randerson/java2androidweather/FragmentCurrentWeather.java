package com.randerson.java2androidweather;

import com.randerson.interfaces.DoQuery;

import systemPack.InterfaceManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentCurrentWeather extends Fragment {
	
	// create a null textview objects for the forecast data
	public static TextView currentCondition;
	public static TextView temp;
	public static TextView humidity;
	public static TextView wind;
	
	// setup Image view
	public static ImageView weatherView;
	
	private DoQuery parentActivity;
	
	// create the UI singleton
	InterfaceManager ifManager;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		if (activity instanceof DoQuery)
		{
			parentActivity = (DoQuery) activity;
		}
		else
		{
			throw new ClassCastException(activity.toString() + " must implement required methods");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_content_currentweather, container);
		
		// create the image view from the layout file
		weatherView = (ImageView) v.findViewById(R.id.condition_image);
		
		// create the current condition text views from layout file
		currentCondition = (TextView) v.findViewById(R.id.current_cond);
		temp = (TextView) v.findViewById(R.id.current_temp);
		wind = (TextView) v.findViewById(R.id.current_wind);
		humidity = (TextView) v.findViewById(R.id.current_humid);
		
		// create button object reference to layout
		Button queryBtn = (Button) v.findViewById(R.id.querybtn);
		
		// set the button click functionality
		queryBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// make an intent for the detailActivity
				Intent detailIntent = ifManager.makeIntent(DetailActivity.class);
				
				// start the next activity for returning a value
				parentActivity.returnQueryResults(detailIntent);
			}
		});
		
		return v;
	}

	
	
}
