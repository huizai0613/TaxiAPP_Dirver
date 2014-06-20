package com.wutong.taxiapp.base;

import android.util.Log;

public class MyLogger {
	
	
	public static boolean isDebug = true;
	private static int VERBOSE = 5;
	private static int mDEBUG = 4;
	private static int INFO = 3;
	private static int WARN = 2;
	private static int ERROR = 1;

	private static int LOG_LEVEL = 10;

	public static void v(String tag, String msg) {
		if (LOG_LEVEL >= VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOG_LEVEL >= mDEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (LOG_LEVEL >= INFO) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (LOG_LEVEL >= WARN) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOG_LEVEL >= ERROR) {
			Log.e(tag, msg);
		}
	}
	
	
	// private static final String TAG = "MyLogger";
	/**
	 * 显示Log信息（带行号�?
	 * 
	 * @param logLevel
	 *            1 v ; 2 d ; 3 i ; 4 w ; 5 e .
	 * @param info
	 *            显示的log信息
	 */
	public static void showLogWithLineNum(int logLevel, String info) {
		String[] infos = getAutoJumpLogInfos();
		switch (logLevel) {
		case 1:
			if (isDebug)
				Log.v(infos[0], info + " : " + infos[1] + infos[2]);
			break;
		case 2:
			if (isDebug)
				Log.d(infos[0], info + " : " + infos[1] + infos[2]);
			break;
		case 3:
			if (isDebug)
				Log.i(infos[0], info + " : " + infos[1] + infos[2]);
			break;
		case 4:
			if (isDebug)
				Log.w(infos[0], info + " : " + infos[1] + infos[2]);
			break;
		case 5:
			if (isDebug)
				Log.e(infos[0], info + " : " + infos[1] + infos[2]);
			break;
		}
	}

	/**
	 * 获取打印信息�?��方法名，行号等信�?
	 * 
	 * @return
	 */
	private static String[] getAutoJumpLogInfos() {
		String[] infos = new String[] { "", "", "" };
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (elements.length < 5) {
			Log.e("MyLogger", "Stack is too shallow!!!");
			return infos;
		} else {
			infos[0] = elements[4].getClassName().substring(
					elements[4].getClassName().lastIndexOf(".") + 1);
			infos[1] = elements[4].getMethodName() + "()";
			infos[2] = " at (" + elements[4].getClassName() + ".java:"
					+ elements[4].getLineNumber() + ")";
			return infos;
		}
	}


}
