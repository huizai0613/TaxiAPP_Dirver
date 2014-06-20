package com.wutong.taxiapp.IA;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.net.MessageBackReciver;
import com.wutong.taxiapp.net.service.BackService;
import com.wutong.taxiapp.net.service.IBackService;
import com.wutong.taxiapp.util.SharedConfig;
import com.wutong.taxiapp_driver.R;

public class IApplication extends Application implements ImBaseSocketNet {

	public static final String SERVERADDRESS = "";

	public SharedConfig sharedConfig;

	public static String mobileCode;
	public static int COMMON_CELL_WIDTH;
	public static int COMMON_CELL_HEIGHT;
	private TelephonyManager telephonyMgr;
	private List<Activity> activitylist = new LinkedList<Activity>();

	private static IApplication mInstance = null;
	public boolean m_bKeyRight = true;
	BMapManager mBMapManager = null;

	private IntentFilter mIntentFilter;
	private IBackService iBackService;
	private Intent mServiceIntent;

	
	private String userNum;
	
	
	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public IBackService getiBackService() {
		return iBackService;
	}

	// 服务 连接回调
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBackService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackService = IBackService.Stub.asInterface(service);
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		if (!MyLogger.isDebug) {
			// 处理崩溃异常
			CrashHandler crashHandler = CrashHandler.getInstance();
			// getApplicationContext()为全局变量，生命周期为应用退出
			crashHandler.init(getApplicationContext());
		}
		initNet();
		getScreenWidthAndHeight();
		COMMON_CELL_WIDTH = getResources().getDimensionPixelSize(
				R.dimen.table_width);
		COMMON_CELL_HEIGHT = getResources().getDimensionPixelSize(
				R.dimen.table_height);
		sharedConfig = new SharedConfig(getApplicationContext());
		telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mobileCode = telephonyMgr.getDeviceId();

		mInstance = this;
		initEngineManager(this);

	}

	private void initNet() {
		mServiceIntent = new Intent(this, BackService.class);
		bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(new MyGeneralListener())) {
			// Toast.makeText(IApplication.getInstance().getApplicationContext(),
			// "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	public static IApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				// Toast.makeText(
				// IApplication.getInstance().getApplicationContext(),
				// "您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				// Toast.makeText(
				// IApplication.getInstance().getApplicationContext(),
				// "输入正确的检索条件！", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Toast.makeText(
						IApplication.getInstance().getApplicationContext(),
						"地图授权有误!!!!!" + iError, Toast.LENGTH_LONG).show();
				IApplication.getInstance().m_bKeyRight = false;
			} else {
				IApplication.getInstance().m_bKeyRight = true;
				// Toast.makeText(
				// IApplication.getInstance().getApplicationContext(),
				// "key认证成功", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void getScreenWidthAndHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
	}

	@Override
	public void acceptResult(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	public void unbindService() {
		unbindService(conn);
	}

	/*
	 * 添加界面的方法 直接调用界面添加
	 */
	public void addActivity(Activity activity) {
		activitylist.add(activity);
	}

	/*
	 * 完全退出我的应用 结束每一个界面 抛异常强制退出
	 */
	public void exit() {

		for (Activity activity : activitylist) {
			if (activity != null) {
				activity.finish();
			}
		}
		System.exit(0);
	}

	// 连续点击返回两次 退出程序
	int i;
	float front;
	float later;

	public void shutDown(Activity activity) {
		i++;
		if (i < 2) {
			Toast.makeText(this, "再点一次退出程序", 0).show();
			front = System.currentTimeMillis();
			return;
		}
		if (i >= 2) {
			later = System.currentTimeMillis();
			if (later - front > 2000) {
				Toast.makeText(this, "再点一次退出程序", 0).show();
				front = System.currentTimeMillis();
				i = 1;
			} else {
				unbindService();
				exit();
				i = 0;
			}
		}
	}

}
