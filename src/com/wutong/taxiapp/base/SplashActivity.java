package com.wutong.taxiapp.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.iss.exception.NetRequestException;
import com.iss.loghandler.Log;
import com.wutong.taxiapp.MainActivity;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.activity.LoginActivity;
import com.wutong.taxiapp.activity.RegisterStartActivity;
import com.wutong.taxiapp.activity.TaxiMainActivity;
import com.wutong.taxiapp.domain.ResponseVersion;
import com.wutong.taxiapp.net.TaxiLib;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.ErrorInfo;
import com.wutong.taxiapp.util.NetManager;
import com.wutong.taxiapp.util.SharedConfig;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

//软件入口，闪屏界面
public class SplashActivity extends Activity implements ImBaseSocketNet {
	private boolean first; // 判断是否第一次打�?���?
	private View view;
	private Context context;
	private Animation animation;
	private NetManager netManager;
	private SharedPreferences shared;
	private static int TIME = 1000; // 进入主程序的延迟时间

	private TextView tv_splash_version;
	private ResponseVersion info;
	private Runnable runnable;

	private static final int GET_INFO_SUCCESS = 10;
	private static final int SERVER_ERROR = 11;
	private static final int SERVER_URL_ERROR = 12;
	private static final int IO_ERROR = 13;
	private static final int XML_PARSER_ERROR = 14;

	public static final int UPDATA_CLIENT = 0;
	public static final int GET_UNDATAINFO_ERROR = 1;
	public static final int DOWN_ERROR = 2;

