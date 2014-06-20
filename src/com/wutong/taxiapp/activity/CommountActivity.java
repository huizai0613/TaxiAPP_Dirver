package com.wutong.taxiapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.view.ExtendedListView;
import com.wutong.taxiapp.view.ExtendedListView.OnPositionChangedListener;
import com.wutong.taxiapp_driver.R;

public class CommountActivity extends BaseActivity implements
		OnPositionChangedListener {

	private ExtendedListView commount_lv;
	private FrameLayout clockLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_commount);
	}

	@Override
	protected void initView() {

		commount_lv = (ExtendedListView) findViewById(R.id.commount_lv);

		commount_lv.setCacheColorHint(Color.TRANSPARENT);
		commount_lv.setOnPositionChangedListener(this);
		clockLayout = (FrameLayout) findViewById(R.id.clock);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPositionChanged(ExtendedListView listView, int position,
			View scrollBarPanel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScollPositionChanged(View scrollBarPanel, int top) {
		// TODO Auto-generated method stub
		MarginLayoutParams layoutParams = (MarginLayoutParams) clockLayout
				.getLayoutParams();
		layoutParams.setMargins(0, top, 0, 0);
		clockLayout.setLayoutParams(layoutParams);
	}

}
