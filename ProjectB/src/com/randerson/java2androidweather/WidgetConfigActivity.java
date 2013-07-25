package com.randerson.java2androidweather;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import systemPack.FileSystem;
import systemPack.JSONhandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

public class WidgetConfigActivity extends Activity {

	final String MESSENGER_KEY = "Messenger";
	final String URL_KEY = "Url";
	
	int appWidgetId = 0;
	Context context;
	Handler requestHandler;
	Messenger intentMessenger;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.widget_config_layout);
		
		// set the default result
		setResult(RESULT_CANCELED, null);
		
		// variables
		Intent intent = getIntent();
		context = this;
		
		// verify that the intent is valid
		if (intent != null)
		{
			// get the intent extras
			Bundle bundle = intent.getExtras();
			
			// verify that the bundle object is valid
			if (bundle != null)
			{
				// set the app widget id from the bundle
				appWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 
			            AppWidgetManager.INVALID_APPWIDGET_ID);
			}
		}
		
		// reference the done button from the layout
		Button doneBtn = (Button) findViewById(R.id.zip_accept);
		
		// verify that the done button is valid
		if (doneBtn != null)
		{
			doneBtn.setOnClickListener(new View.OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					
					// reference the zip code edit text from layout
					EditText zipField = (EditText) findViewById(R.id.zip_data);
					
					// verify that the zipfiled object is valid
					if (zipField != null)
					{
						// set the zip to the string data entered into the edit text
						String zipCode = zipField.getText().toString();
						
						// validate the zip
						zipCode = validateZip(zipCode);
						
						// verify the zip is valid
						if (zipCode != null && zipCode.length() == 5)
						{
							// start the weather service
							serviceCall(zipCode);
						}
					}
					
				}
			});
		}
		
	}

	// method for validating the zip entry
	public String validateZip(String zip)
	{
		// verify that the zip object is valid
		if (zip != null)
		{
			// remove any whitespace
			zip = zip.replace(" ", "");
			
			// check if the zip is now an empty string
			if (zip.equals(""))
			{
				// null the zip for being invalid
				zip = null;
			}
		}
		
		return zip;
	}
	
	@SuppressLint("HandlerLeak")
	public void serviceCall(String zipcode)
	{
		
		final String zipCode = zipcode;
		
		// api request string parts
		// the strings will be concatenated with the selected location and passed as the request
		final String apiKey = getString(R.string.apikey);
		final String restStringA = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
		final String restStringB = "&format=json&num_of_days=5&key=" + apiKey;
		
		// weather URL object
		URL weatherURL = null; 
		
		try {
			// try to create a new URL from the strings
			weatherURL = new URL(restStringA + zipcode + restStringB);
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		}
				
		// the request call back
		requestHandler = new Handler() 
		{	
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if (msg.arg1 == RESULT_OK && msg.obj != null)
				{
					// grab the result string
					String result = (String) msg.obj;
					
					// pass in the result string to parse out the json data
					// call the method to update the widget data
					updateWidget(parseJson(result, zipCode));
				}
			}
		};
		
		// create the messenger object
		intentMessenger = new Messenger(requestHandler);
		
		// make the service intent
		Intent intentService = new Intent(this, DetailService.class);
		
		// put the messenger into the intent
		intentService.putExtra(URL_KEY, weatherURL).putExtra(MESSENGER_KEY, intentMessenger);
		
		// start up the service
		startService(intentService);
	}
	
	public HashMap<String, String> parseJson(String result, String zipcode)
	{
		HashMap<String, String> widgetDetails = new HashMap<String, String>();
		
		// create JSON objects from the result string
		// that object is then queried for the particular key and the string is returned and set
		String temp = JSONhandler.readJSONObject(result, "temp_F");
		String humidity = JSONhandler.readJSONObject(result, "humidity");
		String windSpeed = JSONhandler.readJSONObject(result, "windspeedMiles");
		String windDirection = JSONhandler.readJSONObject(result, "winddir16Point");
		String wind = windSpeed + " mph " + windDirection;
		
		// add the weather details to the hashmap
		widgetDetails.put("temp", temp + " F");
		widgetDetails.put("humidity", humidity + "%");
		widgetDetails.put("wind", wind);
		widgetDetails.put("zipcode", zipcode);
		
		return widgetDetails;
	}
	
	public void updateWidget(HashMap<String, String> widgetDetails)
	{
		// verify that the widget id is valid
		if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
		{
			// get the app widget manager instance
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			
			// create the remote view of the widget layout
			RemoteViews rViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
			rViews.setTextViewText(R.id.widget_current_temp, widgetDetails.get("temp"));
			rViews.setTextViewText(R.id.widget_humidity, widgetDetails.get("humidity"));
			rViews.setTextViewText(R.id.widget_wind, widgetDetails.get("wind"));
			
			// update the app widget manager with the remove view
			appWidgetManager.updateAppWidget(appWidgetId, rViews);
			
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			setResult(RESULT_OK, resultValue);
			FileSystem.writeStringFile(this, widgetDetails.get("zipcode"), "widget_zip", true);
			finish();
		}
	}
}
