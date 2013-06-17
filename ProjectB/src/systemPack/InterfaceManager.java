/*
 * project 		Java2AndroidWeather
 * 
 * package 		systemPack
 * 
 * @author 		Rueben Anderson
 * 
 * date			Jun 17, 2013
 * 
 */
package systemPack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class InterfaceManager {

	// class field for method context referencing
	private Context _context;
	
	// constructor method for the singleton
	public InterfaceManager(Context context)
	{
		this._context = context;
	}
	
	// method for creating and returning an intent
	public Intent makeIntent(Class<?> activity)
	{
		Intent thisIntent = new Intent(_context, activity);
		
		return thisIntent;
	}
	
	// method for creating and returning a linear layout object
			public LinearLayout createLinearLayout (boolean isHorizontal, boolean wrapContent)
			{
				// create a new linear layout object for the passed in context
				LinearLayout thisLayout = new LinearLayout(_context);
				
				// create a null layout param object
				LinearLayout.LayoutParams layoutParams;
				
				// check if the orientation should be horizontal or vertical
				if (isHorizontal)
				{
					// set the orientation horizontally
					thisLayout.setOrientation(LinearLayout.HORIZONTAL);
				}
				else
				{
					// set the orientation vertically
					thisLayout.setOrientation(LinearLayout.VERTICAL);
				}
				
				// check if the content should wrap or match its parent
				if (wrapContent)
				{
					// define the layout param object to wrap
					layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
					// set the layout param for the linear layout
					thisLayout.setLayoutParams(layoutParams);
				}
				else
				{
					// define the layout param object to match its parent
					layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
					
					// set the layout param for the linear layout
					thisLayout.setLayoutParams(layoutParams);
				}
				
				// return the object
				return thisLayout;
			}
			
			// method for creating and returing an editText object
			public EditText createEditText(String hint, int id)
			{
				// creates a new editText object
				EditText thisEditText = new EditText(_context);
				
				// set the editText object hint
				thisEditText.setHint(hint);
				
				// check for valid id to set otherwise ignores id setting
				if (id > 0) 
				{
					// set the editText object identifier
					thisEditText.setId(id);
				}
				
				// return the object
				return thisEditText;
			}
			
			// method for creating and returning a textView object
			public TextView createTextView(String text, int id)
			{
				// creates a new textView object
				TextView thisTextView = new TextView(_context);
				
				// sets the text for the textView object
				thisTextView.setText(text);
				
				// check for valid id to set otherwise ignores id setting
				if (id > 0) 
				{
					// sets the textView object identifier
					thisTextView.setId(id);
				}
				
				// return the object
				return thisTextView;
			}
			
			// method for creating and returning a button object
			public Button createButton(String text, int id)
			{
				// creates a new button object
				Button thisButton = new Button(_context);
				
				// sets the button text
				thisButton.setText(text);

				// check for valid id to set otherwise ignores id setting
				if (id > 0)
				{
					// sets the button object identifier
					thisButton.setId(id);
				}
				
				// return the object
				return thisButton;
			}
			
			// method for creating and returning a radio group
			public RadioGroup createRadioGroup(String[] values, int id)
			{
				// create a new radio group object
				RadioGroup radios = new RadioGroup(_context);
				
				for (int i = 0; i < values.length; i++)
				{
					// create a new radio button object
					RadioButton rb = new RadioButton(_context);
					
					// set the text of that radio button
					rb.setText(values[i]);
					
					// set the radio button id to one more than its index
					rb.setId(i+1);
					
					// add the radio button to the group
					radios.addView(rb);
				}
				
				// check for a valid id to set otherwise ignore
				if (id > 0)
				{
					// set the radio group object identifier
					radios.setId(id);
				}
				
				// return the object
				return radios;
			}
			
			// method for creating and returning a spinner object
			public Spinner createSpinner(String[] values, int id)
			{
				// create a spinner object
				Spinner spinner = new Spinner(_context);
				
				// create a layout params object
				LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
				
				// set the layout params for the spinner
				spinner.setLayoutParams(params);
				
				// create an ArrayAdapter object
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, values);
				
				// set the spinner dropdown resource
				adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				
				// set the spinner adapter
				spinner.setAdapter(adapter);
				
				// return the object
				return spinner;
			}
			
			@SuppressLint("ShowToast")
			public Toast createToast(CharSequence text, boolean isLongToast)
			{
				// integer for the toast duration
				int duration;
				
				// check whether the toast should be long or short
				if (isLongToast)
				{
					duration = Toast.LENGTH_LONG;
				}
				else
				{
					duration = Toast.LENGTH_SHORT;
				}
				
				// create a new toast object
				Toast newToast = Toast.makeText(_context, text, duration);
				
				return newToast;
			}
	
}