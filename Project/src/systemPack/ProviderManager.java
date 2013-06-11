package systemPack;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderManager extends ContentProvider {

	// create the provider AUTHORITY string
	public static final String AUTHORITY = "com.randerson.java2project.providermanager";
	
	public static class ProviderData implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/days");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.randerson.java2project.item";
		
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// throw error on provider calls for non supported operations
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
