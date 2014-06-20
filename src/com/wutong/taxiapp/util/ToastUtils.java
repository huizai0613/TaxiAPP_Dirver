package com.wutong.taxiapp.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wutong.taxiapp_driver.R;


public class ToastUtils {

	public static void toast(Context mContext, String msg) {

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.userdefinedtoast, null);
		// TextView txtView_Title = (TextView)
		// view.findViewById(R.id.txt_Title);
		TextView txtView_Context = (TextView) view
				.findViewById(R.id.txt_context);
		// ImageView imageView = (ImageView)
		// view.findViewById(R.id.image_toast);
		txtView_Context.setText(msg);
		Toast toast = new Toast(mContext);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
		toast.show();

	}

}
