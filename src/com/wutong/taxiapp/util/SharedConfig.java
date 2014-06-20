package com.wutong.taxiapp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

//获得软件的全局配置文件
public class SharedConfig {
	Context context;
	SharedPreferences shared;

	// 各种配置KEY
	public static final String ISFIRST = "First";
	public static final String ISREGISTER = "register";
	public static final String MYMOBILE = "mobile";
	public static final String ADDRESS = "address";

	public SharedConfig(Context context) {
		this.context = context;
		shared = context.getSharedPreferences("config", Context.MODE_PRIVATE);

	}

	public SharedPreferences GetConfig() {
		return shared;
	}

	public void ClearConfig() {
		shared.edit().clear().commit();
	}

	public void setMobile(String num) {
		shared.edit().putString(MYMOBILE, num).commit();
	}

	public String getMobile() {
		return shared.getString(MYMOBILE, "");
	}

	public void setString(String key, String value) {
		shared.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		return shared.getString(key, "");
	}

	public void setBoolean(String key, boolean value) {
		shared.edit().putBoolean(key, value).commit();
	}

	public static void setAddress(Context mContext, String[] value)
			throws IOException {

		BufferedWriter writer = null;
		try {
			File file = new File(mContext.getCacheDir() + "/address.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true)));
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < value.length; i++) {

				if (!TextUtils.isEmpty(value[i])) {
					buffer.append("," + value[i]);
				}

			}

			String substring = buffer.toString();
			writer.write(substring);
			writer.flush();
		} catch (IOException e) {
			if (writer != null) {
				writer.close();
				writer = null;
			}
			throw e;
		}

	}

	public static ArrayList<String> getAddress(Context mContext)
			throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			File file = new File(mContext.getCacheDir() + "/address.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));

			String readLine = reader.readLine();
			if (readLine != null) {
				String[] split = readLine.split(",");
				if (split.length > 5) {
					for (int i = split.length - 1; i >= split.length - 5
							|| list.size() > 5; i--) {
						if (i < 0) {
							return list;
						}
						boolean contains = list.contains(split[i]);
						if (!contains) {
							list.add(split[i]);
						}
					}
				} else {
					for (int i = 0; i < split.length; i++) {
						boolean contains = list.contains(split[i]);
						if (!contains) {
							list.add(split[i]);
						}
					}
				}
			}
		} catch (IOException e) {

			if (reader != null) {
				reader.close();
				reader = null;
			}

			throw e;

		}
		return list;

	}
}
