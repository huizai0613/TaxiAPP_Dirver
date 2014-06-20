package com.wutong.taxiapp.net;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.domain.Province;
import com.wutong.taxiapp.domain.RequestAddress;
import com.wutong.taxiapp.domain.RequestLogin;
import com.wutong.taxiapp.domain.RequestRegister;
import com.wutong.taxiapp.domain.ResponseDirverRegister;
import com.wutong.taxiapp.domain.ResponseLogin;
import com.wutong.taxiapp.domain.ResponseOrder;
import com.wutong.taxiapp.domain.ResponseTaxiNum;
import com.wutong.taxiapp.domain.ResponseVersion;
import com.wutong.taxiapp.net.service.BackService;

public class TaxiLib {
	private static TaxiLib mLib;
	private TaxiRequest mRequest;
	private TaxiParse mParse;
	private Context mContext;
	private LocalBroadcastManager mLocalBroadcastManager;
	private MessageBackReciver reciver;
	private IntentFilter mIntentFilter;
	private LoadingDialog mLoadingDialog;

	public TaxiLib(Context context, ImBaseSocketNet baseSocketNet) {
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
		mIntentFilter.addAction(BackService.MESSAGE_ACTION);
		mIntentFilter.addAction(BackService.SENDIMAGE_ACTION);
		mIntentFilter.addAction(BackService.NET_BAD_ACTION);
		this.mContext = context;
		mLoadingDialog = new LoadingDialog(mContext);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		mRequest = new TaxiRequest(context);
		mParse = new TaxiParse(context);
		if (baseSocketNet != null) {
			reciver = new MessageBackReciver(baseSocketNet);
		}
	}

	public TaxiLib(Context context) {
		mRequest = new TaxiRequest(context);
		mParse = new TaxiParse(context);
	}

	public List<Province> getProvinceInfo() throws JSONException,
			NetRequestException, IOException {

		String json = mRequest.getProvinceInfoRequest();

		return mParse.parseProvinceInfo(json);
	}

	private void requestCom() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		mLoadingDialog.show();
	}

	private void parserCom() {

		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	// 注册接受者
	public void registerReciver() {
		mLocalBroadcastManager.registerReceiver(reciver, mIntentFilter);
	}

	// 注销接受者
	public void unRegisterReciver() {
		mLocalBroadcastManager.unregisterReceiver(reciver);
	}

	// 取消绑定
	public void unbindService() {
		unRegisterReciver();
		mRequest.unbindService();
	}

	// 请求版本
	public void requestVersion() throws RemoteException {

		mRequest.requestVersion();
	}

	// 解析版本
	public ResponseVersion parserVersion(JSONObject jsonObject)
			throws NetRequestException {

		return mParse.parserVersion(jsonObject);

	}

	// 请求出租车数量与坐标
	public void requestTaxiNum(double lat, double lon, String address)
			throws RemoteException {

		requestCom();

		mRequest.requestTaxiNum(lat, lon, address);

	}

	// 解析出租车数量与坐标
	public ResponseTaxiNum parserTaxiNum(JSONObject jsonObject)
			throws NetRequestException {

		parserCom();
		return mParse.parserTaxiNum(jsonObject);

	}

	// 司机注册
	public void requestDirverRegister(RequestRegister register)
			throws RemoteException {

		requestCom();

		mRequest.requestDirverRegister(register);

	}

	// 解析司机注册
	public ResponseDirverRegister parserDirverRegister(JSONObject jsonObject)
			throws NetRequestException {

		parserCom();
		return mParse.parserDirverRegister(jsonObject);

	}

	// 提交审核图片
	public void requestSubmitImage(String path) throws RemoteException {

		requestCom();

		mRequest.requestSubmitImage(path);

	}

	// 解析审核图片
	public void parserPhoto() {
		parserCom();
	}

	// 请求登录
	public void requestLogin(RequestLogin login) throws RemoteException {
		// TODO Auto-generated method stub
		requestCom();

		mRequest.requestLogin(login);
	}

	// 解析登录
	public ResponseLogin parserLogin(JSONObject jsonObject)
			throws NetRequestException {

		parserCom();
		return mParse.parserLogin(jsonObject);

	}

	//提交坐标
	public void requestAddress(RequestAddress requestAddress) throws RemoteException {

		mRequest.requestAddress(requestAddress);
	}

	//解析订单
	public ResponseOrder parserOrder(JSONObject jsonObject) throws NetRequestException {
		return mParse.parserOrder(jsonObject);
	}

	// // 提交留言
	// public BackMsg submitFeed(String leaveType, String fb_pId, String
	// fb_content)
	// throws HttpRequestException, JSONException, NetRequestException {
	//
	// String json = mRequest.submitFeedRequest(LEAVEMSG, leaveType, fb_pId,
	// fb_content);
	//
	// return mParse.parseBackMsg(json);
	//
	// }

}
