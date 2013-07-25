package com.randerson.java2androidweather;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import systemPack.FileSystem;
import systemPack.JSONhandler;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	final String MESSENGER_KEY = "Messenger";
	final String URL_KEY = "Url";
	
	Context CONTEXT;
	Handler requestHandler;
	Messenger intentMessenger;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		// global reference to the application context
		CONTEXT = context;
		
		String zipcode = FileSystem.readStringFile(context, "widget_zip", true);
		
		for (int i = 0; i < appWidgetIds.length; i++)
		{
			serviceCall(zipcode, appWidgetManager, appWidgetIds[i]);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

	@SuppressLint("HandlerLeak")
	public void serviceCall(String zipcode, AppWidgetManager widgetManager, int widgetId)
	{
		final String zipCode = zipcode;
		final AppWidgetManager appWidgetManager = widgetManager;
		final int appWidgetId = widgetId;
		
		// api request string parts
		// the strings will be concatenated with the selected location and passed as the request
		final String apiKey = CONTEXT.getString(R.string.apikey);
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
				
				if (msg.arg1 == 0 && msg.obj != null)
				{
					// grab the result string
					String result = (String) msg.obj;
					
					// pass in the result string to parse out the json data
					// call the method to update the widget data
					updateWidgetDetails(appWidgetManager, appWidgetId, parseJson(result, zipCode));
				}
			}
		};
		
		// create the messenger object
		intentMessenger = new Messenger(requestHandler);
		
		// make the service intent
		Intent intentService = new Intent(CONTEXT, DetailService.class);
		
		// put the messenger into the intent
		intentService.putExtra(URL_KEY, weatherURL).putExtra(MESSENGER_KEY, intentMessenger);
		
		// start up the service
		CONTEXT.startService(intentService);
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
		
		return widgetDetails;
	}
	
	public void updateWidgetDetails(AppWidgetManager appWidgetManager, int appWidgetId, HashMap<String, String> widgetDetails)
	{
		Intent weatherActivity = new Intent(CONTEXT, MainActivity.class);
        weatherActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(CONTEXT, 0, weatherActivity, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        RemoteViews rViews = new RemoteViews(CONTEXT.getPackageName(), R.layout.widget_layout);
        
        rViews.setTextViewText(R.id.widget_current_temp, widgetDetails.get("temp"));
		rViews.setTextViewText(R.id.widget_humidity, widgetDetails.get("humidity"));
		rViews.setTextViewText(R.id.widget_wind, widgetDetails.get("wind"));
        
        rViews.setOnClickPendingIntent(rViews.getLayoutId(), pendingIntent);

        // Tell the AppWidgetManager to perform an update on the current app widget
        appWidgetManager.updateAppWidget(appWidgetId, rViews);
	}
	
}
