package com.wutong.taxiapp.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.domain.Province;
import com.wutong.taxiapp.domain.ResponseDirverRegister;
import com.wutong.taxiapp.domain.ResponseLogin;
import com.wutong.taxiapp.domain.ResponseOrder;
import com.wutong.taxiapp.domain.ResponseTaxiNum;
import com.wutong.taxiapp.domain.ResponseVersion;

public class TaxiParse {
	protected Context mContext;
	protected Gson gson;

	// public static final String BACKMSG = "backMsg";

	public TaxiParse(Context context) {
		mContext = context.getApplicationContext();
		gson = new Gson();
	}

	public List<Province> parseProvinceInfo(String json) throws JSONException,
			NetRequestException {
		JSONObject object = new JSONObject(json);
		Province andCity = new Province();
		andCity.parseJSON(object);

		List<Province> list = new ArrayList<Province>();

		JSONArray optJSONArray = object.optJSONArray("province");

		if (optJSONArray != null) {

			for (int i = 0; i < optJSONArray.length(); i++) {
				JSONObject optJSONObject = optJSONArray.optJSONObject(i);

				if (optJSONObject != null) {

					Province province = new Province();
					province.parseJSON(optJSONObject);
					list.add(province);

				}
			}

		}
		return list;
	}

	public ResponseVersion parserVersion(JSONObject jsonObj)
			throws NetRequestException {

		return new ResponseVersion().parseJSON(jsonObj);

	}

	public ResponseTaxiNum parserTaxiNum(JSONObject jsonObj)
			throws NetRequestException {
		// TODO Auto-generated method stub
		return new ResponseTaxiNum().parseJSON(jsonObj);
	}

	public ResponseDirverRegister parserDirverRegister(JSONObject jsonObject) throws NetRequestException {
		// TODO Auto-generated method stub
		return new ResponseDirverRegister().parseJSON(jsonObject);
	}

	public ResponseLogin parserLogin(JSONObject jsonObject) throws NetRequestException {
		
		
		return new ResponseLogin().parseJSON(jsonObject);
		
	}

	public ResponseOrder parserOrder(JSONObject jsonObject) throws NetRequestException {
		// TODO Auto-generated method stub
		return new ResponseOrder().parseJSON(jsonObject);
	}

	// public Index parseIndex(String json) throws JSONException,
	// NetRequestException {
	//
	// JSONObject object = new JSONObject(json);
	//
	// Index index = new Index();
	// index.setResult(json);
	// return index.parseJSON(object);
	//
	// }

}
