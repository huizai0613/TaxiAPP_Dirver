package com.wutong.taxiapp.activity;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.domain.RequestLogin;
import com.wutong.taxiapp.domain.ResponseLogin;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.ErrorInfo;
import com.wutong.taxiapp.util.RegexUtils;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText edit_mobileNum;
	private EditText edit_pwd;
	private TextView login_but;
	private TextView register_but;
	private String mobile;
	private String pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

	}

	@Override
	protected void initView() {

		edit_mobileNum = (EditText) findViewById(R.id.edit_mobileNum);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);

		login_but = (TextView) findViewById(R.id.login_but);
		register_but = (TextView) findViewById(R.id.register_but);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListener() {

		register_but.setOnClickListener(this);
		login_but.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();

		if (id == R.id.register_but) {// 注册

			ActivityUtils.startActivity(mContext, RegisterStartActivity.class);

		} else if (id == R.id.login_but) { // 登录

			mobile = edit_mobileNum.getText().toString();
			pwd = edit_pwd.getText().toString();

			if (!RegexUtils.checkMobile(mobile)) {
				ToastUtils.toast(mContext, "请填写正确的手机号");
				return;
			} else if (TextUtils.isEmpty(pwd)) {
				ToastUtils.toast(mContext, "密码不可为空");
				return;
			}

			RequestLogin login = new RequestLogin();

			login.setUser(mobile);
			login.setPasswd(pwd);

			try {
				lib.requestLogin(login);
			} catch (RemoteException e) {

				ToastUtils.toast(mContext, ErrorInfo.ERRORNET);
				e.printStackTrace();
			}
		}

	}

	@Override
	public void acceptResult(JSONObject jsonObject) {

		try {
			ResponseLogin parserLogin = lib.parserLogin(jsonObject);

			String ban = parserLogin.getBan();

			int parseInt = Integer.parseInt(ban);

			switch (parseInt) {
			case ResponseLogin.LOGINSUCCESS:
				// 登录成功
				ActivityUtils.startActivityAndFinish(mContext,
						SnatchOrderActivity.class);
				IApplication.getInstance().setUserNum(mobile);

				break;
			case ResponseLogin.LOGINFAIL_CHECK:
				// 没有提交身份验证
				ActivityUtils.startActivity(mContext, RegisterPhoto.class);

				break;
			default:

				break;
			}

		} catch (NetRequestException e) {

			e.getError().print(mContext);

		}

	}

	@Override
	public void onBackPressed() {
		IApplication.getInstance().shutDown(mContext);
	}

}
