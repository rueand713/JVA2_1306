/*
 * project 		Java2Project
 * 
 * package 		com.randerson.java2project
 * 
 * @author 		Rueben Anderson
 * 
 * date			Jun 5, 2013
 * 
 */
package com.randerson.java2project;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import systemPack.FileSystem;
import systemPack.IOManager;
import systemPack.InterfaceManager;
import systemPack.JSONhandler;
import systemPack.ProviderManager;
import systemPack.ProviderManager.ProviderData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("HandlerLeak")
public class DetailActivity extends Activity {
	
	// token identifiers for the service class
	public static final String MESSENGER_KEY = "Messenger";
	public static final String URL_KEY = "Url";
	public static final String JSON_SAVE_FILE = "JsonWeather";
	
	// setup the views for weather detail layout
	TextView currentCondition;
	TextView temp;
	TextView humidity;
	TextView windSpeed;
	TextView windDir;
	TextView header;
	TextView forecastHeader;
	
	// setup the listview
	ListView list;
	
	// setup the memory hash object
	HashMap<String, HashMap<String, String>> memHash;
	
	// setup the context
	Context _context;
	
	// URL for the get request
	URL weatherURL;
	
	// Boolean for the connection status
	Boolean connected;
	
	// setup toast object
	Toast alert;
	
	// the query type
	String querySelection;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the content view
		setContentView(R.layout.details);
		
		// setting the current context
		_context = this;
		
		// create the listview from layout file
		list = (ListView) findViewById(R.id.list);
		
		// setup interface singleton
		InterfaceManager ifManager = new InterfaceManager(_context);
		
		// create a toast from the singleton
		alert = ifManager.createToast("", false);
		
		// get the bundle extras from the intent
		Bundle intentData = getIntent().getExtras();
		
		// get the passed in selected strings from the bundle
		String selectedValue = intentData.getString("selected");
		querySelection = intentData.getString("query");
		
		// api request string parts
		// the strings will be concatenated with the selected location and passed as the request
		final String apiKey = getString(R.string.apikey);
		final String restStringA = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
		final String restStringB = "&format=json&num_of_days=5&key=" + apiKey;
		
		// sets the textViews from layout file
		header = (TextView) findViewById(R.id.conditionheader);
		header.setText("Current Conditions");
		
		// sets the text views for the weather detail layout
		currentCondition = (TextView) findViewById(R.id.currentcondition);
		temp = (TextView) findViewById(R.id.tempcondition);
		humidity = (TextView) findViewById(R.id.humiditycondition);
		windSpeed = (TextView) findViewById(R.id.winspdcondition);
		windDir = (TextView) findViewById(R.id.windircondition);
		forecastHeader = (TextView) findViewById(R.id.forcastheader);
		
		// get the connection status
		connected = IOManager.getConnectionStatus(_context);
		
