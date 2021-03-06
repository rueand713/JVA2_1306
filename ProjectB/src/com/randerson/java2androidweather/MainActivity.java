/*
 * project 		Java2AndroidWeather
 * 
 * package 		com.randerson.java2androidweather
 * 
 * @author 		Rueben Anderson
 * 
 * date			Jun 17, 2013
 * 
 */
package com.randerson.java2androidweather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import systemPack.FileSystem;
import systemPack.IOManager;
import systemPack.InterfaceManager;
import systemPack.JSONhandler;
import systemPack.ProviderManager;
import systemPack.ProviderManager.ProviderData;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.randerson.interfaces.FragmentParams;
import com.randerson.java2androidweather.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements FragmentParams {
	
	// setup toast object
	Toast alert;
	
	// setup the context
	Context _context;
	
	// Boolean for the connection status
	Boolean connected;
	
	// create the UI singleton
	InterfaceManager ifManager;
	
	// save bundle
	Bundle saveState;
	
	// fragment views
	View currentWeatherFragment;
	View forecastFragment;
	
	// set the handler and messenger objects
	Handler requestHandler;
	Messenger intentMessenger;
	
	// orientation id
	int fragView;
		
	// setup the memory hash object
	HashMap<String, HashMap<String, String>> memHash;
	
	// object array for return data
	HashMap<String, Object> returnData;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		saveState = savedInstanceState;
		fragView = InterfaceManager.getOrientation(this);
		
		// check the orientation state and load the appropriate layout id
		if (fragView == InterfaceManager.LANDSCAPE)
		{
			if (saveState != null)
			{
				if (saveState.getString("show_query") != null && saveState.getString("show_query").equals("true"))
				{
					// set the content view
					setContentView(R.layout.main_fragment2);
				}
				else
				{
					// set the content view
					setContentView(R.layout.main_fragment1);
				}
			}
			
		}
		else if (fragView == InterfaceManager.PORTRAIT)
		{
			// set the content view
			setContentView(R.layout.main_view);
			
			if (saveState != null)
			{
				if (saveState.getString("show_query") != null && saveState.getString("show_query").equals("true"))
				{
					saveState.putString("show_query", "false");
				}
			}
		}
		
		// setting the current context
		_context = this;
		
		// setup interface singleton
		ifManager = new InterfaceManager(_context);
		
		// create a toast from the singleton
		alert = ifManager.createToast("", false);
		
		connected = IOManager.getConnectionStatus(_context);
		
		if (!connected)
		{
			// load the previous hashfile
			memHash = (HashMap<String, HashMap<String, String>>) FileSystem.readObjectFile(this, "history", false);
			
			// adds previous values if they exist in file
			if (memHash != null)
			{
				// populate the weather data using the object file
				populateWeather(memHash, -1);
				
				TextView forecastText = (TextView) forecastFragment.findViewById(R.id.forcastheader);
				
				// set the forecast header text
				forecastText.setText("Forecast (cached)");
				
				// show a toast to inform the user of current action
				alert.setText("Loaded saved weather data");
				alert.show();
			}
		}
		
		// check if there is saved data
		if (saveState != null)
		{	
			// create string objects from the stored data
			String result = saveState.getString("result");
			String query = saveState.getString("query");
			
			// pass in the stored data strings to repopulate the views
			if (result != null && query != null)
			{
				if (currentWeatherFragment == null && forecastFragment == null)
				{
					fragView = InterfaceManager.getOrientation(this);
					
					// check the orientation state and load the appropriate layout id
					if (fragView == InterfaceManager.LANDSCAPE)
					{
						currentWeatherFragment = FragmentCurrentWeather.view;
						forecastFragment = FragmentForecast.view;
					}
					else if (fragView == InterfaceManager.PORTRAIT)
					{
						currentWeatherFragment = FragmentFull.view;
						forecastFragment = FragmentFull.view;
					}
				}
				
				handleResult(result, query);
				Log.i("onSaveInstanceState", "Data state loaded");
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	
		super.onSaveInstanceState(outState);
		
		if (saveState != null)
		{
			// save out the instance data
			outState.putString("result", saveState.getString("result"));
			outState.putString("query", saveState.getString("query"));
			outState.putString("show_query", saveState.getString("show_query"));
		}
		
		Log.i("onSaveInstanceState", "Data state saved");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// make sure that data is not null
		// this should alleviate app crashes when the user backs out
		if (data != null)
		{
			if (resultCode == RESULT_OK && requestCode == 0)
			{
				Log.i("Main Activity", "On Activity Result");
				
				// retrieve the extras from the intent
				Bundle returnData = data.getExtras();
				
				// retrieve the passed in hasmap from  the bundle
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) returnData.get("data");
				
				// retrieve the string from the obj array
				String result = (String) map.get("result");
				String querySelection = (String) map.get("query");
				
				// call the handleResult method to parse the JSON and update the UI
				handleResult(result, querySelection);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void handleResult(String result, String queryString)
	{	
		
		// create the savestate bundle
		saveState = new Bundle();
		
		// store the result and query strings for the save state
		saveState.putString("result", result);
		saveState.putString("query", queryString);
		
		// set the uri to default to the content uri
		Uri queryUri = ProviderData.CONTENT_URI;
		
		// check if the user opted to query a single day
		if (queryString.equals("All") == false)
		{
			char queryValue = queryString.charAt(0);
			
			// set the single item content uri
			queryUri = Uri.parse("content://" + ProviderManager.AUTHORITY + "/days/" + queryValue);
		}
		
		// query the provider and capture returned cursor
		Cursor cursorResult = getContentResolver().query(queryUri, null, null, null, null);		
				
		Log.i("RESULT", result);
		// set the default value for all dates
		int queryDay = 0;
		
		// check if the query string is for all or a single day
		if (queryString.equals("All") == false)
		{
			// grab the first character for single day query
			queryString = queryString.charAt(0) + "";
			
			try
			{
				queryDay = Integer.parseInt(queryString);
			}
			catch (NumberFormatException e) {
				Log.e("Format Error", "Error parsing the querystring to integer");
			}
		}
		
		// show a toast to inform the user of current action
		alert.setText("URL request complete");
		alert.show();
		
		// create JSON objects from the result string
		// that object is then queried for the particular key and the string is returned and set
		String tempf = JSONhandler.readJSONObject(result, "temp_F");
		String humidityf = JSONhandler.readJSONObject(result, "humidity");
		String condition = "";
		String windSpeedm = JSONhandler.readJSONObject(result, "windspeedMiles");
		String windDirection = JSONhandler.readJSONObject(result, "winddir16Point");
		String imageIcon = "";
		
		// create a new separate JSON object
		JSONObject cc = JSONhandler.returnJSONObject(result);
		
		try {
			// retrieve the deep nested weather condition string and icon url
			condition = cc.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getJSONArray("weatherDesc").getJSONObject(0).getString("value");
			imageIcon = cc.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");
			
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
			}
			
			// put the completed hashmap thisCondition into the weatherData hashmap 
			weatherData.put("day" + (i+1), thisCondition);
			
			// move the cursor to the next row
			cursorResult.moveToNext();
		}
		
		Log.i("Views", "setting text view data");
		
		if (currentWeatherFragment != null && forecastFragment != null)
		{
			// create the current condition text views from layout file
			TextView currentCondition = (TextView) currentWeatherFragment.findViewById(R.id.current_cond);
			TextView temp = (TextView) currentWeatherFragment.findViewById(R.id.current_temp);
			TextView wind = (TextView) currentWeatherFragment.findViewById(R.id.current_wind);
			TextView humidity = (TextView) currentWeatherFragment.findViewById(R.id.current_humid);
			TextView forecastText = (TextView) forecastFragment.findViewById(R.id.forcastheader);
			
			// set the forecast header text
			forecastText.setText("Forecast");
			
			Log.i("Views", "set forecast header");
			
			// set the detail view data
			currentCondition.setText(condition);
			humidity.setText(humidityf + "%");
			temp.setText(tempf + " F");
			wind.setText(windSpeedm + " mph " + windDirection);
			
			Log.i("Views", "loading image view");
			// create the image view from the layout file
			ImageView weatherView = (ImageView) currentWeatherFragment.findViewById(R.id.condition_image);
			
			// ********** THE UrlImageViewHelper IS A THIRD PARTY ANDROID LIBRARY ***********
			// download and set the weather condition image
			UrlImageViewHelper.setUrlDrawable(weatherView, imageIcon);
		}
		// verify that the weatherData is created properly
		if (weatherData != null)
		{	
			// save the hash to internal storage
			FileSystem.writeObjectFile(_context, weatherData, "history", false);
			
			// populate the weather data using the object file
			populateWeather(weatherData, queryDay);
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
	public void populateWeather(HashMap<String, HashMap<String, String>> hash, int targetDay)
	{
		
		Log.i("POPULATE WEATHER", "method started");
		// create a calendar object
		Calendar cal = Calendar.getInstance();
		
		// get the numeric day value for the current day
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		
		// get the day values for the 5 day forecast
		String[] week = returnNext5days(weekday);
		
		// create a new hashmap list array
		ArrayList<HashMap<String, String>> listArray = new ArrayList<HashMap<String, String>>();
		
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
		
		// create the table row objects from the layout
		TableRow row1 = (TableRow) forecastFragment.findViewById(R.id.cell_day1);
		TableRow row2 = (TableRow) forecastFragment.findViewById(R.id.cell_day2);
		TableRow row3 = (TableRow) forecastFragment.findViewById(R.id.cell_day3);
		TableRow row4 = (TableRow) forecastFragment.findViewById(R.id.cell_day4);
		TableRow row5 = (TableRow) forecastFragment.findViewById(R.id.cell_day5);
		
		// remove any children in the rows
		row1.removeAllViews();
		row2.removeAllViews();
		row3.removeAllViews();
		row4.removeAllViews();
		row5.removeAllViews();
		
		// create an array of table rows
		TableRow[] rows = {row1, row2, row3, row4, row5};
		
		// iterate through the listarray and set the appropriate table rows with the data
		for (int i = 0; i < listArray.size(); i++)
		{
			int cellColor;
			
			// set the cellColor integer based on whether i returns a remainder
			if (i % 2 > 0)
			{
				// set the color for a white
				cellColor = getResources().getColor(android.R.color.white);
			}
			else
			{
				// set the color for a dark gray
				cellColor = getResources().getColor(android.R.color.darker_gray);
			}
			
			// create a new hash map object
			HashMap<String, String> data = new HashMap<String, String>();
			
			// set the hashmap to the current hash in the listarray at the current index
			data = listArray.get(i);
			
			// create the text views to append and set the text from the listarray
			TextView day = ifManager.createTextView(data.get("date"), 0);
			TextView temp = ifManager.createTextView(data.get("temp"), 0);
			TextView wind = ifManager.createTextView(data.get("wind"), 0);
			TextView cond = ifManager.createTextView(data.get("condition"), 0);
			
			// add cell styling
			day.setPadding(0, 5, 20, 1);
			day.setLines(2);
			day.setBackgroundColor(cellColor);
			temp.setPadding(0, 5, 20, 1);
			temp.setLines(2);
			temp.setBackgroundColor(cellColor);
			wind.setPadding(0, 5, 20, 1);
			wind.setLines(2);
			wind.setBackgroundColor(cellColor);
			cond.setPadding(0, 5, 20, 1);
			cond.setLines(2);
			cond.setBackgroundColor(cellColor);
			
			// set the actual day value for the weather forecast
			// 0 for full query | -1 for memory loaded population | >0 for single day query
			if (targetDay == 0)
			{
				// user is querying all days so the week array should be referenced
				day.setText(week[i]);
			}
			else if (targetDay > 0)
			{
				// user is querying only a single day use the passed in target day instead
				day.setText(week[targetDay - 1]);
			}
			
			// append the textviews to their parent text rows
			rows[i].addView(day);
			rows[i].addView(temp);
			rows[i].addView(wind);
			rows[i].addView(cond);
		}
		
		// ******* Removed the List View to use a TableLayout instead ********
		
		// create simple adapter for list view
		//SimpleAdapter listAdapter = new SimpleAdapter(this, listArray, R.layout.listdetails, 
		//new String[] {"date", "temp", "wind", "condition"}, new int[] {R.id.day, R.id.temp, R.id.wind, R.id.condition});
		
		// set the list view adapter
		//list.setAdapter(listAdapter);
	}

	@Override
	public void startResultActivity(Intent intent) {
		
		// check if the intent is null 
		// null intent argument signals to open a second activity
		// otherwise the second fragment should be inflated with the second activity layout
		if (intent == null)
		{
			// create the intent for the detail activity
			Intent detailIntent = ifManager.makeIntent(DetailActivity.class);
			
			// start the querying activity
			startActivityForResult(detailIntent, 0);
		}
		else
		{
			final String intentQuery = intent.getExtras().getString("query");
			
			// the request handler object recieves the message from the service, writes the message to file storage
			requestHandler = new Handler() 
			{	
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					
					if (msg.arg1 == RESULT_OK && msg.obj != null)
					{
						// save the JSON string to the device
						FileSystem.writeObjectFile(_context, msg.obj, "JsonWeather", false);
						
						String result = (String) msg.obj;
						String query = intentQuery;
						
						// create the savestate bundle
						saveState = new Bundle();
			
						saveState.putString("result", result);
						saveState.putString("query", query);
						
						changeFragmentView("false");
					}
				}
			};
			
			// create the messenger object
			intentMessenger = new Messenger(requestHandler);
			
			// put the messenger into the intent
			intent.putExtra("Messenger", intentMessenger);
			
			// start up the service
			startService(intent);
		}
	}

	@Override
	public void receiveTableView(View v) {
		
		// set the forecast weather view
		forecastFragment = v;
	}
	
	@Override
	public void getWeatherView(View v)
	{
		// set the current weather fragment view
		currentWeatherFragment = v;
	}

	@Override
	public void changeFragmentView(String bool) {
		
		if (saveState != null)
		{
			// set the content view params for showing the proper xml layout
			saveState.putString("show_query", bool);
		}
		
		// recreate the views
		this.recreate();
		
	}
	
	// method for retrieving a fragment
	public Fragment getFragment(int id)
	{
		Fragment fragment =  getFragmentManager().findFragmentById(id);
		
		return fragment;
	}
	
	

}
