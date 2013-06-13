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

import systemPack.IOManager;
import systemPack.InterfaceManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends Activity {

	// instantiate the InterfaceManager singleton
	InterfaceManager UIFactory;
			
	// instantiate a new intent
	Intent detailsView;
		
	// setup the context
	Context _context;
	
	// Boolean for the connection status
	Boolean connected;
	
	// setup the radio group
	RadioGroup radios;
	RadioGroup querys;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.radioform);
		
		// call the InterfaceManager singleton constructor
		UIFactory = new InterfaceManager(this);
		
		// instantiate a new intent from the singleton
		detailsView = UIFactory.makeIntent(DetailActivity.class);
		
		// set the context
		_context = this;
		
		// checks if there is a network connection
		connected = IOManager.getConnectionStatus(this);
		
		// call the method for displaying the toast
		displayToast();
	
		// sets the radiogroups
		querys = (RadioGroup) findViewById(R.id.querychoice);
		radios = (RadioGroup) findViewById(R.id.locales);
		
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
					String selectedValue = rBtn.getText().toString();
					String queryValue = qBtn.getText().toString();
				
					// store the strings for the new intent
					detailsView.putExtra("selected", selectedValue);
					detailsView.putExtra("query", queryValue);
						
					// start the new activity
					startActivity(detailsView);
				}
			}
		});
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
