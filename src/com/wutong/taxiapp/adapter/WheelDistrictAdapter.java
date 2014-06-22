package com.wutong.taxiapp.adapter;

import java.util.ArrayList;

import android.content.Context;

import com.iss.view.wheel.AbstractWheelTextAdapter;
import com.wutong.taxiapp.domain.Province;
import com.wutong.taxiapp_driver.R;

public class WheelDistrictAdapter extends AbstractWheelTextAdapter {
	private ArrayList<Province> list;

	public WheelDistrictAdapter(Context context, ArrayList<Province> areaList) {
		super(context, R.layout.layout_station_wheel_text);
		list = areaList;
		setItemTextResource(R.id.station_wheel_textView);
	}

	@Override
	public int getItemsCount() {
		return list.size();
	}

	@Override
	protected CharSequence getItemText(int index) {
		return list.get(index ).privinceName;
	}

	public Province getBean(int position) {
		
		if(position>=0&&position<list.size()){
			return list.get(position);
		}
return null;		
	}
}
