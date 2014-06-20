package com.wutong.taxiapp.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iss.exception.NetRequestException;
import com.iss.view.common.ToastAlone;
import com.iss.view.wheel.OnWheelChangedListener;
import com.iss.view.wheel.WheelView;
import com.wutong.taxiapp.MainActivity;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.adapter.WheelCityAdapter;
import com.wutong.taxiapp.adapter.WheelDistrictAdapter;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.domain.City;
import com.wutong.taxiapp.domain.Province;
import com.wutong.taxiapp.domain.RequestRegister;
import com.wutong.taxiapp.domain.ResponseDirverRegister;
import com.wutong.taxiapp.net.TaxiAsyncTask;
import com.wutong.taxiapp.net.TaxiLib;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.ErrorInfo;
import com.wutong.taxiapp.util.SharedConfig;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

public class RegisterEndActivity extends BaseActivity {

	protected static final String TAG = "RegisterEndActivity";
	private WheelView wheel_province;
	private WheelView wheel_city;
	private TextView registerEnd_ok;
	private TextView but_back;
	private WheelDistrictAdapter adapter;
	private WheelCityAdapter adapter1;
	private RadioGroup sexy_rd;
	private RadioButton sexy_male;
	private EditText edit_name;
	private TextView edit_date;
	private RequestRegister register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_registerend);
	}

	@Override
	protected void initView() {
		sexy_rd = (RadioGroup) findViewById(R.id.sexy_rd);
		sexy_male = (RadioButton) findViewById(R.id.sexy_male);
		sexy_male.setChecked(true);

		edit_name = (EditText) findViewById(R.id.edit_name);
		edit_date = (TextView) findViewById(R.id.edit_date);

		wheel_province = (WheelView) findViewById(R.id.wheel_province);
		wheel_province.IsFirst(true);
		wheel_province.setScaleItem(true);
		wheel_city = (WheelView) findViewById(R.id.wheel_city);
		wheel_city.IsFirst(false);
		wheel_city.setScaleItem(true);
		registerEnd_ok = (TextView) findViewById(R.id.but_next);
		but_back = (TextView) findViewById(R.id.but_back);

	}

	@Override
	protected void initData() {
		new GetDataTask(mContext).execute();

		register = (RequestRegister) getIntent().getSerializableExtra(
				"REQUESTREGISTER");
	}

	class GetDataTask extends TaxiAsyncTask<String, String, List<Province>> {

		public GetDataTask(Activity activity) {
			super(activity);

			mTaxiLib = new TaxiLib(mContext);
		}

		@Override
		protected List<Province> doInBackground(String... params) {
			super.doInBackground(params);
			List<Province> info = null;
			try {
				info = mTaxiLib.getProvinceInfo();
			} catch (IOException e) {
				exception = "网络错误";
			} catch (JSONException e) {
				exception = "解析出错";
			} catch (NetRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return info;
		}

		@Override
		protected void onPostExecute(List<Province> result) {
			super.onPostExecute(result);
			if (exception != null) {
				ToastAlone.showToast(getApplicationContext(), exception,
						Toast.LENGTH_SHORT).show();
				return;
			}

			setDate(result);
		}

	}

	@Override
	protected void setListener() {

		edit_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();

				DatePickerDialog dialog = new DatePickerDialog(
						RegisterEndActivity.this, new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {

								edit_date.setText(year + "-"
										+ (monthOfYear + 1) + "-" + dayOfMonth);

							}
						}, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH));

				dialog.show();
			}
		});

		but_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityUtils.finish(mContext);
			}

		});

		registerEnd_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 提交参数
				Province bean = adapter.getBean(wheel_province.getCurrentItem());

				City bean2 = adapter1.getBean(wheel_city.getCurrentItem());

				int checkedRadioButtonId = sexy_rd.getCheckedRadioButtonId();
				String sex = null;
				if (checkedRadioButtonId == R.id.sexy_male) {
					sex = "男";
				} else if (checkedRadioButtonId == R.id.sexy_female) {
					sex = "女";
				}

				String date = edit_date.getText().toString();
				String name = edit_name.getText().toString();

				if (TextUtils.isEmpty(name)) {
					ToastUtils.toast(mContext, "姓名不可为空");
					return;
				} else if (TextUtils.isEmpty(date)) {
					ToastUtils.toast(mContext, "出生日期不可为空");
					return;
				}
				IApplication.getInstance().sharedConfig.setString(
						SharedConfig.ADDRESS,
						bean.privinceName + bean2.getCityName());

				IApplication.getInstance().sharedConfig.setBoolean(
						SharedConfig.ISREGISTER, true);

				register.setCity(bean2.getCityName());
				register.setProvince(bean.privinceName);
				register.setBirth(date);
				register.setName(name);
				register.setSex(sex);

				try {
					lib.requestDirverRegister(register);
				} catch (RemoteException e) {
					ToastUtils.toast(mContext, ErrorInfo.ERRORNET);
					e.printStackTrace();
				}

			}
		});

		wheel_province.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				if (oldValue != newValue) {
					Province areaBean = adapter.getBean(wheel.getCurrentItem());
					ArrayList<City> stationList = areaBean.citys;
					adapter1 = new WheelCityAdapter(mContext, stationList);
					wheel_city.setViewAdapter(adapter1);
					wheel_city.setCurrentItem(0);
				}
			}
		});
	}

	public void setDate(List<Province> result) {

		adapter = new WheelDistrictAdapter(mContext,
				(ArrayList<Province>) result);
		wheel_province.setViewAdapter(adapter);
		wheel_province.setCurrentItem(0);

		adapter1 = new WheelCityAdapter(mContext, result.get(0).citys);
		wheel_city.setViewAdapter(adapter1);
		wheel_city.setCurrentItem(0);

	}

	@Override
	public void acceptResult(JSONObject jsonObject) {

		try {
			ResponseDirverRegister parserDirverRegister = lib
					.parserDirverRegister(jsonObject);

			if (parserDirverRegister.getResponse().equals(
					ResponseDirverRegister.LOGINSUCCESS)) {// 注册成功
				ToastUtils.toast(mContext, "注册成功,请登录验证");
				ActivityUtils.startActivity(mContext, LoginActivity.class);
			}

		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.getError().print(mContext);
		}

	}

}
