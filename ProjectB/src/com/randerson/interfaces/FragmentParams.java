package com.randerson.interfaces;

import android.view.View;

public interface FragmentParams {
	
	// method for receiving the view data from the current weather fragment
	void getWeatherView(View v);
	
	// method for receiving the view from the forecast fragment
	void receiveTableView(View v);
	
	// method for starting the intent in parent activity
	void startResultActivity();
}
