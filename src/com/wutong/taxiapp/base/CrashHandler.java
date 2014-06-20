package com.wutong.taxiapp.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

/**
 * UncaughtException处理�?当程序发生Uncaught异常的时�?有该类来接管程序,并记录发送错误报�?
 * 
 * @author luo
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";
	// 错误报告文件的扩展名
	private static final String CRASH_REPORTER_EXTENSION = ".log";
	// 错误报告父文件夹�?
	private static final String CRASH_REPORTER_DIR = "crash";
	// 系统默认的UncaughtException处理�?
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler crashHandler = null;
	// 程序的Context对象
	private Context mContext;
	// 用来存储设备信息和异常信�?
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化日�?作为日志文件名的�?���?
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有�?��CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (crashHandler == null) {
			crashHandler = new CrashHandler();
			return crashHandler;
		}
		return crashHandler;
	}

	/**
	 * 初始�?
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理�?
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理�?
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处�?
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				Log.e(TAG, "error : ", e);
//			}
			// �?��程序
			// loading();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处�?收集错误信息 发�?错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信�?
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				// Toast.makeText(mContext, "异常�?��", Toast.LENGTH_LONG)
				// .show();
				MyLogger.e("CrashHandler", "异常�?��");
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		MyLogger.e("CrashHandler", "loading");
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2SDCard(ex);
		return true;
	}

	/**
	 * 重新加载
	 * 
	 * @throws
	 */
	private void loading() {
		// MyLogger.e(TAG, "程序崩溃后获得loading");
		// String json = null;
		// TysxOA oa = new TysxOA();
		// json = oa.loading(mContext, ComParams.devid, ComParams.appid,
		// ComParams.pkey, ComParams.HTTP, ComParams.channelID);
		// Map<String, String> map = null;
		// try {
		// map = JsonAnalytic.getInstance().analyseLoginInitInfo(json);
		// String token = map.get("token");
		// MyLogger.e(TAG, "程序崩溃后获�?token:" + token);
		// if (map.containsKey("uid")) {
		// ComParams.uid = map.get("uid");
		// ComParams.bind = map.get("bind");
		// ComParams.phoneStatus = map.get("phoneStatus");
		// ComParams.USERNICKNAME = map.get("nickName");
		// MyLogger.i(TAG, "电信号码,无需登录,uid是：" + ComParams.uid
		// + ",phoneStatus是：" + ComParams.phoneStatus + ",bind是："
		// + ComParams.bind);
		// ComParams.SILENTLOGIN = true;
		// ComParams.isLogin = true;
		// } else {
		// MyLogger.i(TAG, "------非电信号�?未登�?-----");
		// // LoadMainUI();
		// }
		//
		// } catch (OWNException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 把错误报告发送给服务�?包含新产生的和以前没发�?�?
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));
			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// 删除已发送的报告
			}
		}
	}

	private void postReport(File file) {
		// TODO 发�?错误报告到服务器
	}

	/**
	 * 获取错误报告文件�?
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * 保存错误信息到文件中(SDCard�?
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务�?
	 */
	private String saveCrashInfo2SDCard(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		FileOutputStream fos = null;
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp
					+ CRASH_REPORTER_EXTENSION;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()
						+ File.separator + CRASH_REPORTER_DIR + File.separator;// "/sdcard/crash/";
				MyLogger.d(TAG, "path=" + path);
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				// 暂时没有上传接口，删除上�?��保存的错误报告，让其只保留最新的�?��报告�?
				forTestDelete(path);

				fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.flush();
			}
			return fileName;
		} catch (Exception e) {
			MyLogger.d(TAG, "Exception---saveCrashInfo2SDCard");
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				MyLogger.d(TAG, "IOException---saveCrashInfo2SDCard");
			}
		}
		return null;
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		FileOutputStream fos = null;
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp
					+ CRASH_REPORTER_EXTENSION;
			String path = mContext.getFilesDir() + File.separator;

			// 暂时没有上传接口，删除上�?��保存的错误报告，让其只保留最新的�?��报告�?
			forTestDelete(path);

			fos = new FileOutputStream(path + fileName);
			fos.write(sb.toString().getBytes());
			fos.flush();
			return fileName;
		} catch (Exception e) {
			MyLogger.d(TAG, "Exception---saveCrashInfo2File");
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				MyLogger.d(TAG, "IOException---saveCrashInfo2File");
			}
		}
		return null;
	}

	/**
	 * 删除上一次保存的错误报告，让文件夹只保留�?��的一个报�?
	 * 
	 * @param ex
	 * @return
	 */
	private boolean forTestDelete(String path) {
		File dir = new File(path);
		String[] names = dir.list();
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				new File(path + names[i]).delete();
			}
		}
		return true;
	}
}
