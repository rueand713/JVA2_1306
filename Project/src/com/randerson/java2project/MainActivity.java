package com.randerson.java2project;

import systemPack.InterfaceManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	// create a new InterfaceManager object
	InterfaceManager UIFactory;
			
	// instantiate a new intent
	Intent detailsView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// create a new InterfaceManager object
		UIFactory = new InterfaceManager(this);
		
		// instantiate a new intent
		detailsView = UIFactory.makeIntent(DetailActivity.class);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
