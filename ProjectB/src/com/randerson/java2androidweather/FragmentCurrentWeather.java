package com.randerson.java2androidweather;

import com.randerson.interfaces.FragmentParams;
import systemPack.InterfaceManager;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentCurrentWeather extends Fragment {
	
	// create a null textview objects for the forecast data
	public TextView currentCondition;
	public TextView temp;
	public TextView humidity;
	public TextView wind;
	
	public static View view;
	
	// setup Image view
	public ImageView weatherView;
	
	private FragmentParams parentActivity;
	
	// create the UI singleton
	InterfaceManager ifManager;

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
		
		view = inflater.inflate(R.layout.fragment_content_currentweather, container);
		
		// create the image view from the layout file
		weatherView = (ImageView) view.findViewById(R.id.condition_image);
		
		// create the current condition text views from layout file
		currentCondition = (TextView) view.findViewById(R.id.current_cond);
		temp = (TextView) view.findViewById(R.id.current_temp);
		wind = (TextView) view.findViewById(R.id.current_wind);
		humidity = (TextView) view.findViewById(R.id.current_humid);
		
		// create button object reference to layout
		Button queryBtn = (Button) view.findViewById(R.id.querybtn);
		
		// set the button click functionality
		queryBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// start the next activity for returning a value
				parentActivity.changeFragmentView("true");
			}
		});
		
		// pass the view into the parent
		parentActivity.getWeatherView(view);
		
		return view;
	}
}