	protected static final String TAG = "SplashActivity";

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case XML_PARSER_ERROR:
				// Toast.makeText(getApplicationContext(), "XML解析异常",
				// Toast.LENGTH_LONG).show();
				loginMainUI(); // 加载主界�?
				break;
			case IO_ERROR:
				// Toast.makeText(getApplicationContext(), "IO异常",
				// Toast.LENGTH_LONG).show();
				loginMainUI(); // 加载主界�?
				break;
			case SERVER_URL_ERROR:
				// Toast.makeText(getApplicationContext(), "服务器URL出错",
				// Toast.LENGTH_LONG).show();
				loginMainUI(); // 加载主界�?
				break;
			case SERVER_ERROR:
				// Toast.makeText(getApplicationContext(), "服务器异�?,
				// Toast.LENGTH_LONG).show();
				loginMainUI();// 加载主界�?
				break;
			case GET_INFO_SUCCESS:
				String serverVersion = info.getVersion(); // 取得服务器上的版本号
				String currentVersion = getVersion(); // 取得当前应用的版本号
				if (currentVersion.equals(serverVersion)) { // 判断两个版本号是否相�?
					Log.i(TAG, "版本号相同，进入主界面！");
					loginMainUI(); // 如果版本号相同，则进入主界面
				} else {
					Log.i(TAG, "版本号不相同，对话框");
					showUpdateDialog(); // 如果版本号不相同，则加载更新对话�?
				}
				break;
			case DOWN_ERROR:
				ToastUtils.toast(getApplicationContext(), "下载新版本失败");
				loginMainUI();// 加载主界�?
				break;
			default:
				break;
			}
		}

	};
	private IApplication iApplication;
	private TaxiLib taxiLib;

	// 显示升级对话�?
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this); // 实例化对话框
		builder.setIcon(getResources().getDrawable(R.drawable.app_download));
		// 添加图标
		builder.setTitle("升级提示"); // 添加标题
		builder.setMessage("版本升级"); // 添加内容
		builder.setPositiveButton("升级", new OnClickListener() { // 点击升级时的操作方法
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Log.i(TAG, "升级,地址" + info.getApkurl());
						downLoadApk();
					}
				});
		builder.setNegativeButton("取消", new OnClickListener() { // 点击取消时的操作方法
					@Override
					public void onClick(DialogInterface dialog, int which) {
						loginMainUI();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = getFileFromServer(
							"http://down.zhihuiapp.com:7002/"
									+ "/APKPackage/complanyApp.apk", pd);
					sleep(1000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(),
					"updata.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 取得应用的版本号
	 * 
	 * @return
	 */
	public String getVersion() {
		PackageManager pm = getPackageManager(); // 取得包管理器的对象，这样就可以拿到应用程序的管理对象
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0); // 得到应用程序的包信息对象
			return info.versionName; // 取得应用程序的版本号
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// 此异常不会发�?
			return "";
		}
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名
	 * @return true 在运false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// loadConfig();

		IApplication.getInstance().addActivity(this);
		
		// Intent intent = new Intent(this, Google.class);
		// 启动服务
		// startService(intent);
		view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		context = this; // 得到上下问
		iApplication = (IApplication) getApplication();
		shared = iApplication.sharedConfig.GetConfig(); // 得到配置文件
		netManager = new NetManager(context); // 得到网络管理
		taxiLib = new TaxiLib(context, SplashActivity.this);
	}

	@Override
	protected void onResume() {
		taxiLib.registerReciver();
		checkVersion();
		super.onResume();
	}

	public void onPause() {
		taxiLib.unRegisterReciver();
		super.onPause();
	}

	// 进入主程序
	private void loginMainUI() {

		// 这里监听动画结束的动作，在动画结束的时�?�?���?��线程，这个线程中绑定�?��Handler,�?
		// 在这个Handler中调用goHome方法，�?通过postDelayed方法使这个方法延�?00毫秒执行，达�?
		// 达到持续显示第一�?00毫秒的效�?
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent;
				// 如果第一次，则进入引导页WelcomeActivity
				if (first) {
					iApplication.sharedConfig.setBoolean(SharedConfig.ISFIRST,
							false);

					ActivityUtils.startActivityAndFinish(SplashActivity.this,
							WelcomeActivity.class);

				} else {
					ActivityUtils.startActivityAndFinish(SplashActivity.this,
							LoginActivity.class);

				}
			}
		}, TIME);
	}

	// �?��版本更新
	private void checkVersion() {
		if (netManager.isOpenNetwork()) {
			// 如果网络可用则判断是否第�?��进入，如果是第一次则进入欢迎界面
			first = shared.getBoolean(SharedConfig.ISFIRST, true);
			// 设置动画效果是alpha，在anim目录下的alpha.xml文件中定义动画效�?
			animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
			// 给view设置动画效果
			view.startAnimation(animation);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// new Handler().postDelayed(new Runnable() {
					// @Override
					// public void run() {
					// Intent intent;
					// 如果第一次，则进入引导页WelcomeActivity
					// if (first) {
					// intent = new Intent(SplashActivity.this,
					// WelcomeActivity.class);
					//
					// } else {
					// intent = new Intent(SplashActivity.this,
					// MainActivity.class);
					// }
					// startActivity(intent);
					// 设置Activity的切换效�?
					// overridePendingTransition(R.anim.in_from_right,
					// R.anim.out_to_left);
					// SplashActivity.this.finish();
					// }
					// }, TIME);
					// }
					MyLogger.i("sdf", "检测版本号");
					// TODO Auto-generated method stub
					try {
						taxiLib.requestVersion();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					runnable = new Runnable() {

						@Override
						public void run() {
							Message msg = new Message();
							msg.what = IO_ERROR;
							handler.sendMessage(msg);
						}
					};
					handler.postDelayed(runnable, 20000);

				}
			});
		} else {
			// 如果网络不可用，则弹出对话框，对网络进行设置
			Builder builder = new Builder(context);
			builder.setTitle("没有可用的网络");
			builder.setMessage("是否对网络进行设置?");
			builder.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = null;
							try {
								String sdkVersion = android.os.Build.VERSION.SDK;
								if (Integer.valueOf(sdkVersion) > 10) {
									intent = new Intent(
											android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								} else {
									intent = new Intent();
									ComponentName comp = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									intent.setComponent(comp);
									intent.setAction("android.intent.action.VIEW");
								}
								SplashActivity.this.startActivity(intent);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SplashActivity.this.finish();
						}
					});
			builder.show();
		}
	}

	// 加载配置文件
	void loadConfig() {
		InputStream stream = null;
		try {
			stream = this.getApplicationContext().getAssets()
					.open("config.properties");

			Properties pp = new Properties();
			pp.load(stream);
			// IApplication.SERVERADDRESS = new String(pp.getProperty(
			// "SERVERADDRESS", "").getBytes("ISO-8859-1"), "utf-8");
			// IApplication.COMPANYID = new String(pp.getProperty("COMPANYID",
			// "")
			// .getBytes("ISO-8859-1"), "utf-8");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.close();
					stream = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void acceptResult(JSONObject jsonObject) {

		MyLogger.i("result", "接受数据");
		handler.removeCallbacks(runnable);
		
		if(!jsonObject.optString("response").equals("1000")){
			try {
				info = taxiLib.parserVersion(jsonObject);
				Message msg = new Message();
				msg.what = GET_INFO_SUCCESS;
				handler.sendMessage(msg);
			} catch (NetRequestException e) {
				e.getError().print(context);
				Message msg = new Message();
				msg.what = IO_ERROR;
				handler.sendMessage(msg);
			}
			
		}

	}

	@Override
	public void onBackPressed() {

		IApplication.getInstance().shutDown(this);

	}

}
