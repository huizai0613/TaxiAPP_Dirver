package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class ResponseVersion extends BaseDomain<ResponseVersion> {

	private String version;

	
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toJsonString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseVersion parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		boolean checkJson = CheckJson(jsonObj, 1 + "");

		if (checkJson) {
			version = jsonObj.optString("version");
		}

		return this;
	}

	@Override
	public List<ResponseVersion> parseArrayJSON(JSONObject jsonObj)
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
	public ResponseVersion cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
