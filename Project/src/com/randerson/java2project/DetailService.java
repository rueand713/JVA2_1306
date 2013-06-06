/*
 * project 		Java2Project
 * 
 * package 		com.randerson.java2project
 * 
 * @author 		Rueben Anderson
 * 
 * date			Jun 6, 2013
 * 
 */
package com.randerson.java2project;

import java.net.URL;

import systemPack.IOManager;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class DetailService extends IntentService {
	
	// token identifiers for the service class
	public static final String MESSENGER_KEY = "Messenger";
	public static final String URL_KEY = "Url";

	public DetailService() {
		super("DetailService");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Bundle mainBundle = intent.getExtras();
		URL url = (URL) mainBundle.get(URL_KEY);
		Messenger messenger = (Messenger) mainBundle.get(MESSENGER_KEY);
		
		
		// checks if there is a network connection
		boolean connected = IOManager.getConnectionStatus(this);
		
		// create a empty response string
		String response = "";
		
		if (connected == true)
		{
			// loop for making url request with URLs 
			//for (URL url:urls)
		//	{
				// response gets set for each request made (in this instance just one)
				response = IOManager.makeStringRequest(url);
			//}
		}
		
		// obtain message for the message object
		Message message = Message.obtain();
		
		// set the result ok to message argument 1
		message.arg1 = Activity.RESULT_OK;
		
		// set the response text to the message object
		message.obj = response;
		
		// try to send the message
		try {
			messenger.send(message);
		} catch (RemoteException e) {
			Log.e("REMOTE EXCEPTION", "Sending message in 'onHandleIntent()'");
		}
		
	}

}
