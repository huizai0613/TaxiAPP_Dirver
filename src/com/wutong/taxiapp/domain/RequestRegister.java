package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class RequestRegister extends BaseDomain {

	private int request = 11;
	private String user;
	private String passwd;
	private String province;
	private String city;
	private String name;
	private String sex;
	private String birth;

	
	
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

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
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