		// verify that there is a connected
		// if true try the url request otherwise load previous weather data
		if (connected)
		{
			try {
				// create a URL object from the api strings
				weatherURL = new URL(restStringA + selectedValue + restStringB);
				
				// show a toast to inform the user of current action
				alert.setText("Making URL request...");
				alert.show();
				
				Handler requestHandler = new Handler() 
				{	
					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						
						if (msg.arg1 == RESULT_OK && msg.obj != null)
						{
							// save the JSON string to the device
							FileSystem.writeObjectFile(_context, msg.obj, JSON_SAVE_FILE, false);
							
							// set the uri to default to the content uri
							Uri queryUri = ProviderData.CONTENT_URI;
							
							// check if the user opted to query a single day
							if (querySelection.equals("All") == false)
							{
								char queryValue = querySelection.charAt(0);
								
								// set the single item content uri
								queryUri = Uri.parse("content://" + ProviderManager.AUTHORITY + "/days/" + queryValue);
							}
							
							// query the provider and capture returned cursor
							Cursor cRes = getContentResolver().query(queryUri, null, null, null, null);
							
							// call the handleResult method to parse the JSON and update the UI
							handleResult((String) msg.obj, cRes);
						}
					}
				};
				
				// create the messenger object
				Messenger intentMessenger = new Messenger(requestHandler);
				
				// create the service intent object and pass in the required extras
				Intent thisService = ifManager.makeIntent(DetailService.class);
				thisService.putExtra(URL_KEY, weatherURL).putExtra(MESSENGER_KEY, intentMessenger);
				
				// start up the service
				startService(thisService);
				
			} catch (MalformedURLException e) {
				Log.e("URL ERROR", "Malformed URL");
			}
		}
		else if (!connected)
		{
			// load the previous hashfile
			memHash = (HashMap<String, HashMap<String, String>>) FileSystem.readObjectFile(this, "history", false);
			
			// adds previous values if they exist in file
			if (memHash != null)
			{
				// populate the weather data using the object file
				populateWeather(memHash);
				
				// set the forecast header text
				forecastHeader.setText("Forecast (cached)");
				
				// show a toast to inform the user of current action
				alert.setText("Loaded saved weather data");
				alert.show();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void handleResult(String result, Cursor cursorResult)
	{
		// show a toast to inform the user of current action
		alert.setText("URL request complete");
		alert.show();
		
		// log the result text
		Log.i("RESPONSE", result);
		
		// create JSON objects from the result string
		// that object is then queried for the particular key and the string is returned and set
		String tempf = JSONhandler.readJSONObject(result, "temp_F");
		String humidityf = JSONhandler.readJSONObject(result, "humidity");
		String condition = "";
		String windSpeedm = JSONhandler.readJSONObject(result, "windspeedMiles");
		String windDirection = JSONhandler.readJSONObject(result, "winddir16Point");
		
		// create a new separate JSON object
		JSONObject cc = JSONhandler.returnJSONObject(result);
		
		try {
			// retrieve the deep nested weather condition string
			condition = cc.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getJSONArray("weatherDesc").getJSONObject(0).getString("value");
			
		} catch (JSONException e) {
			Log.e("JSON ERROR", "JSON Exception parsing weather condition");
		}
		
		// create a hashmap for holding the full weather condition
		HashMap<String, HashMap<String, String>> weatherData = new HashMap<String, HashMap<String, String>>();
		
		// move the cursor to the first row
		cursorResult.moveToFirst();
		
		// iterate through the cursor object for each row
		for (int i = 0; i < cursorResult.getCount(); i++)
		{
			// create a hashmap for holding the current weather conditions
			HashMap<String, String> thisCondition = new HashMap<String, String>();
			
			// create string array for iterating through and saving column keys
			String[] keys = {"date", "temp", "wind", "condition"};
			
			// iterate through the cursor object for each column
			for (int j = 1; j < cursorResult.getColumnCount(); j++)
			{
				// create a string with the current column string
				String value = cursorResult.getString(j);
				
				// put the value into the weather hashmap
				thisCondition.put(keys[j-1], value);
		
				Log.i("Rows", value);
			}
			
			// put the completed hashmap thisCondition into the weatherData hashmap 
			weatherData.put("day" + (i+1), thisCondition);
			
			// move the cursor to the next row
			cursorResult.moveToNext();
		}
		
		// set the detail view data
		currentCondition.setText(condition);
		humidity.setText(humidityf);
		temp.setText(tempf);
		windSpeed.setText(windSpeedm);
		windDir.setText(windDirection);
		
		// save the hash to internal storage
		FileSystem.writeObjectFile(_context, weatherData, "history", false);
		
		// verify that the weatherData is created properly
		if (weatherData != null)
		{
			// set the forecast header text
			forecastHeader.setText("Forecast");
			
			// populate the weather data using the object file
			populateWeather(weatherData);
			
		/*	// create a calendar object
			Calendar cal = Calendar.getInstance();
			
			// get the numeric day value for the current day
			int day = cal.get(Calendar.DAY_OF_WEEK);
			
			// get the day values for the 5 day forecast
			String[] week = returnNext5days(day);
			
			// set the actual day values for the dynamic weather forecast
			day1.setText(week[0]);
			day2.setText(week[1]);
			day3.setText(week[2]);
			day4.setText(week[3]);
			day5.setText(week[4]);
			*/
		}
	}
	
	// method for returning a string array of next 5 days beginning with the current day
	public String[] returnNext5days(int dayId)
	{
		// set the final day id
		int endDay = 5;
		
		// create a string array with a capacity of 5
		String[] day = new String[5];
		
		// iterate through the 
		for (int i = 0; i < endDay; i++)
		{
			// check if the dayId is greater than 7 (Saturday)
			if (dayId > 7)
			{
				// sub the previous week
				dayId -= 7;
			}
			
			// create a new empty string
			String thisDay = "";
			
			// set the thisDay string based on the value of the dayId
			switch(dayId)
			{
			case 1:
				thisDay = "Sunday";
				break;
				
			case 2:
				thisDay = "Monday";
				break;
				
			case 3:
				thisDay = "Tuesday";
				break;
				
			case 4:
				thisDay = "Wednesday";
				break;
				
			case 5:
				thisDay = "Thursday";
				break;
				
			case 6:
				thisDay = "Friday";
				break;
				
			case 7:
				thisDay = "Saturday";
				break;
				
				default:
					break;
			}
			
			// set the current index of the string array
			day[i] = thisDay;
			
			// increment the dayId
			dayId++;
		}
		
		// return the object
		return day;
	}
	
	// method for populating the extended weather details
	public void populateWeather(HashMap<String, HashMap<String, String>> hash)
	{
		list.setVisibility(View.VISIBLE);
		
		// create a new hashmap list array
		ArrayList<HashMap<String, String>> listArray = new ArrayList<HashMap<String, String>>();
		
		// create a new hashmap for the title values
		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("date", "DAY");
		headerMap.put("temp", "TEMP");
		headerMap.put("wind", "WIND");
		headerMap.put("condition", "COND");
		
		// add the header hashmap to the list array
		listArray.add(headerMap);
		
		for (int i = 0; i < hash.size(); i++)
		{	
			// create a new hashmap for holding weather details
			HashMap<String, String> listMap = new HashMap<String, String>();
			
			// create string array for iterating through and saving column keys
			String[] keys = {"date", "temp", "wind", "condition"};
			
			// iterate the map for each key
			for (int j = 0; j < keys.length; j++)
			{
				// create a string for each hash value
				String value = hash.get("day" + (i+1)).get(keys[j]);
				
				// add the extracted value to the new hashmap
				listMap.put(keys[j], value);
			}
			
			// add the hashmap to the listarray
			listArray.add(listMap);
		}
		
		// create simple adapter for list view
		SimpleAdapter listAdapter = new SimpleAdapter(this, listArray, R.layout.listdetails, 
				new String[] {"date", "temp", "wind", "condition"}, new int[] {R.id.day, R.id.temp, R.id.wind, R.id.condition});
		
		// set the list view adapter
		list.setAdapter(listAdapter);
	}

}