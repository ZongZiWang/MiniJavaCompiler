package minijava.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileChooserActivity extends ListActivity {
	
	File root;
	ListView lv;
	String format;
	int icon = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		format = getIntent().getStringExtra("format");
		if (format.equals(".java")) {
			icon = R.drawable.java;
		} else {
			icon = R.drawable.android;
		}
		
		lv = this.getListView();

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state) || 
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			root = Environment.getExternalStorageDirectory();
			setListView(root);	
		} else {
			Toast.makeText(this, "Please mount SD card first.", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private void setListView(File file) {
		if (file.isDirectory()) {
			((TextView) findViewById(R.id.list_path)).setText(file.toString());
			File[] files = file.listFiles();
			List<File> list = new ArrayList<File>();
			if (files != null)
				for (File f : files) 
					list.add(f);
			
			lv.setAdapter(new FileListAdapter(file, list));
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					File f = (File) lv.getItemAtPosition(position);
					if (f.isDirectory())
						setListView(f);
					else {
						Intent intent = new Intent();
						intent.putExtra("file", f.getAbsolutePath());
						setResult(Activity.RESULT_OK, intent);
						finish();
					}
				}
			});
		}
	}

	private class FileListAdapter extends BaseAdapter {
		
		File path;
		List<File> files;
		
		FileListAdapter(File path, List<File> files) {
			this.path = path;
			this.files = files;
			List<File> removed = new ArrayList<File>();
			for (File f : files) {
				if (f.isDirectory() && !f.getName().startsWith("."))
					continue;
				if (f.isFile() && f.getName().endsWith(format) && !f.getName().startsWith("."))
					continue;
				removed.add(f);
			}
			files.removeAll(removed);
			removed.clear();
			Collections.sort(files, new Comparator<File>() {

				@Override
				public int compare(File f0, File f1) {
					return f0.getName().compareToIgnoreCase(f1.getName());
				}
			});

			if (!path.toString().equals(root.toString()))
				files.add(0, path.getParentFile());
		}

		@Override
		public int getCount() {
			return files.size();
		}

		@Override
		public Object getItem(int position) {
			return files.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			View v = LayoutInflater.from(FileChooserActivity.this)
					.inflate(R.layout.item, null);
			File f = files.get(position);
			String name = (f.toString().equals(path.getParent()))?"..":f.getName();
			((TextView) v.findViewById(R.id.item_text)).setText(name);
			if (f.isFile()) {
				((ImageView) v.findViewById(R.id.item_icon))
					.setImageResource(icon);
			}
			return v;
		}
		
	}
}