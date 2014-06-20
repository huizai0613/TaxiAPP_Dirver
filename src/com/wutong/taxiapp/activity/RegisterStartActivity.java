package com.wutong.taxiapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.domain.RequestRegister;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.RegexUtils;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

public class RegisterStartActivity extends BaseActivity implements
		OnClickListener {

	private EditText edit_mobileNum;
	private EditText edit_pwd;
	private EditText edit_re_pwd;
	private TextView but_next;
	private TextView but_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registerstart);
	}

	@Override
	protected void initView() {
		edit_mobileNum = (EditText) findViewById(R.id.edit_mobileNum);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);
		edit_re_pwd = (EditText) findViewById(R.id.edit_re_pwd);
		but_next = (TextView) findViewById(R.id.but_next);
		but_back = (TextView) findViewById(R.id.but_back);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void setListener() {

		but_next.setOnClickListener(this);
		but_back.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();

		if (id == R.id.but_next) {
			String mobile = edit_mobileNum.getText().toString();
			String pwd = edit_pwd.getText().toString().trim();
			String re_pwd = edit_re_pwd.getText().toString().trim();
			if (checkInfo(mobile, pwd, re_pwd)) {

				RequestRegister requestRegister = new RequestRegister();
				requestRegister.setUser(mobile);
				requestRegister.setPasswd(pwd);

				ActivityUtils.startActivityForSerializable(mContext,
						RegisterEndActivity.class, requestRegister,
						"REQUESTREGISTER", mContext);

			}
		} else if (id == R.id.but_back) {

			ActivityUtils.finish(mContext);

		}

	}

	public boolean checkInfo(String mobile, String pwd, String re_pwd) {

		boolean checkMobile = RegexUtils.checkMobile(mobile);
		boolean pwdIsOk = true;
		boolean re_pwdIsOk = true;
		if (checkMobile) {
			int length = pwd.length();
			if (length <= 3) {
				pwdIsOk = false;
				ToastUtils.toast(mContext, "密码不能为空或少于3位");
			} else {
				if (!pwd.equals(re_pwd)) {
					pwdIsOk = false;
					ToastUtils.toast(mContext, "两次输入密码不一致");
				}
			}
		} else {
			ToastUtils.toast(mContext, "请输入正确的手机号码");
		}

		return checkMobile && pwdIsOk && pwdIsOk;
	}

	@Override
	public void onBackPressed() {

		ActivityUtils.finish(mContext);

	}

}
