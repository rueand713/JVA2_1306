package com.randerson.java2androidweather;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class WidgetConfigActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// variables
		Intent intent = getIntent();
		int appWidgetId = 0;
		String zipCode = null;
		
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
			// reference the zip code edit text from layout
			EditText zipField = (EditText) findViewById(R.id.zip_data);
			
			// verify that the zipfiled object is valid
			if (zipField != null)
			{
				// set the zip to the string data entered into the edit text
				zipCode = zipField.getText().toString();
			}
		}
	}

	
}
