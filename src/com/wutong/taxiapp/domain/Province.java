package com.wutong.taxiapp.domain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.bean.BaseBean;
import com.iss.exception.NetRequestException;

public class Province extends BaseBean<Province> {
	private static final long serialVersionUID = 1L;
	public String privinceName;
	public ArrayList<City> citys;

	@Override
	public Province parseJSON(JSONObject jsonObj) throws NetRequestException {

		privinceName = jsonObj.optString("name");

		JSONArray optJSONArray = jsonObj.optJSONArray("sub");

		if (optJSONArray != null) {
			citys = new ArrayList<City>();
			City city = null;
			for (int i = 0; i < optJSONArray.length(); i++) {
				city = new City();
				citys.add(city.parseJSON(optJSONArray.optJSONObject(i)));
			}
		}

		return null;
	}

	@Override
	public List<Province> parseArrayJSON(JSONObject jsonObj)
			throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Province cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
