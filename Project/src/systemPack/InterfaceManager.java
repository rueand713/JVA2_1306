package systemPack;

import android.content.Context;
import android.content.Intent;

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
	
}
