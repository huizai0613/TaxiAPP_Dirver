package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class ResponseOrder extends BaseDomain<ResponseOrder> {

	private String phone;

	private int type;

	private String sourceAddress;
	private String sourceLat;
	private String sourceLong;

	private String desAddress;
	private String desLat;
	private String desLong;

	private String sex;

	private String price;

	private String sourceSound;

	private String flag;
	
	
	
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getSourceLat() {
		return sourceLat;
	}

	public void setSourceLat(String sourceLat) {
		this.sourceLat = sourceLat;
	}

	public String getSourceLong() {
		return sourceLong;
	}

	public void setSourceLong(String sourceLong) {
		this.sourceLong = sourceLong;
	}

	public String getDesLat() {
		return desLat;
	}

	public void setDesLat(String desLat) {
		this.desLat = desLat;
	}

	public String getDesLong() {
		return desLong;
	}

	public void setDesLong(String desLong) {
		this.desLong = desLong;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSourceSound() {
		return sourceSound;
	}

	public void setSourceSound(String sourceSound) {
		this.sourceSound = sourceSound;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSouceAddress() {
		return sourceAddress;
	}

	public void setSouceAddress(String soucerAddress) {
		this.sourceAddress = soucerAddress;
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
	public ResponseOrder parseJSON(JSONObject jsonObj)
			throws NetRequestException {

		CheckJson(jsonObj, "3");

		JSONObject jsonObject = jsonObj.optJSONObject("fareInfo");

		if (jsonObject != null) {

			String optString = jsonObject.optString("type");

			int parseInt = Integer.parseInt(optString);

			switch (parseInt) {
			case 0:// 文本
				type = 0;
				phone = jsonObject.optString("phone");
				sourceAddress = jsonObject.optString("sourceAddress");
				sourceLat = jsonObject.optString("sourceLat");
				sourceLong = jsonObject.optString("sourceLong");
				desAddress = jsonObject.optString("desAddress");
				desLat = jsonObject.optString("desLat");
				desLong = jsonObject.optString("desLong");
				sex = jsonObject.optString("sex");
				price = jsonObject.optString("price");

				break;
			case 1:// 音频
				type = 1;
				phone = jsonObject.optString("phone");
				sourceAddress = jsonObject.optString("sourceAddress");
				sourceLat = jsonObject.optString("sourceLat");
				sourceLong = jsonObject.optString("sourceLong");
				sourceSound = jsonObject.optString("sourceSound");
				sex = jsonObject.optString("sex");
				price = jsonObject.optString("price");
				flag = jsonObject.optString("flag");

				break;
			}

		}

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
	public ResponseOrder cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
