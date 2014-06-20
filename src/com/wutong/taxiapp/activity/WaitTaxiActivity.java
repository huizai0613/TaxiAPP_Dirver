package com.wutong.taxiapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
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
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp_driver.R;

public class WaitTaxiActivity extends BaseActivity {

	private TextView waittaxi_tv;
	private ProgressBar progressBar;
	private View wait_but;
	private BMapManager mBMapMan;
	private MKSearch mMKSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_waittaxi);
		initMap();
	}

	// 初始化地图
	private void initMap() {
		mBMapMan = new BMapManager(getApplicationContext());
		mBMapMan.init(null);
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearchListener());// 注意，MKSearchListener只支持一个，以最后一次设置为准

	}

	//搜索时间 地点
	private void searchTimeAndDistance(GeoPoint startPoint, GeoPoint endPoint) {
		MKPlanNode start = new MKPlanNode();
		start.pt = startPoint;
		MKPlanNode end = new MKPlanNode();
		end.pt = endPoint;// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
		mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mMKSearch.drivingSearch(null, start, null, end);
	}

	// 地图地点搜索
	public class MySearchListener implements MKSearchListener {

		private static final String TAG = "MySearchListener";

		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {

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
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int iError) {
			// 返回公交搜索结果
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int iError) {
			// 返回步行路线搜索结果
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			// 返回公交车详情信息搜索结果
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int iError) {

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int type,
				int error) {
			// 在此处理短串请求返回结果.
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	protected void initView() {

		initTitle(true, "等待司机抢单", false, null);

		waittaxi_tv = (TextView) findViewById(R.id.waittaxi_tv);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		wait_but = findViewById(R.id.wait_but);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

}
