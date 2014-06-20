package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class ResponseMyOrder extends BaseDomain {

	public static final int COMPLETED = 0;
	public static final int COMPLETEING = 1;
	public static final int CLANCE = 2;

	private String phone;
	private String souceAddress;
	private String desAddress;
	private String price;
	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSouceAddress() {
		return souceAddress;
	}

	public void setSouceAddress(String souceAddress) {
		this.souceAddress = souceAddress;
	}

	public String getDesAddress() {
		return desAddress;
	}

	public void setDesAddress(String desAddress) {
		this.desAddress = desAddress;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toJsonString() {
		// TODO Auto-generated method stub
		return null;
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
