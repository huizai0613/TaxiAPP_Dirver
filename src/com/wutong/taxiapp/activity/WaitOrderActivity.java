package com.wutong.taxiapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp_driver.R;

public class WaitOrderActivity extends BaseActivity {

	private TextView waittaxi_tv;
	private ProgressBar progressBar;
	private View wait_but;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_waitorder);

	}

	@Override
	protected void initView() {

		initTitle(true, "等待司机抢单", false, null);

		waittaxi_tv = (TextView) findViewById(R.id.waittaxi_tv);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		wait_but = findViewById(R.id.wait_but);

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
