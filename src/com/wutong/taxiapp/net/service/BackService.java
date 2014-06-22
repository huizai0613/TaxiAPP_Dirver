package com.wutong.taxiapp.net.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.wutong.taxiapp.base.MyLogger;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class BackService extends Service {
	private boolean serviceIsLife = false;
	private static final String TAG = "BackService";
	private static long HEART_BEAT_RATE = 10 * 1000;

//	public static final String HOST = "192.168.1.254";// "192.168.1.21";//
//	public static final int PORT = 7018;
	
	public static final String HOST = "120.210.73.148";// "192.168.1.21";//
	public static final int PORT = 4002;

	public static final String MESSAGE_ACTION = "com.wutong.message_ACTION";
	public static final String SENDIMAGE_ACTION = "com.wutong.message_ssendimage_ACTION";
	public static final String HEART_BEAT_ACTION = "com.wutong.heart_beat_ACTION";
	public static final String NET_BAD_ACTION = "com.wutong.netbad_ACTION";

	private ReadThread mReadThread;

	private static final int connectionNum = 3;

	private LocalBroadcastManager mLocalBroadcastManager;

	private WeakReference<Socket> mSocket;

	// For heart Beat
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {

		int i;

		@Override
		public void run() {
			if (serviceIsLife) {
				if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
					boolean isSuccess = sendMsg("{\"request\":0}");// 就发送一个\r\n过去
					if (isSuccess) {
						i = 0;
						HEART_BEAT_RATE = 10 * 1000;
						MyLogger.i("BackService", "心跳包发送"); // 如果发送失败，就重新初始化一个socket
					}
					if (!isSuccess) {
						if (i++ >= connectionNum) {
							Intent intent = new Intent(NET_BAD_ACTION);
							mLocalBroadcastManager.sendBroadcast(intent);
						} else {
							if (HEART_BEAT_RATE >= 1000) {
								HEART_BEAT_RATE = HEART_BEAT_RATE / 4;
							}
							MyLogger.i("BackService", "连接失败!!!重新建立连接");
							mHandler.removeCallbacks(heartBeatRunnable);
							if (mReadThread != null && mReadThread.isAlive()) {
								mReadThread.release();
							}
							releaseLastSocket(mSocket);
							new InitSocketThread().start();
						}
						return;
					}
				}
				mHandler.postDelayed(this, HEART_BEAT_RATE);
			}
		}
	};

	private long sendTime = 0L;
	private IBackService.Stub iBackService = new IBackService.Stub() {

		@Override
		public boolean sendMessage(String message) throws RemoteException {
			return sendMsg(message);
		}

		@Override
		public boolean sendImageMessage(String path) throws RemoteException {
			return sendImageMsg(path);
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return iBackService;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new InitSocketThread().start();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		serviceIsLife = true;
		MyLogger.i("BackService", "心跳包发送");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseLastSocket(mSocket);
		serviceIsLife = false;
	}

	public boolean sendMsg(String msg) {
		if (null == mSocket || null == mSocket.get()) {
			return false;
		}
		Socket soc = mSocket.get();
		try {
			if (!soc.isClosed() && !soc.isOutputShutdown()) {

				OutputStream os = soc.getOutputStream();
				String message = msg + "\0";
				os.write(message.getBytes());
				os.flush();
				sendTime = System.currentTimeMillis();// 每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendImageMsg(final String path) {

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					FileInputStream fileInputStream = new FileInputStream(path);
					ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
					byte[] buf = new byte[1024 * 4];

					int read = 0;
					int i = 0;
					while ((read = fileInputStream.read(buf)) != -1) {
						binaryData.reset();
						binaryData.write(buf);
						binaryData.flush();
						byte[] byteArray = binaryData.toByteArray();

						String decodeBase64 = Base64.encodeToString(byteArray,
								Base64.CRLF);

						String json = "{\"request\":13,\"picSize\":"
								+ byteArray.length + ",\"updownPic\":\" "
								+ decodeBase64 + "\",\"picSign\":" + i
								+ ",\"phone\":\"yexiangyu\"}";
						sendMsg(json);
						++i;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// 其他消息回复
					Intent intent = new Intent(SENDIMAGE_ACTION);
					mLocalBroadcastManager.sendBroadcast(intent);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		}.start();
		return true;
	}

	private void initSocket() {// 初始化Socket
		try {
			Socket so = new Socket();
			
			so.connect(new InetSocketAddress(HOST, PORT), 100);
			
			mSocket = new WeakReference<Socket>(so);
			mReadThread = new ReadThread(so);
			mReadThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);// 初始化成功后，就准备发送心跳包
	}

	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class InitSocketThread extends Thread {
		@Override
		public void run() {
			super.run();
			initSocket();
		}
	}

	// Thread to read content from Socket
	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			byte[] buffer = new byte[1024 * 1024 * 10];
			StringBuilder builder = new StringBuilder();
			String result = null;
			if (null != socket) {
				try {
					InputStream is = socket.getInputStream();

					int length = 0;

					while (!socket.isClosed() && !socket.isInputShutdown()
							&& isStart) {
						builder.delete(0, builder.length());
						while (((length = is.read(buffer)) != -1)) {
							result = new String(buffer, 0, length);
							if (result.substring(result.length() - 1,
									result.length()).equals("\0")) {
								result = result.substring(0,
										result.length() - 1);
								builder.append(result);
								break;
							}
							builder.append(result);
						}
						String message = builder.toString();
						// 收到服务器过来的消息，就通过Broadcast发送出去
						if (message.equals("ok")) {// 处理心跳回复
							Intent intent = new Intent(HEART_BEAT_ACTION);
							mLocalBroadcastManager.sendBroadcast(intent);
						} else {
							// 其他消息回复
							Intent intent = new Intent(MESSAGE_ACTION);
							intent.putExtra("message", message);
							mLocalBroadcastManager.sendBroadcast(intent);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}