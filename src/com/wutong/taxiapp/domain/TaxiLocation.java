package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class TaxiLocation extends BaseDomain<TaxiLocation> {

	private String taxiUser;
	private String taxiLat;
	private String taxiLong;

	public String getTaxiUser() {
		return taxiUser;
	}

	public void setTaxiUser(String taxiUser) {
		this.taxiUser = taxiUser;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaxiLocation parseJSON(JSONObject jsonObj) throws NetRequestException {

		taxiUser = jsonObj.optString("taxiUser");
		taxiLat = jsonObj.optString("taxiLat");
		taxiLong = jsonObj.optString("taxiLong");

		return this;
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
	public TaxiLocation cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
