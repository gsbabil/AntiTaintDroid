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

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Credit: http://www.vogella.com/articles/AndroidListView/article.html
 */
public class MyListViewAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final List<String> names;

	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}

	public MyListViewAdapter(Activity context, List<String> names) {
		super(context, R.layout.listview_row_layout, names);
		this.context = context;
		this.names = names;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.listview_row_layout, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView
					.findViewById(R.id.listview_row_text);
			viewHolder.image = (ImageView) rowView
					.findViewById(R.id.listview_row_icon);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		String row_text_label = names.get(position);
		holder.text.setText(row_text_label);

		holder.image.setImageResource(R.drawable.ic_launcher);

		if (holder.text.getText() == "Tainted Variable Test") {
			holder.image.setImageResource(R.drawable.ic_launcher);
		} else if (holder.text.getText() == "File Read/Write Test") {
			holder.image.setImageResource(R.drawable.file);
		} else if (holder.text.getText() == "Simple Encoding Trick") {
			holder.image.setImageResource(R.drawable.encode);
		} else if (holder.text.getText() == "Shell Command Trick") {
			holder.image.setImageResource(R.drawable.shell2);
		} else if (holder.text.getText() == "File+Shell Hybrid Trick") {
			holder.image.setImageResource(R.drawable.fileshell);
		} else if (holder.text.getText() == "Bitmap Cache Trick") {
			holder.image.setImageResource(R.drawable.ram);
		} else if (holder.text.getText() == "Timekeeper Trick") {
			holder.image.setImageResource(R.drawable.timer2);
		} else if (holder.text.getText() == "Count-to-X Trick") {
			holder.image.setImageResource(R.drawable.count);
		} else if (holder.text.getText() == "File Length Trick") {
			holder.image.setImageResource(R.drawable.filelen);
		} else if (holder.text.getText() == "Clipboard Trick") {
			holder.image.setImageResource(R.drawable.clipboard);
		} else if (holder.text.getText() == "Bitmap Pixel Trick") {
			holder.image.setImageResource(R.drawable.pixel);
		} else if (holder.text.getText() == "Text Scaling Trick") {
			holder.image.setImageResource(R.drawable.scaling);
		} else if (holder.text.getText() == "Exception/Error Trick") {
			holder.image.setImageResource(R.drawable.exception);
		} else if (holder.text.getText() == "Direct Buffer Trick") {
			holder.image.setImageResource(R.drawable.buffer);
		} else if (holder.text.getText() == "Remote Dex Trick") {
			holder.image.setImageResource(R.drawable.remote_dex);
		} else if (holder.text.getText() == "Remote Control Trick") {
			holder.image.setImageResource(R.drawable.remote_control);
		} else if (holder.text.getText() == "File LastModified Trick") {
			holder.image.setImageResource(R.drawable.last_modified);
		}
		else {
			holder.image.setImageResource(R.drawable.batman);
		}
		return rowView;
	}
}
