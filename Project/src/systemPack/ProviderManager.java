package systemPack;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderManager extends ContentProvider {

	// create the provider AUTHORITY string
	public static final String AUTHORITY = "com.randerson.java2project.providermanager";
	
	// create the constant JSON file key
	public static final String JSON_SAVE_FILE = "JsonWeather";
	
	public static class ProviderData implements BaseColumns
	{
		// create the constant string for the content uri
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/days");
		
		// create the constant strings for the content types
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.randerson.java2project.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.randerson.java2project.item";
		
		// define the colums
		public static final String DATE_COL = "DAY";
		public static final String TEMP_COL = "TEMPERATURE";
		public static final String WIND_COL = "WIND";
		public static final String COND_COL = "CONDITIONS";
		
		// create the projection string array
		public static final String[] PROJECTION = {"_Id", DATE_COL, TEMP_COL, WIND_COL, COND_COL};
		
		// constructor
		private ProviderData(){};
		
	}
	
	// create the uri matcher
	public static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	// create the constant return codes for the uri matcher
	public static final int ALL_DAYS = 1;
	public static final int SINGLE_DAY = 2;
	
	// add the matchable uri's to the uri matcher
	static 
	{
		uriMatcher.addURI(AUTHORITY, "days/", ALL_DAYS);
		uriMatcher.addURI(AUTHORITY, "days/#", SINGLE_DAY);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// throw error on provider calls for non supported operations
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		
		// create the string for holding the return value for the method
		String returnString = null;
		
		switch(uriMatcher.match(uri))
		{
		case ALL_DAYS:
			returnString = ProviderData.CONTENT_TYPE;
			break;
			
		case SINGLE_DAY:
			returnString = ProviderData.CONTENT_ITEM_TYPE;
			break;
			
			default:
				break;
		}
		
		return returnString;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// throw error on provider calls for non supported operations
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
		// create the MatrixCursor object
		MatrixCursor cursorResult = new MatrixCursor(ProviderData.PROJECTION);
		
		// read string from saved system file
		String jsonString = FileSystem.readStringFile(getContext(), JSON_SAVE_FILE, true);
		
		// create the json object from the saved file string
		JSONObject readObject = JSONhandler.returnJSONObject(jsonString);
		
		// create null json array
		JSONArray weatherArray = null;
		
		// try to retrieve the JSON array from the read json object
		try {
			weatherArray = readObject.getJSONObject("data").getJSONArray("weather");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// check if the array is empty or not
		// if empty, return the empty array otherwise continue to match the uri
		if (weatherArray == null)
		{
			return cursorResult;
		}
		
		// create a weather data hashmap
		HashMap<String, HashMap<String, String>> weatherData = new HashMap<String, HashMap<String, String>>();
		
		switch(uriMatcher.match(uri))
		{
		case ALL_DAYS:
			for (int i = 0; i < weatherArray.length(); i++)
			{
				// create a hashmap for holding the current weather conditions at the index i
				HashMap<String, String> thisCondition = new HashMap<String, String>();
				
				try {
					// get and set the JSON weather data into strings
					String date = weatherArray.getJSONObject(i).getString("date");
					String tempHi = weatherArray.getJSONObject(i).getString("tempMaxF");
					String tempLo = weatherArray.getJSONObject(i).getString("tempMinF");
					String description = weatherArray.getJSONObject(i).getJSONArray("weatherDesc").getJSONObject(0).getString("value");
					String winDir = weatherArray.getJSONObject(i).getString("winddir16Point");
					String winSpd = weatherArray.getJSONObject(i).getString("windspeedMiles");
					
					// put the individual conditions into the current weather hashmap
					thisCondition.put("date", date);
					thisCondition.put("temp_hi", tempHi);
					thisCondition.put("temp_lo", tempLo);
					thisCondition.put("condition", description);
					thisCondition.put("wind_dir", winDir);
					thisCondition.put("wind_spd", winSpd);
					
					//integer id key
					int ID = (i + 1);
					
					// create the object array of the weather data
					Object[] columnValues = {ID, date, (tempHi + " / " + tempLo), (winDir + " @ " + winSpd + "mph"), description};
					
					// add the row of data to the matrix cursor
					cursorResult.addRow(columnValues);
				
				// set the hashmap key for the weather data
				String key = "day" + (i+1);
				
				// put the current weather hashmap inside the master weather data hashmap
				weatherData.put(key, thisCondition);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// save the hash to internal storage
			FileSystem.writeObjectFile(getContext(), weatherData, "history", false);
			break;
			
		case SINGLE_DAY:
			
			break;
			
			default:
				break;
		}
		
		
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// throw error on provider calls for non supported operations
		throw new UnsupportedOperationException();
	}

	
	
}
