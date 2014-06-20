package com.wutong.taxiapp.util;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.wutong.taxiapp_driver.R;

public class ActivityUtils {

	/**
	 * @param packageContext
	 * @param cls
	 */
	public static void startActivityAndFinish(Activity packageContext,
			Class<?> cls) {
		Intent intent = new Intent(packageContext, cls);
		packageContext.startActivity(intent);
		packageContext.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
		packageContext.finish();
	}

	/**
	 * @param packageContext
	 * @param cls
	 */
	public static void startActivity(Activity packageContext, Class<?> cls) {
		Intent intent = new Intent(packageContext, cls);
		packageContext.startActivity(intent);
		packageContext.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public static void startActivityCode(Activity packageContext, Class<?> cls,
			int code) {
		Intent intent = new Intent(packageContext, cls);
		intent.setFlags(code);
		packageContext.startActivity(intent);
	}

	public static void startActivityForResult(Context mContext, Class c,
			Activity act) {
		Intent intent = new Intent();
		intent = intent.setClass(mContext, c);
		act.startActivityForResult(intent, 0); // 只有这里不同
	}

	public static void startActivityForResult(Context mContext, Class c,
			String Key, Serializable serializable, int resultCode, Activity act) {
		Intent intent = new Intent();
		intent = intent.setClass(mContext, c);
		intent.putExtra(Key, serializable);
		intent.putExtra("ADDRESSTYPE", resultCode);
		act.startActivityForResult(intent, resultCode); // 只有这里不同
		act.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	public static void startActivityForSerializable(Context mContext, Class c,
			Serializable serializable, String key, Activity packageContext) {
		Intent intent = new Intent();
		intent = intent.setClass(mContext, c);
		intent.putExtra(key, serializable);
		packageContext.startActivity(intent);
		packageContext.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public static void startActivityForSerializable(Context mContext, Class c,
			Serializable serializable, String key, int num,
			Activity packageContext) {
		Intent intent = new Intent();
		intent = intent.setClass(mContext, c);
		intent.putExtra(key, serializable);
		intent.putExtra("NUM", num);
		packageContext.startActivity(intent);
		packageContext.overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	public static void finish(Activity mActivity) {
		mActivity.finish();
		mActivity.overridePendingTransition(R.anim.in_from_left,
				R.anim.out_to_right);

	}

}
