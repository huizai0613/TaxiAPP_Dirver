package com.wutong.taxiapp.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.domain.MyLocation;
import com.wutong.taxiapp.domain.TaxiLocation;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.SharedConfig;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_driver.R;

public class TaxiMapActivity extends BaseActivity implements TextWatcher {
	protected static final String TAG = "TaxiMapActivity";
	BMapManager mBMapMan;
	MapView mMapView;
	private MapController mMapController;
	private MyLocation mLocation;
	private MyLocation newLocation;
	private ImageButton map_back;
	private ImageButton map_go;
	private EditText map_address;
	private EditText map_address_go;
	private EditText map_address_to;
	MKSearch mMKSearch = null;
	private ListView address_lv;
	private View address_but;
	private PopupWindow popupWindow;
	private LayoutInflater inflater;
	private ArrayList<String> Addresslist;
	private AdressAdapter adressAdapter;
	private IApplication instance;
	private View map_top;
	private View home_but_locate;
	private boolean isSerach;

	private OverlayItem myLocationitem;
	private MyOverlay myLocationoverlay;
	private PopupOverlay popupOverlay;
	private TextView address_name;
	private TextView address_address;
	private MKAddrInfo addressInfo;
	private View popView;
	private int addressType;
	private InputMethodManager imm;
	private ItemizedOverlay mTaxioverlay;
	private OverlayItem mTaxioverlayItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = IApplication.getInstance();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inflater = LayoutInflater.from(mContext);
		setContentView(R.layout.activity_taximap);
	}

	// 初始化地图
	private void initMap() {
		mBMapMan = new BMapManager(getApplicationContext());
		mBMapMan.init(null);
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		mMapView.setBuiltInZoomControls(false);
		mMapController.setZoom(15);// 设置地图zoom级别
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearchListener());// 注意，MKSearchListener只支持一个，以最后一次设置为准

		MKPlanNode start = new MKPlanNode();
		start.pt = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
		MKPlanNode end = new MKPlanNode();
		end.pt = new GeoPoint(40057031, 116307852);// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
		mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mMKSearch.drivingSearch(null, start, null, end);

	}

	@Override
	protected void initView() {
		Addresslist = new ArrayList<String>();
		map_top = (View) findViewById(R.id.map_top);
		home_but_locate = (View) findViewById(R.id.home_but_locate);
		map_back = (ImageButton) findViewById(R.id.map_back);
		map_go = (ImageButton) findViewById(R.id.map_go);
		map_address_go = (EditText) findViewById(R.id.map_address_go);
		map_address_to = (EditText) findViewById(R.id.map_address_to);
		initMap();
		initPopuwindow();
	}

	@Override
	protected void initData() {

		Intent intent = getIntent();

		Serializable serializableExtra = intent
				.getSerializableExtra("LOCATION");
		if (serializableExtra != null
				&& serializableExtra instanceof MyLocation) {
			newLocation = mLocation = (MyLocation) serializableExtra;
		} else {

		}

		addressType = intent.getIntExtra("ADDRESSTYPE", 0);

		if (addressType == MyLocation.STARTADDRESS) {
			map_address = map_address_go;
			map_address_to.setVisibility(View.GONE);
		} else if (addressType == MyLocation.EndADDRESS) {
			map_address = map_address_to;
			map_address_go.setVisibility(View.GONE);
		}
		map_address.setVisibility(View.VISIBLE);
		map_address.addTextChangedListener(this);// 添加文本输入框监听事件
		map_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSerach = true;
				map_address.setText(map_address.getText().toString());// 添加这句后实现效果
				Spannable content = map_address.getText();
				Selection.selectAll(content);
				if (popupWindow != null && !popupWindow.isShowing()) {
					if (popupOverlay != null) {
						popupOverlay.hidePop();
						mMapView.removeView(popView);
					}
					popupWindow.showAsDropDown(map_top, 0, 0);
				}
				try {
					Addresslist = SharedConfig.getAddress(mContext);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtils.toast(mContext, "读取最近使用地址失败!!");
				}
				adressAdapter = new AdressAdapter(Addresslist);
				address_lv.setAdapter(adressAdapter);
				adressAdapter.notifyDataSetChanged();
			}
		});

		setMapDate();
	}

	// 为地图初始化数据
	private void setMapDate() {
		if (mLocation != null) {
			map_address.setText(mLocation.getAddress_address());
			GeoPoint geoPoint = new GeoPoint(
					(int) (mLocation.getLatitude() * 1e6),
					(int) (mLocation.getLongitude() * 1e6));

			myLocationitem = new OverlayItem(geoPoint, "item", "item");
			Drawable mark = null;
			Drawable markTaxi = null;

			if (addressType == MyLocation.STARTADDRESS) {
				mark = mContext.getResources().getDrawable(R.drawable.icon_man);

			} else if (addressType == MyLocation.EndADDRESS) {
				mark = mContext.getResources().getDrawable(R.drawable.to_icon);
			}
			markTaxi = mContext.getResources().getDrawable(
					R.drawable.icon_taxicar);
			mTaxioverlay = new ItemizedOverlay<OverlayItem>(markTaxi, mMapView);// 出租车图标图层
			for (TaxiLocation tl : mLocation.getTaxis()) {

				mTaxioverlayItem = new OverlayItem(new GeoPoint(
						(int) (Double.parseDouble(tl.getTaxiLat()) * 1e6),
						(int) (Double.parseDouble(tl.getTaxiLong()) * 1e6)),
						"Taxi", "Taxi");

				mTaxioverlayItem.setMarker(markTaxi);
				mTaxioverlay.addItem(mTaxioverlayItem);
			}
			myLocationitem.setMarker(mark);
			myLocationoverlay = new MyOverlay(mark, mMapView);

			mMapView.getOverlays().add(mTaxioverlay);
			mMapView.getOverlays().add(myLocationoverlay);
			myLocationoverlay.addItem(myLocationitem);
			// 移动到定点为
			mMapController.animateTo(geoPoint);

			// 修改定位数据后刷新图层生效
			mMapView.refresh();
		}
		popView = inflater.inflate(R.layout.map_mylocation_pop, null);

		address_name = (TextView) popView.findViewById(R.id.address_name);
		address_address = (TextView) popView.findViewById(R.id.address_address);

		/**
		 * 创建一个popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				if (popupOverlay != null) {
					popupOverlay.hidePop();
					mMapView.removeView(popView);
				}

			}
		};
		popupOverlay = new PopupOverlay(mMapView, popListener);

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		// 回到初始位置
		home_but_locate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLocation != null) {
					newLocation = null;
					myLocationoverlay.removeAll();
					GeoPoint geoPoint = new GeoPoint((int) (mLocation
							.getLatitude() * 1e6), (int) (mLocation
							.getLongitude() * 1e6));
					myLocationitem.setGeoPoint(geoPoint);
					myLocationoverlay.addItem(myLocationitem);
					// 移动到定点为
//					mMapController.setZoom(19);// 设置地图zoom级别
					mMapController.animateTo(geoPoint);

					// 修改定位数据后刷新图层生效
					mMapView.refresh();

				}
			}
		});

		map_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createResult();
				ActivityUtils.finish(mContext);
			}
		});

		map_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// 处理点击事件,弹出泡泡
			return true;
		}

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	// 自动补全地址栏
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (isSerach) {
			String newText = s.toString().trim();

			mMKSearch.suggestionSearch(newText, mLocation.getCity());
		}
	}

	// 地图地点搜索
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			isSerach = false;
			// 返回地址信息搜索结果
			myLocationoverlay.removeAll();

			newLocation = new MyLocation(mLocation.getAddressType(),
					result.strBusiness, result.strAddr,
					result.geoPt.getLongitudeE6(),
					result.geoPt.getLatitudeE6(), mLocation.getCity());

			GeoPoint geoPt = result.geoPt;
			addressInfo = result;

			myLocationitem.setGeoPoint(geoPt);
			myLocationoverlay.addItem(myLocationitem);
			// 移动到定点为
//			mMapController.setZoom(19);// 设置地图zoom级别
			mMapController.animateTo(geoPt);
			// 修改定位数据后刷新图层生效
			mMapView.refresh();

			map_address.setText(addressInfo.strAddr);

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int iError) {
			// 返回驾乘路线搜索结果

			MKRoutePlan plan = result.getPlan(0);

			int distance = plan.getDistance();

			int time = plan.getTime();

			MyLogger.i(TAG, "距离:" + distance + "时间:" + time);
		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			MyLogger.i(TAG, "3");
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int iError) {
			// 返回公交搜索结果
			MyLogger.i(TAG, "4");
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int iError) {
			// 返回步行路线搜索结果
			MyLogger.i(TAG, "5");
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			// 返回公交车详情信息搜索结果
			MyLogger.i(TAG, "6");
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int iError) {
			if (iError != 0 || res == null) {

				ToastUtils.toast(mContext, "为找到您要的地点!!!!!");
				return;
			}
			int nSize = res.getSuggestionNum();
			Addresslist.clear();
			for (int i = 0; i < nSize; i++) {

				Addresslist.add(res.getSuggestion(i).city
						+ res.getSuggestion(i).district
						+ res.getSuggestion(i).key);
			}
			if (popupWindow != null && !popupWindow.isShowing()) {
				popupWindow.showAsDropDown(map_top);
			}
			adressAdapter = new AdressAdapter(Addresslist);
			address_lv.setAdapter(adressAdapter);
			adressAdapter.notifyDataSetChanged();

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int type,
				int error) {
			// 在此处理短串请求返回结果.
			MyLogger.i(TAG, "7");
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			MyLogger.i(TAG, "8");

		}
	}

	public void initPopuwindow() {

		View view = inflater.inflate(R.layout.popuwindow_address, null);

		address_lv = (ListView) view.findViewById(R.id.address_lv);
		address_but = (View) view.findViewById(R.id.address_but);
		address_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, 200);
		// ColorDrawable cd = new ColorDrawable(-0000);
		// popupWindow.setBackgroundDrawable(cd);
		// popupWindow.setOutsideTouchable(true);
	}

	private class AdressAdapter extends BaseAdapter {

		ArrayList<String> list;

		public AdressAdapter(ArrayList<String> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView != null) {

			} else {
				convertView = inflater.inflate(R.layout.route_inputs, null);
			}
			((TextView) convertView).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					isSerach = false;
					map_address.setText(list.get(position));
					mMKSearch.geocode(list.get(position), mLocation.getCity());

					if (popupWindow != null && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
				}
			});
			((TextView) convertView).setText(list.get(position));

			return convertView;
		}
	}

	// 自定义图层
	class MyOverlay extends ItemizedOverlay<OverlayItem> {
		GeoPoint pt;

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			super.onTap(arg0, arg1);

			if (popupOverlay != null) {
				popupOverlay.hidePop();
				arg1.removeView(popView);
			}
			pt = arg0;
			mMKSearch.reverseGeocode(pt);
			return false;
		}

		@Override
		protected boolean onTap(int arg0) {
			// TODO Auto-generated method stub
			super.onTap(arg0);
			if (arg0 == 0) {
				mMKSearch.poiSearchInCity("北京", "天安门");
				if (newLocation == null) {
					newLocation = mLocation;
				}
				// pt = new GeoPoint(
				// (int) (newLocation.getLatitude()),
				// (int) ((newLocation.getLongitude() / 1E6 - 0.003) *
				// 1E6));

				// 弹出自定义View
				if (!TextUtils.isEmpty(newLocation.getAddress_name())) {
					address_name.setText(newLocation.getAddress_name());
				} else {
					address_name.setText(newLocation.getAddress_address());
				}
				address_address.setText(newLocation.getAddress_address());

				popupOverlay.showPopup(popView, pt, 10);

			}
			return true;
		}
	}

	private void createResult() {

		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		String text = map_address.getText().toString();

		try {

			Intent intent = getIntent();

			intent.putExtra("LOCATION", newLocation);

			mContext.setResult(mLocation.getAddressType(), intent);
			SharedConfig.setAddress(mContext, new String[] { text });

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {

		createResult();
		super.onBackPressed();
	}
}
