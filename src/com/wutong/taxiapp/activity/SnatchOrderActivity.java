package com.wutong.taxiapp.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;
import com.iss.exception.NetRequestException;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.adapter.SnatchAdapter;
import com.wutong.taxiapp.domain.RequestAddress;
import com.wutong.taxiapp.domain.ResponseOrder;
import com.wutong.taxiapp.domain.ResponseTaxiNum;
import com.wutong.taxiapp.net.TaxiLib;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.ApkInstaller;
import com.wutong.taxiapp.util.ErrorInfo;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

public class SnatchOrderActivity extends SlidingFragmentActivity implements
		OnClickListener, ImBaseSocketNet {

	private static final String RECORD_FILENAME = "temp";
	boolean isStartSpeking;
	private Myhandler mHandler;
	private Context mContext;
	private TextView above_content;
	private ImageView above_margin;
	private TaxiLib lib;
	private AlertDialog mLoadDialog;
	// 地图定位
	private double longitude;// 经度
	private double latitude;// 纬度
	private MyLocationListenner myListener;// 位置监听
	private LocationClient mLocClient; // 定位客户端
	private BDLocation startLocation;
	private ResponseTaxiNum parserTaxiNum;
	private TextView above_second;
	private ResponseOrder order;
	boolean isEnableScroll = true; // 列表时候可以自动归位
	private MediaPlayer mMediaPlayer;

	class Myhandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String url = SpeechUtility.getUtility(SnatchOrderActivity.this)
						.getComponentUrl();
				String assetsApk = "SpeechService.apk";
				if (processInstall(SnatchOrderActivity.this, url, assetsApk)) {
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
				break;
			case 1:
				if (mLoadDialog != null) {
					mLoadDialog.dismiss();
				}
				break;
			case 3:// 读取下一个订单

				if (responseOrders.size() <= orderNum) {
					isStartSpeking = false;
				} else {
					order = responseOrders.get(orderNum);
					++orderNum;
					if (isEnableScroll) {
						orderListPositionReset();
					}
					switch (order.getType()) {
					case 0:// 文本给讯飞解析
						playerOrder(order);
						break;
					case 1:// 音频直接播放
						try {
							mMediaPlayer = new MediaPlayer();
							mMediaPlayer.setDataSource(getAmrFile(
									order.getPhone()).getAbsolutePath());
							mMediaPlayer.prepare();
							mMediaPlayer.start();
							// 设置播放结束时监听
							mMediaPlayer
									.setOnCompletionListener(new OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer mp) {
											mMediaPlayer.release();
											mMediaPlayer.stop();
											mMediaPlayer = null;
											mHandler.sendEmptyMessageDelayed(3,
													2000);
										}
									});
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						break;
					}

					// responseOrders.removeFirst();
					// adapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	// 生成音频临时文件
	private boolean createSoundFile(ResponseOrder order) {
		FileOutputStream fos = null;
		byte[] parserString2Byte = parserString2Byte(order.getSourceSound());
		File amrFile = getAmrFile(order.getPhone());
		if ("START".equals(order.getFlag())) {
			if (amrFile.exists()) {
				amrFile.delete();
				try {
					amrFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {
			fos = new FileOutputStream(amrFile, true);

			fos.write(parserString2Byte);

			fos.flush();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fos = null;
			}
		}
		
		System.out.println(order.getFlag());
		if ("END".equals(order.getFlag())) {
			return true;
		}

		return false;
	}

	// 获取文件手机路径
	private File getAmrFile(String fileName) {
		File file = new File(Environment.getExternalStorageDirectory(),
				"WifiChat/voiceRecord/" + fileName + ".amr");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return file;
	}

	// 解析字符串为byte数组
	private byte[] parserString2Byte(String SourceSound) {

		byte[] decode = Base64.decode(SourceSound, Base64.DEFAULT);

		return decode;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		responseOrders = new ArrayList<ResponseOrder>();
		initSlidingMenu();
		setContentView(R.layout.activity_snatchorder);
		initLocation();
		initSpeak();
		initControl();
		initView();
		setListener();
		initData();
		lib = new TaxiLib(mContext, this);
	}

	// 初始化科大讯飞插件
	private void initSpeak() {
		if (SpeechUtility.getUtility(this).queryAvailableEngines() == null
				|| SpeechUtility.getUtility(this).queryAvailableEngines().length <= 0) {

			final Dialog dialog = new Dialog(this, R.style.dialog);

			LayoutInflater inflater = getLayoutInflater();
			View alertDialogView = inflater.inflate(
					R.layout.superman_alertdialog, null);
			dialog.setContentView(alertDialogView);
			Button okButton = (Button) alertDialogView.findViewById(R.id.ok);
			TextView comeText = (TextView) alertDialogView
					.findViewById(R.id.title);
			comeText.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
			okButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					mLoadDialog = new AlertDialog.Builder(
							SnatchOrderActivity.this).create();
					mLoadDialog.show();
					// 注意此处要放在show之后 否则会报异常
					mLoadDialog
							.setContentView(R.layout.loading_process_dialog_anim);
					Message message = new Message();
					message.what = 0;
					mHandler.sendMessage(message);
				}
			});
			dialog.show();
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = (int) (display.getWidth()); // 设置宽度
			dialog.getWindow().setAttributes(lp);
			return;
		}

		SpeechUtility.getUtility(SnatchOrderActivity.this).setAppid("5397c9b7");
		mTts = new SpeechSynthesizer(this, mTtsInitListener);
	}

	/**
	 * 初期化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			if (code != ErrorCode.SUCCESS) {
				ToastUtils.toast(mContext, "语音插件初始化失败!请重新登录");
				ActivityUtils.startActivityAndFinish(SnatchOrderActivity.this,
						LoginActivity.class);
			} else if (code == ErrorCode.SUCCESS) {

				// 设置引擎类型
				mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
				// 设置发音人
				mTts.setParameter(SpeechSynthesizer.VOICE_NAME, "xiaoyan");
				// 设置语速
				mTts.setParameter(SpeechSynthesizer.SPEED, "50");
				// 设置音调
				mTts.setParameter(SpeechSynthesizer.PITCH, "50");

			}
		}
	};
	private ListView snatch_orderlist;
	private SpeechSynthesizer mTts;

	// 定位初始化
	private void initLocation() {
		myListener = new MyLocationListenner();
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(60 * 1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		// mLocClient.requestLocation();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			startLocation = location;
			longitude = location.getLongitude();// 获取经度
			latitude = location.getLatitude(); // 获取纬度

			RequestAddress requestAddress = new RequestAddress();
			requestAddress.setUser(IApplication.getInstance().getUserNum());
			requestAddress.setTaxiLat(latitude + "");
			requestAddress.setTaxiLong(longitude + "");
			try {
				lib.requestAddress(requestAddress);
			} catch (RemoteException e) {
				ToastUtils.toast(mContext, ErrorInfo.ERRORNET);
				e.printStackTrace();
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// 初始化侧边栏
	private void initSlidingMenu() {

		setBehindContentView(R.layout.behind_slidingmenu);// 设置侧边栏布局
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		sm.setShadowWidthRes(R.dimen.shadow_width);// 侧边栏阴影宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 屏幕宽度-侧边栏滑出宽度
		// sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
		// sm.setShadowWidth(20);
		sm.setBehindScrollScale(0);
		// 设置滑动时菜单的是否淡入淡出
		sm.setFadeEnabled(true);
		// 设置淡入淡出的比例
		sm.setFadeDegree(0.4f);
		// 设置滑动时拖拽效果
		sm.setBehindScrollScale(0.4f);
	}

	// 得到侧边栏控制
	private void initControl() {

	}

	protected void initView() {
		mHandler = new Myhandler();
		snatch_orderlist = (ListView) findViewById(R.id.snatch_orderlist);
		above_content = (TextView) findViewById(R.id.above_content);
		above_margin = (ImageView) findViewById(R.id.above_margin);
		above_second = (TextView) findViewById(R.id.above_second);
		above_content.setText("抢单中..");

		snatch_orderlist.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int action = event.getAction();

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					isEnableScroll = false;
					break;
				case MotionEvent.ACTION_UP:
					isEnableScroll = true;
					break;
				}

				return false;
			}
		});

	}

	protected void initData() {

		adapter = new SnatchAdapter(responseOrders, SnatchOrderActivity.this);

		snatch_orderlist.setAdapter(adapter);

	}

	int orderNum = 1;
	int orderId = 0;

	// 语音合成并播放
	public int playerOrder(ResponseOrder order) {

		String text = "订单" + orderNum + "从" + order.getSouceAddress() + "到"
				+ order.getDesAddress() + "给价" + order.getPrice() + "元";
		int code = mTts.startSpeaking(text, mTtsListener);
		return code;

	}

	private int itemHeight;
	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {

		@Override
		public void onSpeakResumed() throws RemoteException {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessageDelayed(3, 2000);
		}

		@Override
		public void onSpeakProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpeakPaused() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpeakBegin() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompleted(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessageDelayed(3, 2000);
		}

		@Override
		public void onBufferProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub

		}
	};

	// 订单列表自动归为
	private void orderListPositionReset() {
		if (itemHeight == 0 || listHeight == 0) {
			listHeight = snatch_orderlist.getHeight();
			View childAt = snatch_orderlist.getChildAt(0);
			itemHeight = childAt.getHeight();
			itemNum = listHeight / itemHeight;
		}
		int childTotal = responseOrders.size();

		if (itemNum < childTotal - itemNum) {

			int lastVisiblePosition = snatch_orderlist.getLastVisiblePosition() + 1;
			int childVisibalCount = snatch_orderlist.getChildCount();
			View childAt = snatch_orderlist.getChildAt(childVisibalCount - 1);
			int lastheightBottom = childAt.getBottom();
			int lastheightTop = childAt.getTop();
			int scroll = 0;

			if (orderNum > itemNum) {// 当订单大于一屏幕时
				if (lastVisiblePosition > orderNum) {// 当最后显示的订单号大于正在读的订单号,

					scroll = -((((lastVisiblePosition - orderNum) - 1)
							* itemHeight + Math.abs(listHeight - lastheightTop)));

				} else if (lastVisiblePosition < orderNum) {

					scroll = (((orderNum - lastVisiblePosition)) * itemHeight + Math
							.abs(lastheightBottom - listHeight));

				} else {
					scroll = Math.abs(lastheightBottom - listHeight);
				}
			}
			snatch_orderlist.smoothScrollBy(scroll, 500);
		}
	}

	private SnatchAdapter adapter;
	private ArrayList<ResponseOrder> responseOrders;
	private int listHeight;
	private int itemNum;

	protected void setListener() {

		above_margin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 进入设置界面
				ActivityUtils.startActivity(SnatchOrderActivity.this,
						SettingActivity.class);

			}
		});

		above_second.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 跳转到我的订单界面
			}
		});

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocClient != null) {
			mLocClient.stop();
		}

		if (mTts != null) {
			mTts.destory();
		}
		lib.unbindService();
	}

	@Override
	public void acceptResult(JSONObject jsonObject) {
		try {
			order = lib.parserOrder(jsonObject);

			int size = responseOrders.size();

			if (!isStartSpeking) {
				isStartSpeking = true;
				if (size != 0) {
					orderNum += 1;
				}
				switch (order.getType()) {
				case 0:// 文本给讯飞解析
					responseOrders.add(size, order);
					adapter.notifyDataSetChanged();
					playerOrder(order);
					break;
				case 1:// 音频直接播放

					if (createSoundFile(order)) {
						responseOrders.add(size, order);
						adapter.notifyDataSetChanged();
						try {
							mMediaPlayer = new MediaPlayer();
							mMediaPlayer.setDataSource(getAmrFile(
									order.getPhone()).getAbsolutePath());
							mMediaPlayer.prepare();
							mMediaPlayer.start();
							// 设置播放结束时监听
							mMediaPlayer
									.setOnCompletionListener(new OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer mp) {
											mMediaPlayer.stop();
											mMediaPlayer.release();
											mMediaPlayer = null;
											mHandler.sendEmptyMessageDelayed(3,
													2000);
										}
									});
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					} else {
						isStartSpeking = false;
					}

					break;
				}

			}

		} catch (NetRequestException e) {
			e.getError().print(mContext);
		}
	}

	@Override
	public void onBackPressed() {

		IApplication.getInstance().shutDown(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mTts != null) {
			mTts.pauseSpeaking(mTtsListener);
		}

		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
		}

		lib.unRegisterReciver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTts != null) {
			mTts.resumeSpeaking(mTtsListener);
		}
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
		lib.registerReciver();
	}

	/**
	 * 如果服务组件没有安装，有两种安装方式。 1.直接打开语音服务组件下载页面，进行下载后安装。
	 * 2.把服务组件apk安装包放在assets中，为了避免被编译压缩，修改后缀名为mp3，然后copy到SDcard中进行安装。
	 */
	private boolean processInstall(Context context, String url, String assetsApk) {
		// 直接下载方式
		// ApkInstaller.openDownloadWeb(context, url);
		// 本地安装方式
		if (!ApkInstaller.installFromAssets(context, assetsApk)) {
			ToastUtils.toast(SnatchOrderActivity.this, "插件安装失败!!请重新下载");
			IApplication.getInstance().shutDown(SnatchOrderActivity.this);
			return false;
		}
		return true;
	}
}