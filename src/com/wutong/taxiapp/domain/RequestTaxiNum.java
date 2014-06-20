package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class RequestTaxiNum extends BaseDomain<RequestTaxiNum> {

	private final int request = 2;
	private String lat; // 纬度
	private String lon; // 经度
	private String address;

	public RequestTaxiNum(String lat, String lon, String address) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.address = address;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toJsonString() {

		Gson gson = new Gson();

		return gson.toJson(this);
	}

	@Override
	public RequestTaxiNum parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RequestTaxiNum> parseArrayJSON(JSONObject jsonObj)
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
	public RequestTaxiNum cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
