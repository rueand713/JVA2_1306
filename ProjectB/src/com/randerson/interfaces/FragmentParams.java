package com.randerson.interfaces;

import android.content.Intent;
import android.view.View;

public interface FragmentParams {
	
	// method for receiving the intent and view data from the current weather fragment
	void getFragmentParams(Intent intent, View v);
	
	// method for receiving the view from the forecast fragment
	void receiveTableView(View v);
}

