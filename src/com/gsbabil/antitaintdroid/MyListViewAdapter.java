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
