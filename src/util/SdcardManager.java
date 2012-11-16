package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class SdcardManager {
	
	private final static String LOG_TAG = "sdcard";
	
	public final static String ROOT_PATH = Environment
			.getExternalStorageDirectory().toString()+"/developer";
	
	public static FileOutputStream getFileOutputStream(String fileName) {
		
		fileName = fileName.replace("/", "");
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			//File external = Environment.getExternalStorageDirectory();
			File file = new File(ROOT_PATH, fileName);
			if (Mode.isDebugMode()) Log.i(LOG_TAG, file.toString());
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(LOG_TAG, "create file failed: "+e.toString());
					return null;
				}
			}
			if (!file.canWrite()) {
				Log.e(LOG_TAG, "file not writable");
				return null;
			}
			try {
				FileOutputStream fos = new FileOutputStream(file);
				return fos;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(LOG_TAG, "file not found");
				return null;
			}
		} else {
			Log.e(LOG_TAG, "sdcard not mounted");
			return null;
		}
	}
	
	public static String getAbsoluteFilePathAndName(String fileName) {
		return ROOT_PATH + "/" + fileName;
	}
}