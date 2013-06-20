package com.randerson.java2androidweather;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.randerson.java2androidweather.DetailService;

import systemPack.FileSystem;
import systemPack.IOManager;
import systemPack.InterfaceManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class DetailActivity extends Activity {

	// token identifiers for the service class
		public static final String MESSENGER_KEY = "Messenger";
		public static final String URL_KEY = "Url";
		public static final String JSON_SAVE_FILE = "JsonWeather";
		
	// instantiate the InterfaceManager singleton
	InterfaceManager UIFactory;
			
	// instantiate a new intent
	Intent detailsView;
		
	// URL for the get request
	URL weatherURL;
		
	// Boolean for the connection status
	Boolean connected;
	
	// setup toast object
	Toast alert;
	
	// the query type
	String querySelection;
	
	// set the context
	Context _context;
	
	// setup the radio group
	RadioGroup radios;
	RadioGroup querys;
	
	// set the handler and messenger objects
	Handler requestHandler;
	Messenger intentMessenger;
	
	// setup radio string
	String selectedValue;
	
	// object array for return data
	HashMap<String, Object> returnData;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		// get the id of the currently selected radio buttons
		int cityId = radios.getCheckedRadioButtonId();
		int queryId = querys.getCheckedRadioButtonId();
		
		RadioButton cBtn = (RadioButton) findViewById(cityId);
		RadioButton qBtn = (RadioButton) findViewById(queryId);
		
		Log.i("Saving IDs", "query: " + queryId + "city: " + cityId);
		
		if (cBtn != null && qBtn != null)
		{
			// store the radio key value pairs
			outState.putInt("city", cityId);
			outState.putInt("query", queryId);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.details);
		
		// call the InterfaceManager singleton constructor
		UIFactory = new InterfaceManager(this);
		
		// set the context
		_context = this;
		
		// checks if there is a network connection
		connected = IOManager.getConnectionStatus(this);
		
		// call the method for displaying the toast
		displayToast();
	
		// sets the radiogroups
		querys = (RadioGroup) findViewById(R.id.querychoice);
		radios = (RadioGroup) findViewById(R.id.locales);
		
		// setup the alert toast object
		alert = UIFactory.createToast("", false);
		 
		// api request string parts
		// the strings will be concatenated with the selected location and passed as the request
		final String apiKey = getString(R.string.apikey);
		final String restStringA = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
		final String restStringB = "&format=json&num_of_days=5&key=" + apiKey;
		
		// the request handler object recieves the message from the service, writes the message to file storage
		// queries the provider for user selection and then returns that data to the calling activity
		requestHandler = new Handler() 
		{	
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if (msg.arg1 == RESULT_OK && msg.obj != null)
				{
					// save the JSON string to the device
					FileSystem.writeObjectFile(_context, msg.obj, JSON_SAVE_FILE, false);
					
					// Create container object
					returnData = new HashMap<String, Object>();
					
					returnData.put("result", ((String) msg.obj));
					returnData.put("query", querySelection);
					
					// call method to end activity
					finish();
				}
			}
		};
		
		// create the messenger object
		intentMessenger = new Messenger(requestHandler);
		
		// creates the button with the UIFactory instance
		Button sendBtn = (Button) findViewById(R.id.weather_btn);
		
		// set up the on click for the send button
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// checks if there is a network connection
				connected = IOManager.getConnectionStatus(_context);
				
				// call the method for displaying the toast
				displayToast();
				
				// get the id of the currently selected radio buttons
				int selectedId = radios.getCheckedRadioButtonId();
				int queryId = querys.getCheckedRadioButtonId();
				
				// create an instance of the radio buttons that are currently selected
				RadioButton rBtn = (RadioButton) findViewById(selectedId);
				RadioButton qBtn = (RadioButton) findViewById(queryId);
				
				// do a quick check to make sure that the selected buttons are returned
				if (rBtn != null && qBtn != null)
				{	
					// retrieve the selected button text value from the instance
					selectedValue = rBtn.getText().toString();
					querySelection = qBtn.getText().toString();
					
					if (connected)
					{
						try {
							// create a URL object from the api strings
							weatherURL = new URL(restStringA + selectedValue + restStringB);
							
							// show a toast to inform the user of current action
							alert.setText("Making URL request...");
							alert.show();
								
							// create the service intent object and pass in the required extras
							Intent thisService = UIFactory.makeIntent(DetailService.class);
							thisService.putExtra(URL_KEY, weatherURL).putExtra(MESSENGER_KEY, intentMessenger);
							
							// start up the service
							startService(thisService);
							
						} catch (MalformedURLException e) {
							Log.e("MALFORMED URL", "weather URL try/catch block");
						}
					}
				}
			}
		});
		
		// check whether or not there is saved state data
		if (savedInstanceState != null)
		{
			int queryId = savedInstanceState.getInt("query");
			int cityId = savedInstanceState.getInt("city");
			
			Log.i("Loading IDs", "query: " + queryId + "city: " + cityId);
			
			// check that the data is an actual id
			if ( queryId > 0 && cityId > 0)
			{
				// create an instance of the radio buttons that are currently selected
				RadioButton queryBtn = (RadioButton) querys.findViewById(queryId);
				RadioButton cityBtn = (RadioButton) radios.findViewById(cityId);
				
				if (cityBtn != null && queryBtn != null)
				{
					
					Log.i("Button setting", "Buttons are not null");
					// set the radios to be selected
					cityBtn.setChecked(true);
					queryBtn.setChecked(true);
				}
			}
		}
	}

	@Override
	public void finish() {
		
		Log.i("Detail Activity", "Finish Method");
		
		// only set the data if the finish method was called from the handler
		if (returnData != null)
		{
			// create an intent to pass back to the calling activity
			Intent data = new Intent();
			
			// store the return data in the intent extras
			data.putExtra("data", returnData);
			
			setResult(RESULT_OK, data);
		}
		
		super.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// method for showing the proper toast
		private void displayToast()
		{
			String conType = IOManager.getConnectionType(this);
			
			// create the Toast object with the singleton
			Toast msg = UIFactory.createToast("", false);
			
			if (connected == true)
			{
				// set the toast text
				msg.setText(conType + "Network detected");
			}
			else
			{
				// set the toast text
				msg.setText("No Connection");
			}
			
			// show the toast
			msg.show();
		}
}
