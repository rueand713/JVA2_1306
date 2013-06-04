package com.randerson.java2project;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import systemPack.FileSystem;
import systemPack.IOManager;
import systemPack.JSONhandler;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;


public class DetailActivity extends Activity {
	
	// setup the views for weather detail layout
	TextView currentCondition;
	TextView temp;
	TextView humidity;
	TextView windSpeed;
	TextView windDir;
	TextView header;
	
	// setup the memory hash object
	HashMap<String, String> memHash;
	
	// setup the context
	Context _context;
	
	// URL for the get request
	URL weatherURL;
	
	// Boolean for the connection status
	Boolean connected;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.details);
		
		_context = this;
		
		Bundle intentData = getIntent().getExtras();
		
		String selectedValue = intentData.getString("selected");
		
		// api request string parts
		// the strings will be concatenated with the selected location and passed as the request
		final String apiKey = getString(R.string.apikey);
		final String restStringA = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
		final String restStringB = "&format=json&num_of_days=5&key=" + apiKey;
		
		// sets the textViews from layout file
		header = (TextView) findViewById(R.id.conditionheader);
		header.setText("No Previous Conditions");
		
		// sets the text views for the weather detail layout
		currentCondition = (TextView) findViewById(R.id.currentcondition);
		temp = (TextView) findViewById(R.id.tempcondition);
		humidity = (TextView) findViewById(R.id.humiditycondition);
		windSpeed = (TextView) findViewById(R.id.winspdcondition);
		windDir = (TextView) findViewById(R.id.windircondition);
		
		// load the previous hashfile
		memHash = (HashMap<String, String>) FileSystem.readObjectFile(this, "history", false);
		
		// adds previous values if they exist in file
		if (memHash != null)
		{
			// temp strings for the weather data
			String cc = memHash.get("cond");
			String tmp = memHash.get("temp");
			String hum = memHash.get("humi");
			String wsp = memHash.get("wspd");
			String wdi = memHash.get("wdir");
			
			// sets the last weather data saved/viewed
			currentCondition.setText(cc);
			temp.setText(tmp);
			humidity.setText(hum);
			windSpeed.setText(wsp);
			windDir.setText(wdi);
			
			// sets the previous header text
			header.setText("Previous Conditions");
		}
		
		try {
			// create a URL object from the api strings
			weatherURL = new URL(restStringA + selectedValue + restStringB);
			
			// creates the new request object
			doRequest req = new doRequest();
			
			// starts the request
			req.execute(weatherURL);
			
		} catch (MalformedURLException e) {
			Log.e("URL ERROR", "Malformed URL");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// android class thread for asynchronous requests
	private class doRequest extends AsyncTask<URL, Void, String>
	{
		
		@Override
		protected String doInBackground(URL...urls)
		{
			// checks if there is a network connection
			connected = IOManager.getConnectionStatus(_context);
			
			// create a empty response string
			String response = "";
			
			if (connected == true)
			{
				// loop for making url request with URLs 
				for (URL url:urls)
				{
					// response gets set for each request made (in this instance just one)
					response = IOManager.makeStringRequest(url);
				}
			}
			// return the response text
			return response;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
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
			JSONArray extendedWeather;
			
			// create a weather data hashmap
			HashMap<String, HashMap<String, String>> weatherData = new HashMap<String, HashMap<String, String>>();
			
			try {
				// retrieve the deep nested weather condition string
				condition = cc.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getJSONArray("weatherDesc").getJSONObject(0).getString("value");
				
			} catch (JSONException e) {
				Log.e("JSON ERROR", "JSON Exception");
			}
			
			try {
				// retrieve the deep nested extended weather array
				extendedWeather = cc.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getJSONArray("weather");
				
				for (int i = 0; i < extendedWeather.length(); i++)
				{
					HashMap<String, String> thisCondition = new HashMap<String, String>();
				}
				
			} catch (JSONException e) {
				Log.e("JSON ERROR", "JSON Exception");
			}
			
			// set the detail view data
			currentCondition.setText(condition);
			humidity.setText(humidityf);
			temp.setText(tempf);
			windSpeed.setText(windSpeedm);
			windDir.setText(windDirection);
			header.setText("Current Conditions");
			
			// create a hashmap and save the data to the hashmap
			memHash = new HashMap<String, String>();
			memHash.put("cond", condition);
			memHash.put("humi", humidityf);
			memHash.put("temp", tempf);
			memHash.put("wspd", windSpeedm);
			memHash.put("wdir", windDirection);
			
			// save the hash to internal storage
			FileSystem.writeObjectFile(_context, memHash, "history", false);
		}
	}

}