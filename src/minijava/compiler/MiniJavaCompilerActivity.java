package minijava.compiler;

import java.io.*;
import main.ParseException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MiniJavaCompilerActivity extends Activity {
	
	final static int CHOOSE_JAVA_REQUEST = 1;
	final static int CHOOSE_APK_REQUEST = 2;
	
	EditText pathEdit, resultEdit;
	ProgressDialog progress;
	CompilerAsyncTask task;
	
	private void init() {
		setContentView(R.layout.main);
        
		String tmp = null;
		if (pathEdit != null) {
			tmp = pathEdit.getText().toString();
		}
        pathEdit = (EditText)this.findViewById(R.id.editText);
		pathEdit.setText(tmp);
		
		tmp = "Compiler Information will be here.";
		if (resultEdit != null) {
			tmp = resultEdit.getText().toString();
		}
        resultEdit = (EditText)this.findViewById(R.id.editText1);
        resultEdit.setText(tmp);
        
        ((Button) findViewById(R.id.fileChooser)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MiniJavaCompilerActivity.this, FileChooserActivity.class);
				intent.putExtra("format", ".java");
				startActivityForResult(intent, CHOOSE_JAVA_REQUEST);
			}
		});
        
        Button btn0 = (Button)this.findViewById(R.id.Button0);
        btn0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final String src = pathEdit.getText().toString();
				if (!src.endsWith(".java")) {
					pathEdit.setText("Wrong Extended Name!");
				} else {

					ErrorInfo.init();
					WarningInfo.init();
					
					progress = new ProgressDialog(MiniJavaCompilerActivity.this);
					
					task = new CompilerAsyncTask() {

						@Override
						void onFinished() {
							if (progress != null)
								progress.dismiss();
							if (!ErrorInfo.getInfo().equals("")) {
								if (!ErrorInfo.getInfo().equals("No such File!\n")) 
									ErrorInfo.addlnInfo("===Type Check Error!===");
								resultEdit.setText(ErrorInfo.getInfo()+WarningInfo.getInfo());
							} else {
								resultEdit.setText("Compile completely! The apk file is at '"
									+src.replace(".java", ".apk")+"'\n"+WarningInfo.getInfo());
							}
						}
					};
					
					progress.setTitle("^_^");
					progress.setMessage("Compiling, please wait...");
					progress.setIndeterminate(true);
					progress.setCancelable(false);
					progress.show();
					
					task.compile(src);
				}
			}
		});
        
        Button btn1 = (Button)this.findViewById(R.id.Button1);
        btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				pathEdit.setText(null);
		        resultEdit.setText("Compiler Information will be here.");
			}
		});
        
        Button signButton = (Button) findViewById(R.id.sign);
		signButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signApk();
			}
		});

		Button installButton = (Button) findViewById(R.id.install);
		installButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				install();
			}
		});
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    
    private void signApk() {
    	Intent intent = new Intent();
    	intent.setClassName("kellinwood.zipsigner2", "kellinwood.zipsigner2.ZipPickerActivity");
    	startActivityForResult(intent, 0);
    }
    
    private void install() {
    	Intent intent = new Intent(this, FileChooserActivity.class);
    	intent.putExtra("format", ".apk");
    	startActivityForResult(intent, CHOOSE_APK_REQUEST);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode == Activity.RESULT_OK) {
    		switch (requestCode) {
    		case CHOOSE_JAVA_REQUEST:
    			((TextView) findViewById(R.id.editText))
    				.setText(data.getStringExtra("file"));
    			break;
    		case CHOOSE_APK_REQUEST:
    			Intent intent = new Intent(Intent.ACTION_VIEW);
    			intent.setDataAndType(Uri.parse("file://"+data.getStringExtra("file")),
    					"application/vnd.android.package-archive");
    			startActivity(intent);
    			break;
    		default:
    			
    		}	 
    	} else {
    	}
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
    	super.onConfigurationChanged(config);
    	init();
    }
}

abstract class CompilerAsyncTask extends AsyncTask<Void, Void, Void> {
	
	String mSrcFile = null;
	
	void compile(String srcFile) {
		mSrcFile = srcFile;
		super.execute();
	}
	
	abstract void onFinished();

	@Override
	protected Void doInBackground(Void... params) {
		
		MiniJavaCompiler mjc = new MiniJavaCompiler(mSrcFile);
		try {
			mjc.compile();
		} catch (ParseException e) {
			e.printStackTrace();
			ErrorInfo.addInfo(e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorInfo.addInfo(e.toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorInfo.addInfo(e.toString());
		}
		publishProgress();
		
		return null;
	}
	
	@Override
	protected final void onProgressUpdate(Void... params) {
		onFinished();
	}
}