package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class RequestLogin extends BaseDomain {

	private int request = 12;

	private String user;

	private String passwd;

	public int getRequest() {
		return request;
	}

	public void setRequest(int request) {
		this.request = request;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toJsonString() {

		Gson gson = new Gson();

		return gson.toJson(this);
	}

	@Override
	public Object parseJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List parseArrayJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
