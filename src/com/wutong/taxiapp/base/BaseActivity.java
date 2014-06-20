package com.wutong.taxiapp.base;

import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iss.app.IssActivity;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.net.TaxiLib;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp_driver.R;

public abstract class BaseActivity extends IssActivity implements
		ImBaseSocketNet {

	protected Activity mContext;
	private TextView com_title_deputy_tv;
	private TextView com_title_content;
	protected View com_title_back;
	protected LayoutInflater mInflater;
	protected TaxiLib lib;
	protected static final String PAGESIZE = "15";
	protected int page = 1;

	public BaseActivity() {
		mContext = this;
	}

	@Override
	protected void onPause() {
		lib.unRegisterReciver();
		super.onPause();
	}

	@Override
	protected void onResume() {

		IApplication.getInstance().addActivity(this);

		if (lib == null) {
			lib = new TaxiLib(mContext, this);
		}
		lib.registerReciver();
		super.onResume();
	}

	public void initTitle(boolean isHaveTitle, String content,
			boolean isShowDeputy, String... string) {
		if (isHaveTitle) {
			com_title_back = findViewById(R.id.com_title_back);
			com_title_content = (TextView) findViewById(R.id.com_title_content);
			com_title_deputy_tv = (TextView) findViewById(R.id.com_title_deputy_tv);

			if (isShowDeputy) {
				com_title_deputy_tv.setVisibility(View.VISIBLE);
				com_title_deputy_tv.setText(string[0]);
			} else {
				com_title_deputy_tv.setVisibility(View.GONE);

			}

			com_title_content.setText(content);

			com_title_back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ActivityUtils.finish(mContext);
				}
			});
		}
	}

	public void setOnDeputyListener(OnClickListener listener) {
		com_title_deputy_tv.setOnClickListener(listener);
	}

	@Override
	public void onBackPressed() {

		ActivityUtils.finish(this);

	}

	@Override
	public void acceptResult(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

}
