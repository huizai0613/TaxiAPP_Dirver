package com.wutong.taxiapp.activity;

import android.os.Bundle;
import android.view.View;

import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp_driver.R;

public class SettingActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);

	}

	public void clickSetting(View v) {

		int id = v.getId();

		switch (id) {
		case R.id.setting_myComment:// 我的评价

			break;
		case R.id.setting_updatePhone:// 修改手机号

			break;
		case R.id.setting_updatePwd:// 修改密码

			break;
		case R.id.setting_aboutUs:// 关于我们

			break;
		}

	}

	@Override
	protected void initView() {
		initTitle(true, "设置", false, null);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

}
