package com.wutong.taxiapp.domain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.baidu.location.BDLocation;
import com.iss.bean.BaseBean;
import com.iss.exception.NetRequestException;

public class MyLocation extends BaseBean<MyLocation> {

	public static final int STARTADDRESS = 0;
	public static final int EndADDRESS = 1;

	private int addressType;
	private String address_name;
	private String address_address;
	private double longitude;
	private double latitude;
	private String city;
	private ArrayList<TaxiLocation> taxis;

	
	
	public MyLocation(int addressType, String address_name,
			String address_address, double longitude, double latitude,
			String city) {
		super();
		this.addressType = addressType;
		this.address_name = address_name;
		this.address_address = address_address;
		this.longitude = longitude;
		this.latitude = latitude;
		this.city = city;
	}

	public MyLocation(int addressType, String address_name,
			String address_address, double longitude, double latitude,
			String city,ArrayList<TaxiLocation> taxis) {
		super();
		this.taxis=taxis;
		this.addressType = addressType;
		this.address_name = address_name;
		this.address_address = address_address;
		this.longitude = longitude;
		this.latitude = latitude;
		this.city = city;
	}

	
	
	public ArrayList<TaxiLocation> getTaxis() {
		return taxis;
	}



	public void setTaxis(ArrayList<TaxiLocation> taxis) {
		this.taxis = taxis;
	}



	public String getAddress_name() {
		return address_name;
	}

	public void setAddress_name(String address_name) {
		this.address_name = address_name;
	}

	public String getAddress_address() {
		return address_address;
	}

	public void setAddress_address(String address_address) {
		this.address_address = address_address;
	}

	public int getAddressType() {
		return addressType;
	}

	public void setAddressType(int addressType) {
		this.addressType = addressType;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public MyLocation parseJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MyLocation> parseArrayJSON(JSONObject jsonObj)
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
	public MyLocation cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
