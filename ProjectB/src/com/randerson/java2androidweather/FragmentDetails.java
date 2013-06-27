package com.randerson.java2androidweather;

import java.net.MalformedURLException;
import java.net.URL;

import com.randerson.interfaces.FragmentParams;

import systemPack.IOManager;
import systemPack.InterfaceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class FragmentDetails extends Fragment {

	// set the context
	Context _context;
	
	public static ViewGroup viewGroup;
	
	// URL for the get request
	URL weatherURL;
		
	// Boolean for the connection status
	Boolean connected;
	
	// setup toast object
	Toast alert;
	
	// instantiate the InterfaceManager singleton
	InterfaceManager UIFactory;
	
	// the query type
	String querySelection;
	
	// setup radio string
	String selectedValue;
		
	// setup the radio group
	RadioGroup radios;
	RadioGroup querys;
	
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
		
		viewGroup = container;
		
		// set the context
		_context = getActivity();
		
		// call the InterfaceManager singleton constructor
		UIFactory = new InterfaceManager(_context);
		
		// setup the alert toast object
		alert = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
		
		view = inflater.inflate(R.layout.details_fragment, container);
		
		// creates the button from layout file
		Button sendBtn = (Button) view.findViewById(R.id.weather_btn);
		Button webBtn = (Button) view.findViewById(R.id.webbtn);
		
		// sets the radiogroups
		querys = (RadioGroup) view.findViewById(R.id.querychoice);
		radios = (RadioGroup) view.findViewById(R.id.locales);
		
		// set the click listener for the web button
		webBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// checks if there is a network connection
				connected = IOManager.getConnectionStatus(_context);
				
				if (connected)
				{
					// display the connected message
					String type = IOManager.getConnectionType(_context);
					alert.setText(type + " detected");
				}
				else
				{
					// display the no connection
					alert.setText("No Connection");
				}
				
				// show the toast
				alert.show();
				
				// get the id of the currently selected city radio button
				int selectedId = radios.getCheckedRadioButtonId();
				
				// create an instance of the radio buttons that are currently selected
				RadioButton rBtn = (RadioButton) view.findViewById(selectedId);
				
				if (connected)
				{
					if (rBtn != null)
					{
						// create strings with the city values from the string resource file
						String houston = getString(R.string.houston);
						String chicago = getString(R.string.chicago);
						String seattle = getString(R.string.seattle);
						String miami = getString(R.string.miami);
						
						// retrieve the selected button text value from the instance
						String cityString = rBtn.getText().toString();
						
						// set the state null for string validation
						String stateString = null;
						
						// compare the value of the city string with the resource strings
						// set the state string when the city string matches the resource string
						if (cityString.equals(houston))
						{
							stateString = "Texas";
						}
						else if (cityString.equals(chicago))
						{
							stateString = "Illinois";
						}
						else if (cityString.equals(seattle))
						{
							stateString = "Washington";
						}
						else if (cityString.equals(miami))
						{
							stateString = "Florida";
						}
						
						// verify that the state string is created
						if (stateString !=  null)
						{
							// set the web url string with the proper city and state values
							String webUrl = "http://www.worldweatheronline.com/" + cityString + "-weather/" + stateString + "/US.aspx";
							
							// create the new system intent for opening a url
							Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
							
							// start activity with the web intent
							startActivity(webIntent);
						}
					}
				}
			}
		});
		
		// set up the on click for the send button
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// checks if there is a network connection
				connected = IOManager.getConnectionStatus(_context);
				
				if (connected)
				{
					// display the connected message
					String type = IOManager.getConnectionType(_context);
					alert.setText(type + " detected");
				}
				else
				{
					// display the no connection
					alert.setText("No Connection");
				}
				
				// show the toast
				alert.show();
				
				// get the id of the currently selected radio buttons
				int selectedId = radios.getCheckedRadioButtonId();
				int queryId = querys.getCheckedRadioButtonId();
				
				// create an instance of the radio buttons that are currently selected
				RadioButton rBtn = (RadioButton) view.findViewById(selectedId);
				RadioButton qBtn = (RadioButton) view.findViewById(queryId);
				
				// do a quick check to make sure that the selected buttons are returned
				if (rBtn != null && qBtn != null)
				{	
					// retrieve the selected button text value from the instance
					selectedValue = rBtn.getText().toString();
					querySelection = qBtn.getText().toString();
					
					if (connected)
					{
						
						// api request string parts
						// the strings will be concatenated with the selected location and passed as the request
						final String apiKey = getString(R.string.apikey);
						final String restStringA = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
						final String restStringB = "&format=json&num_of_days=5&key=" + apiKey;
						
						try {
							// create a URL object from the api strings
							weatherURL = new URL(restStringA + selectedValue + restStringB);
							
							// create the service intent object and pass in the required extras
							Intent thisService = UIFactory.makeIntent(DetailService.class);
							thisService.putExtra("query", querySelection);
							thisService.putExtra("Url", weatherURL);
							
							// show a toast to inform the user of current action
							alert.setText("Making URL request...");
							alert.show();
							
							// start the result service in the parent
							parentActivity.startResultActivity(thisService);
							
						} catch (MalformedURLException e) {
							Log.e("MALFORMED URL", "weather URL try/catch block");
						}
					}
				}
			}
		});

		return view;
	}
	
}
