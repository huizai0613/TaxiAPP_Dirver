package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.bean.BaseBean;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class RequestVersion extends BaseDomain<RequestVersion> {

	private final int request = 1;

	@Override
	public RequestVersion parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RequestVersion> parseArrayJSON(JSONObject jsonObj)
			throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public RequestVersion cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJsonString() {
		Gson gson = new Gson();

		return gson.toJson(this);
	}

}
