package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class ResponseLogin extends BaseDomain<ResponseLogin> {

	// {
	// "response" : "12" ;
	// "ban" : "0"
	// "banInfo" : "禁止内容"
	// } ban：0成功，1没有身份验证,2.....
	
	public static final int LOGINSUCCESS=0;
	public static final int LOGINFAIL_CHECK=1;
	

	private String ban;
	private String banInfo;
	
	
	

	public String getBan() {
		return ban;
	}

	public void setBan(String ban) {
		this.ban = ban;
	}

	public String getBanInfo() {
		return banInfo;
	}

	public void setBanInfo(String banInfo) {
		this.banInfo = banInfo;
	}

	@Override
	public String toJsonString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseLogin parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		CheckJson(jsonObj, "12");

		ban = jsonObj.optString("ban");
		banInfo = jsonObj.optString("banInfo");

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
	public ResponseLogin cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
