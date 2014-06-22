package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class RequestAddress extends BaseDomain {

	private int request = 15;

	private String user;
	private String taxiLat;
	private String taxiLong;

	private int getRequest() {
		return request;
	}

	private void setRequest(int request) {
		this.request = request;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTaxiLat() {
		return taxiLat;
	}

	public void setTaxiLat(String taxiLat) {
		this.taxiLat = taxiLat;
	}

	public String getTaxiLong() {
		return taxiLong;
	}

	public void setTaxiLong(String taxiLong) {
		this.taxiLong = taxiLong;
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
