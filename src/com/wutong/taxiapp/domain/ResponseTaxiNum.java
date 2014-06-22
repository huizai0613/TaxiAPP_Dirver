package com.wutong.taxiapp.domain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class ResponseTaxiNum extends BaseDomain<ResponseTaxiNum> {

	private String totalTaxi;
	private ArrayList<TaxiLocation> taxis;

	
	
	
	public ArrayList<TaxiLocation> getTaxis() {
		return taxis;
	}

	public void setTaxis(ArrayList<TaxiLocation> taxis) {
		this.taxis = taxis;
	}

	public String getTotalTaxi() {
		return totalTaxi;
	}

	public void setTotalTaxi(String totalTaxi) {
		this.totalTaxi = totalTaxi;
	}

	@Override
	public String toJsonString() {
		return null;
	}

	@Override
	public ResponseTaxiNum parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		boolean checkJson = CheckJson(jsonObj, "2");

		if (checkJson) {
			totalTaxi = jsonObj.optString("totalTaxi");
			JSONArray optJSONArray = jsonObj.optJSONArray("nearbyTaxi");
			if (optJSONArray != null) {
				TaxiLocation taxi = null;
				taxis = new ArrayList<TaxiLocation>();
				for (int i = 0; i < optJSONArray.length(); i++) {

					JSONObject optJSONObject = optJSONArray.optJSONObject(i);

					if (optJSONObject != null) {
						taxi = new TaxiLocation();

						taxis.add(taxi.parseJSON(optJSONObject));
					}
				}
			}
		}
		return this;
	}

	@Override
	public List<ResponseTaxiNum> parseArrayJSON(JSONObject jsonObj)
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
	public ResponseTaxiNum cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
