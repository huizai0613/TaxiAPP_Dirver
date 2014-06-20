package com.wutong.taxiapp.net;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.dialog.OutDialog;
import com.wutong.taxiapp.net.service.BackService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

//处理服务器发送的数据,广播接受者
public class MessageBackReciver extends BroadcastReceiver {
	private WeakReference<ImBaseSocketNet> wk;
	private OutDialog dialog;

	public MessageBackReciver(ImBaseSocketNet imBaseSocketNet) {
		wk = new WeakReference<ImBaseSocketNet>(imBaseSocketNet);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		ImBaseSocketNet is = wk.get();

		// 心跳包数据,暂时不做处理
		if (action.equals(BackService.HEART_BEAT_ACTION)) {
			if (null != is) {
			}
		} else if (action.equals(BackService.NET_BAD_ACTION)) {// 网络无法连接,关闭程序
			if (dialog == null) {
				dialog = new OutDialog(((Activity) is));
			}
			if (!dialog.isShowing()) {
				dialog.show();
			}
		} else if (action.equals(BackService.SENDIMAGE_ACTION)) {// 图片上传完成

			try {
				is.acceptResult(new JSONObject("{\"response\":\"ok\"}"));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {// 非心跳包数据
			String message = intent.getStringExtra("message");
			MyLogger.i("result", message);
			try {
				is.acceptResult(new JSONObject(message));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// try {
			// byte[] data = Base64.decode(message, Base64.DEFAULT);
			// /*
			// * for(int i=0 ;i<data.length;i++){ if (data[i] < 0) { data[i]
			// +=
			// * 256; } }
			// */
			// OutputStream out = new FileOutputStream(Environment
			// .getExternalStorageDirectory().getAbsolutePath()
			// + "/WifiChat/123.amr");
			// out.write(data, 0, data.length);
			// out.flush();
			// out.close();
			// } catch (Exception ex) {
			//
			// }
			// try {
			// is.acceptResult(new JSONObject(message));
			// } catch (JSONException e) {
			// e.printStackTrace();
			//
			// }
		}
	};
}
