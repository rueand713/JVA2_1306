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

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	// create a null textview object for the forecast title
	TextView headerText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);
		
		// set the header textview text
		headerText = (TextView) findViewById(R.id.forcastheader);
		headerText.setText("Forecast");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
