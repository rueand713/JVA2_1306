package systemPack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class FileSystem {
	// method for reading string data from the device
		public static String readStringFile(Context context, String name, Boolean isInternal)
		{
			// create the method fields required for reading the file
			File file;
			FileInputStream fileInput;
			String stringContent = "";
			
			try {
				// checks if the read is to be done for internal or external storage
				if (isInternal)
				{
					// create a new File object with the file name
					file = new File(name);
					
					// opens a FileInputStream for the file name
					fileInput = context.openFileInput(name);
				}
				else
				{
					// create a new File object using the application directory
					file = new File(context.getExternalFilesDir(null), name);
					
					// create a new FileInputStream with the file object
					fileInput = new FileInputStream(file);
				}
				
				// create a new BufferInputStream object
				BufferedInputStream bufStream = new BufferedInputStream(fileInput);
				
				// create a new byte array
				byte[] contentBytes = new byte[1024];
				
				// int for holding the number of bytes read
				int readBytes = 0;
				
				// create a StringBuffer object
				StringBuffer bufferString = new StringBuffer();
				
				try {
					
					// loop for appending string content from file
					while((readBytes = bufStream.read(contentBytes)) != -1)
					{
						// creates a new string for stringContent using the bytes read
						stringContent = new String(contentBytes, 0, readBytes);
						
						// appends the new string data to the buffer
						bufferString.append(stringContent);
					}
					
					// sets the string object to the full buffered string content in the bufferString object
					stringContent = bufferString.toString();
					
					// close the input stream
					fileInput.close();
					
				} catch (IOException e) {
					Log.e("READ ERROR", "Error reading string content from file.");
				}
				
			} catch (FileNotFoundException e) {
				Log.e("FILE NOT FOUND", "No file with the specified name was found.");
			}
			
			// return the object
			return stringContent;
		}
		
		// method for saving string data to device storage
		public static Boolean writeStringFile(Context context, String data, String name, Boolean isInternal)
		{
			// create the method fields required for writing the file
			File file;
			FileOutputStream fileOut;
			Boolean operationSuccess = true;
			
			try {
				
				// check if the write should be done to internal or external storage
				if (isInternal)
				{
					// open the file output stream for internal storage
					fileOut = context.openFileOutput(name, Context.MODE_PRIVATE);
				}
				else
				{
					// create a new file object in app directory with the passed in name
					file = new File(context.getExternalFilesDir(null), name);
					
					// open the file output stream for external storage
					fileOut = new FileOutputStream(file);
				}
				
				// convert the passed in data string into bytes
				// and write that to the specified storage
				fileOut.write(data.getBytes());
				
				// close the file output stream
				fileOut.close();
				
			} catch (IOException e) {
				operationSuccess = false;
				Log.e("FAILED WRITE", "File did not write successfully.");
			}
			
			// return whether the operation was successful or not
			return operationSuccess;
		}
		
		// method for reading string data from the device
			public static Object readObjectFile(Context context, String name, Boolean isInternal)
			{
				// create the method fields required for reading the file
				File file;
				FileInputStream fileInput;
				Object objectContent = new Object();
				
				try {
					// checks if the read is to be done for internal or external storage
					if (isInternal)
					{
						// create a new File object with the file name
						file = new File(name);
						
						// opens a FileInputStream for the file name
						fileInput = context.openFileInput(name);
					}
					else
					{
						// create a new File object using the application directory
						file = new File(context.getExternalFilesDir(null), name);
						
						// create a new FileInputStream with the file object
						fileInput = new FileInputStream(file);
					}
					
					// create a new object input stream
					ObjectInputStream objectInput = new ObjectInputStream(fileInput);
					
					try {
						
						// set the object to object read from stream
						objectContent = (Object) objectInput.readObject();
					} catch (ClassNotFoundException e) {
						Log.e("READ ERROR", "Invalid Java Object file");
					}
					
					// close the object input stream
					objectInput.close();
					
					// close the input stream
					fileInput.close();
						
					} catch (IOException e) {
						Log.e("READ ERROR", e.toString());
						
						return null;
					}
				
				// return the object
				return objectContent;
			}
		
		// method for saving object data to device storage
		public static Boolean writeObjectFile(Context context, Object data, String name, Boolean isInternal)
		{
			// create the method fields required for writing the file
			File file;
			FileOutputStream fileOut;
			ObjectOutputStream objectOut;
			Boolean operationSuccess = true;
			
			try {
				
				// check if the write should be done to internal or external storage
				if (isInternal)
				{
					// open the file output stream for internal storage
					fileOut = context.openFileOutput(name, Context.MODE_PRIVATE);
				}
				else
				{
					// create a new file object in app directory with the passed in name
					file = new File(context.getExternalFilesDir(null), name);
					
					// open the file output stream for external storage
					fileOut = new FileOutputStream(file);
				}
				
				// create a new object output stream object using the specified file output stream
				objectOut = new ObjectOutputStream(fileOut);
				
				// write the passed in object to the specified device storage
				objectOut.writeObject(data);
				
				// close the object output stream
				objectOut.close();
				
				// close the file output stream
				fileOut.close();
				
			} catch (IOException e) {
				operationSuccess = false;
				Log.e("FAILED WRITE", "File did not write successfully.");
			}
			
			// return whether the operation was successful or not
			return operationSuccess;
		}
		
		public static HashMap<String, String> setStorageHash(String key, String value, HashMap<String, String> storageHash)
		{
			// check for non null hashMap parameter
			if (storageHash == null)
			{
				// create a new hashmap object
				storageHash = new HashMap<String, String>();
			}
			
			// set the passed in key value pair to the hash
			storageHash.put(key, value);
			
			return storageHash;
		}
}
