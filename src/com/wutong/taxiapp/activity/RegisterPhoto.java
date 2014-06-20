package com.wutong.taxiapp.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

public class RegisterPhoto extends BaseActivity implements OnClickListener {

	private ImageView photoImage;
	private Button submit_but;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_registerphoto);

	}

	@Override
	protected void initView() {

		initTitle(true, "上传正面照", false, null);

		com_title_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ActivityUtils.startActivity(mContext, LoginActivity.class);

			}
		});

		photoImage = (ImageView) findViewById(R.id.photo);

		submit_but = (Button) findViewById(R.id.submit_but);
		actionReceiver = new MediaActionReceiver();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListener() {

		photoImage.setOnClickListener(this);
		submit_but.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.photo:
			changeImage();
			break;
		case R.id.submit_but:
			if (mCurrentPhotoFile != null) {
				try {
					lib.requestSubmitImage(mCurrentPhotoFile.getAbsolutePath());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;

		}

	}

	private static MediaActionReceiver actionReceiver;
	private static final int SCAN_MEDIA_START = 1;
	private static final int SCAN_MEDIA_FINISH = 2;
	private static final int SCAN_MEDIA_FILE = 3;
	private static final int SCAN_MEDIA_FILE_FINISH_INT = 4;
	private static final int SCAN_MEDIA_FILE_FAIL_INT = 5;
	ProgressDialog delLoadingDialog = null;
	private static final String SCAN_MEDIA_FILE_FINISH = "ACTION_MEDIA_SCANNER_SCAN_FILE_FINISH";

	private static final int PIC_REQUEST_CODE_WITH_DATA = 1; // 标识获取图片数据
	private static final int PIC_REQUEST_CODE_SELECT_CAMERA = 2; // 标识请求照相功能的activity
	private static final int PIC_Select_CODE_ImageFromLoacal = 3;// 标识请求相册取图功能的activity

	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera/taxiApp");
	protected static final String TAG = null;
	private String fileName;// 图片名称
	private File mCurrentPhotoFile;//

	// 用当前时间给取得的图片命名
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public void changeImage() {

		LayoutInflater inflater = LayoutInflater.from(mContext);

		View view = inflater.inflate(R.layout.changeimage, null);

		TextView pushImage = (TextView) view.findViewById(R.id.pushImage);
		TextView cacheImage = (TextView) view.findViewById(R.id.cacheImage);

		pushImage.setOnClickListener(btn_onClickListener);
		cacheImage.setOnClickListener(btn_onClickListener);

		dialog = new Dialog(mContext, R.style.MyDialog1);

		dialog.setContentView(view);

		dialog.show();

	}

	private OnClickListener btn_onClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.pushImage:
				try {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						PHOTO_DIR.mkdirs();// 创建照片的存储目录
						fileName = getPhotoFileName();
						mCurrentPhotoFile = new File(PHOTO_DIR, fileName);// 给新照的照片文件命名
						Intent cIntent = getTakePickIntent(mCurrentPhotoFile);
						startActivityForResult(cIntent,
								PIC_REQUEST_CODE_SELECT_CAMERA);
					} else {

					}

				} catch (ActivityNotFoundException e) {
					// Toast.makeText(mContext, "", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.cacheImage:
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent,
							PIC_Select_CODE_ImageFromLoacal);
				} else {
					// Toast.makeText(mContext, "没有SD卡",
					// Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
			}

		}
	};

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

		return intent;
	}

	/**
	 * 根据资源ID获得ProgressDialog对象
	 * 
	 * @param resId
	 * @return
	 */
	protected ProgressDialog onCreateDialogByResId(int resId) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getResources().getText(resId));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		return dialog;
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_MEDIA_START:
				Log.i(TAG, "sccan media started");
				delLoadingDialog = onCreateDialogByResId(R.string.loading);
				delLoadingDialog.show();
				break;
			case SCAN_MEDIA_FINISH:
				Log.i(TAG, "sccan media finish");
				galleryPhoto();
				break;

			case SCAN_MEDIA_FILE:
				Log.i(TAG, "sccan file");
				delLoadingDialog = onCreateDialogByResId(R.string.loading);
				delLoadingDialog.show();
				ScanMediaThread sthread = new ScanMediaThread(mContext, 40, 300);
				sthread.run();
				break;

			case SCAN_MEDIA_FILE_FINISH_INT:
				Log.i(TAG, "sccan file finish");
				galleryPhoto();
				break;

			case SCAN_MEDIA_FILE_FAIL_INT:
				Log.i(TAG, "sccan file fail");
				if (delLoadingDialog != null && delLoadingDialog.isShowing()) {
					delLoadingDialog.dismiss();
				}

				try {
					unregisterReceiver(actionReceiver);
				} catch (Exception e) {
					Log.e(TAG, "actionReceiver not registed");
				}

				Toast.makeText(mContext, "no take photo", Toast.LENGTH_LONG)
						.show();
				break;

			}
		}
	};

	private Bitmap decodeStream;
	private TextView username;
	private Bitmap photo;
	private Dialog dialog;

	private void galleryPhoto() {
		try {
			long id = 0;
			Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver cr = mContext.getContentResolver();

			Cursor cursor = cr.query(imgUri, null,
					MediaStore.Images.Media.DISPLAY_NAME + "='"
							+ mCurrentPhotoFile.getName() + "'", null, null);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToLast();
				id = cursor.getLong(0);

				/*
				 * update by lvxuejun on 20110322 2.1系统对 gallery
				 * 这个调用进行了修改，uri不让传file:///了，只能传图库中的图片，
				 * 比如此类uri：content://media/external/images/media/3 所以需要把FIle的Uri
				 * 转化成图库的Uri
				 */
				Uri uri = ContentUris.withAppendedId(imgUri, id);// Uri.fromFile(mCurrentPhotoFile);

				// 启动gallery去剪辑这个照片
				final Intent intent = getCropImageIntent(uri);
				if (intent != null) {
					startActivityForResult(intent, PIC_REQUEST_CODE_WITH_DATA);
				}

			}

			if (delLoadingDialog != null && delLoadingDialog.isShowing()) {
				delLoadingDialog.dismiss();
			}

			try {
				unregisterReceiver(actionReceiver);
			} catch (Exception e) {
				Log.e(TAG, "actionReceiver not registed");
			}
		} catch (Exception ee) {
			// TODO Auto-generated catch block
			Log.e(TAG, "", ee);

		}
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setData(photoUri);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("return-data", true);
		return intent;
	}

	/**
	 * 定义receiver接收其他线程的广播
	 * 
	 */
	public class MediaActionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
				mHandler.sendEmptyMessage(SCAN_MEDIA_START);
			}
			if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				mHandler.sendEmptyMessage(SCAN_MEDIA_FINISH);
			}
			if (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE.equals(action)) {
				mHandler.sendEmptyMessage(SCAN_MEDIA_FILE);
			}
		}
	}

	public class ScanMediaThread extends Thread {
		private int scanCount = 5;
		private int interval = 50;
		private Context cx = null;

		public ScanMediaThread(Context context, int count, int i) {
			scanCount = count;
			interval = i;
			cx = context;
			this.setName(System.currentTimeMillis() + "_ScanMediaThread");
		}

		@Override
		public void run() {
			Log.i(TAG, "scan thread start");
			if (this.cx == null)
				return;
			try {
				int j = 0;
				Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = mContext.getContentResolver();

				Cursor cursor;
				for (j = 0; j < this.scanCount; j++) {
					Thread.sleep(this.interval);
					cursor = cr.query(imgUri, null,
							MediaStore.Images.Media.DISPLAY_NAME + "='"
									+ mCurrentPhotoFile.getName() + "'", null,
							null);
					Log.i(TAG, "scan thread " + j);
					if (cursor != null && cursor.getCount() > 0) {
						Log.i(TAG, "send finish " + SCAN_MEDIA_FILE_FINISH);
						mHandler.sendEmptyMessage(SCAN_MEDIA_FILE_FINISH_INT);
						break;
					}
				}
				if (j == this.scanCount) {
					Log.i(TAG, "send fail ");
					mHandler.sendEmptyMessage(SCAN_MEDIA_FILE_FAIL_INT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			// 调用Gallery返回的
			case PIC_REQUEST_CODE_WITH_DATA:
				// photo = data.getParcelableExtra("data");
				//
				// photoImage.setImageBitmap(photo);
				// if (dialog != null && dialog.isShowing()) {
				// dialog.dismiss();
				// }

				break;
			// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			case PIC_REQUEST_CODE_SELECT_CAMERA:
				getBitmap();
				break;
			case PIC_Select_CODE_ImageFromLoacal:
				Uri uri = data.getData();
				boolean isSDCard = true;
				Uri fileUri = null;
				ContentResolver cr = getContentResolver();
				Cursor cursor = cr.query(uri, null, null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					isSDCard = false;
					fileName = cursor.getString(3);
					mCurrentPhotoFile = new File(cursor.getString(1)); // 图片文件路径

				} else {
					Toast.makeText(mContext, "该文件不存在!", Toast.LENGTH_LONG)
							.show();
				}
				if (mCurrentPhotoFile.exists()) {
					getBitmap();

				} else {
					Toast.makeText(mContext, "该文件不存在!", Toast.LENGTH_LONG)
							.show();
				}
				break;
			}
		}
	}

	private void getBitmap() {

		long length = mCurrentPhotoFile.length();
		FileOutputStream fileOutputStream = null;
		Bitmap decodeFile = null;
		try {
			Uri fileUri = Uri.fromFile(mCurrentPhotoFile);

			if (length < 1024 * 200) {
				decodeFile = BitmapFactory.decodeFile(fileUri.getPath());
				decodeFile = right(decodeFile);
				fileOutputStream = new FileOutputStream(mCurrentPhotoFile);
				photoImage.setImageBitmap(decodeFile);
				decodeFile.compress(CompressFormat.JPEG, 100, fileOutputStream);
			} else {
				BitmapFactory.Options options = new Options();
				options.inSampleSize = 2;
				decodeFile = BitmapFactory.decodeFile(fileUri.getPath(),
						options);
				decodeFile = right(decodeFile);
				photoImage.setImageBitmap(decodeFile);
				fileOutputStream = new FileOutputStream(mCurrentPhotoFile);
				decodeFile.compress(CompressFormat.JPEG, 80, fileOutputStream);
			}
			fileOutputStream.flush();
		} catch (Exception e) {
			Toast.makeText(this, "获取图片异常，请重新尝试。", Toast.LENGTH_LONG).show();
			Log.e(TAG, "Cannot crop image:", e);
		} finally {

			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// if (decodeFile != null) {
			// decodeFile.recycle();
			// decodeFile = null;
			// }

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

		}
	}

	private float scaleW = 1;// 横向缩放系数，1表示不变
	private float scaleH = 1;// 纵向缩放系数，1表示不变

	// 右转事件处理
	private Bitmap right(Bitmap mBitmap) {

		int bmpW = mBitmap.getWidth();
		int bmpH = mBitmap.getHeight();
		// 设置图片放大比例
		double scale = 1;
		// 计算出这次要放大的比例
		scaleW = (float) (scaleW * scale);
		scaleH = (float) (scaleH * scale);
		// 产生reSize后的Bitmap对象
		// 注意这个Matirx是android.graphics包下的那个
		Matrix mt = new Matrix();
		mt.postScale(scaleW, scaleH);
		if (bmpH > bmpW) {
			mt.setRotate(90);
		}
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, bmpW, bmpH, mt, true);

		return mBitmap;
	}

	@Override
	public void onBackPressed() {

		finish();

	}

	@Override
	public void acceptResult(JSONObject jsonObject) {

		ToastUtils.toast(mContext, "上传成功");
		
		lib.parserPhoto();
		

	}

}
