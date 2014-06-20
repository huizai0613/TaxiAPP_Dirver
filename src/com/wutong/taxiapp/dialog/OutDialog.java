package com.wutong.taxiapp.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.iss.app.AbsDialog;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.net.service.BackService;
import com.wutong.taxiapp.net.service.BackService.InitSocketThread;
import com.wutong.taxiapp_driver.R;

public class OutDialog extends AbsDialog {

	private Button button_ok, button_no;

	public OutDialog(Activity context) {
		super(context, R.style.dialog_normal);
		setContentView(R.layout.dialog_out_game);
		setProperty(1, 1);
	}

	@Override
	protected void initView() {
		button_ok = (Button) findViewById(R.id.button_ok);
		button_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				IApplication.getInstance().exit();
			}
		});
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	protected void setListener() {
		// button_no.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// dismiss();
		// }
		// });
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	public void setOnConfirmListener(View.OnClickListener l) {
		// button_ok.setOnClickListener(l);
	}
}
