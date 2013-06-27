package com.randerson.interfaces;

import android.content.Intent;
import android.view.View;

public interface FragmentParams {
	
	// method for receiving the view data from the current weather fragment
	void getWeatherView(View v);
	
	// method for receiving the view from the forecast fragment
	void receiveTableView(View v);
	
	// method for starting the intent in parent activity
	void startResultActivity(Intent intent);
	
	// method for updating the second landscape fragment
	void changeFragmentView(String bool);
}
