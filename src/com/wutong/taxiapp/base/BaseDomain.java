package com.wutong.taxiapp.base;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.iss.bean.BaseBean;
import com.iss.exception.NetRequestException;

public abstract class BaseDomain<T> extends BaseBean<T> {
	
	
	/**
	 * 将Bean实例对象转化为json字符串
	 * 
	 * @param jsonObj
	 * @return
	 */
	public abstract String  toJsonString();
	
}
