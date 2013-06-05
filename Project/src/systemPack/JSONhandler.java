package systemPack;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONhandler {
	public static String readJSONObject(String JSONString, String key)
	{
		// create a container string for the json value
		String jsonValue = "";
		
		// builds and returns a JSON object from the string
		JSONObject json = returnJSONObject(JSONString);
		
		try {
			// set the JsonValue string by querying through the json object
			jsonValue = json.getJSONObject("data").getJSONArray("current_condition").getJSONObject(0).getString(key);
	
		} catch (JSONException e) {
			Log.e("ERROR", "JSON Exception error within 'readJSONObject()'");
		}
		
		return jsonValue;
	}
	
	public static JSONObject returnJSONObject(String JSONString)
	{
		// create a null JSON object
		JSONObject thisJSON = null;
		
		try {
			
			// create a new JSON object with the passed in JSON string
			thisJSON = new JSONObject(JSONString);
			
		} catch (JSONException e) {
			Log.e("ERROR", "JSON Exception error within 'returnJSONObject()'");
		}
		
		// return the object
		return thisJSON;
	}
}
