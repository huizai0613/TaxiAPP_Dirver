package com.wutong.taxiapp.net;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Base64;

import com.iss.utils.StringUtil;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.domain.RequestAddress;
import com.wutong.taxiapp.domain.RequestLogin;
import com.wutong.taxiapp.domain.RequestRegister;
import com.wutong.taxiapp.domain.RequestTaxiNum;
import com.wutong.taxiapp.domain.RequestVersion;
import com.wutong.taxiapp.net.service.IBackService;

public class TaxiRequest {

	public static final String URL = "http://www.zhihuiapp.com:7000/Api/?";// 正式接口
	public static final String IMAGEURL = "http://img.zhihuiapp.com:7001";// 正式接口

	protected Context mContext;

	private MessageBackReciver reciver;
	private IBackService iBackService;

	public TaxiRequest(Context context) {

		mContext = context.getApplicationContext();

	}

	public void unbindService() {
		IApplication.getInstance().unbindService();
	}

	public String getProvinceInfoRequest() throws IOException {
		return StringUtil.inputToString(mContext.getAssets().open("city.json"),
				"utf-8");
	}

	private void addShareParamsAndOpenLoding() {
		if (iBackService == null) {
			iBackService = IApplication.getInstance().getiBackService();
		}

	}

	public void requestVersion() throws RemoteException {
		addShareParamsAndOpenLoding();
		iBackService.sendMessage(new RequestVersion().toJsonString());
	}

	public void requestTaxiNum(double lat, double lon, String address)
			throws RemoteException {
		addShareParamsAndOpenLoding();
		String jsonString = new RequestTaxiNum(lat + "", lon + "", address)
				.toJsonString();
		MyLogger.i("send", jsonString);
		iBackService.sendMessage(jsonString);
	}

	public void requestDirverRegister(RequestRegister register)
			throws RemoteException {
		addShareParamsAndOpenLoding();
		String jsonString = register.toJsonString();
		MyLogger.i("send", jsonString);
		iBackService.sendMessage(jsonString);

	}

	public void requestSubmitImage(final String path) throws RemoteException {
		addShareParamsAndOpenLoding();
		iBackService.sendImageMessage(path);
	}

	public void requestLogin(RequestLogin login) throws RemoteException {
		addShareParamsAndOpenLoding();
		String jsonString = login.toJsonString();
		MyLogger.i("send", jsonString);
		iBackService.sendMessage(jsonString);

	}

	public void requestAddress(RequestAddress requestAddress) throws RemoteException {
		addShareParamsAndOpenLoding();
		String jsonString = requestAddress.toJsonString();
		MyLogger.i("send", jsonString);
		iBackService.sendMessage(jsonString);

	}

}
