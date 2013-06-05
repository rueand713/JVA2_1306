/*
 * project 		Java2Project
 * 
 * package 		systemPack
 * 
 * @author 		Rueben Anderson
 * 
 * date			Jun 5, 2013
 * 
 */
package systemPack;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class IOManager {
	
	static boolean connectionStatus = false;
	static String connectionType = "No Connection";
	
	// getter method for retrieving the connectionType
	public static String getConnectionType(Context context)
	{
		// calls the method to check and retrieve the connection data
		fetchConnectionData(context);
				
		// return the connection type
		return connectionType;
	}
	
	// getter method for retrieving the connectionStatus
	public static boolean getConnectionStatus(Context context)
	{
		// calls the method to check and retrieve the connection data
		fetchConnectionData(context);
		
		// return the connection status
		return connectionStatus;
	}
	
	//  method for retrieving the connection details and setting the class fields
	private static void fetchConnectionData(Context context)
	{
		// setup the connectivity manager object
		ConnectivityManager conMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// instantiate the networkinfo object to hold the connectivity manager active network
		NetworkInfo net411 = conMngr.getActiveNetworkInfo();
		
		// check if the network info object is created
		if (net411 != null)
		{
			// the network info object is created now it checks for a connection
			// if there is a connection the connected and connectionType fields are set to reflect that data
			// otherwise the fields are set to their default values
			if (net411.isConnected())
			{
				connectionStatus = true;
				connectionType = net411.getTypeName();
			}
			else
			{
				connectionStatus = false;
				connectionType = "No Connection";
			}
		}
	}
	
	//  method for making a web request for string data
	public static String makeStringRequest(URL urlString)
	{
		// string object to hold the URL response text
		String responseText = "";
		
		try {
			// create a new URL connection object
			URLConnection connection = urlString.openConnection();
			
			// creates a new buffer input stream with the URL connection object
			BufferedInputStream bfStream = new BufferedInputStream(connection.getInputStream());
			
			// create a new byte array
			byte[] contentBytes = new byte[1024];
			
			// integer to handle the number of bytes read
			int readBytes = 0;
			
			// create a new string buffer object
			StringBuffer buffString = new StringBuffer();
			
			// loop to handle the appending of the string content to buffer
			while ((readBytes = bfStream.read(contentBytes)) != -1)
			{
				// set the response string to the string content read
				responseText = new String(contentBytes, 0, readBytes);
				
				// appends the new string content to the buffer string object
				buffString.append(responseText);
			}
			
			// set the responseText to the full buffered string content in buffer
			responseText = buffString.toString();
			
		} catch (IOException e) {
			Log.e("URL RESPONSE ERROR", "Server failed to respond to request");
		}
		
		// return the response string
		return responseText;
	}
	
}
