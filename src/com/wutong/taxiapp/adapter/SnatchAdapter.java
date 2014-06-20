package com.wutong.taxiapp.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.zip.Inflater;

import com.wutong.taxiapp.activity.SnatchOrderActivity;
import com.wutong.taxiapp.domain.ResponseOrder;
import com.wutong.taxiapp_driver.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SnatchAdapter extends BaseAdapter {

	private ArrayList<ResponseOrder> responseOrders;
	private LayoutInflater inflater;
	private SnatchOrderActivity mActivity;

	public ArrayList<ResponseOrder> getResponseOrders() {
		return responseOrders;
	}

	public SnatchAdapter(ArrayList<ResponseOrder> responseOrders,
			SnatchOrderActivity mActivity) {
		super();
		this.mActivity = mActivity;
		inflater = LayoutInflater.from(mActivity);
		this.responseOrders = responseOrders;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return responseOrders.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return responseOrders.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub

		return responseOrders.get(position).getType();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int i = position % 2;
		int itemViewType = getItemViewType(position);
		final ResponseOrder responseOrder = responseOrders.get(position);

		Holder1 holder1 = null;
		Holder2 holder2 = null;

		if (convertView == null) {
			switch (itemViewType) {
			case 0:// 文本
				convertView = inflater.inflate(R.layout.listitem_snatch, null);
				holder1 = new Holder1();
				holder1.price = (TextView) convertView
						.findViewById(R.id.snatch_order_price);
				holder1.num = (TextView) convertView
						.findViewById(R.id.snatch_order_num);
				holder1.go_address = (TextView) convertView
						.findViewById(R.id.snatch_go_address);
				holder1.to_address = (TextView) convertView
						.findViewById(R.id.snatch_to_address);
				holder1.snatchOrder = (Button) convertView
						.findViewById(R.id.snatch_snatchOrder);
				holder1.phone = (View) convertView
						.findViewById(R.id.snatch_phone);

				convertView.setTag(holder1);
				break;
			case 1:// 语音
				holder2 = new Holder2();
				convertView = inflater.inflate(R.layout.listitem_snatch_sound,
						null);
				holder2.num = (TextView) convertView
						.findViewById(R.id.snatch_order_num);
				holder2.go_address = (TextView) convertView
						.findViewById(R.id.snatch_go_address);
				holder2.snatchOrder = (Button) convertView
						.findViewById(R.id.snatch_snatchOrder);
				holder2.phone = (View) convertView
						.findViewById(R.id.snatch_phone);
				convertView.setTag(holder2);
				break;
			}
		} else {
			switch (itemViewType) {
			case 0:// 文本
				holder1 = (Holder1) convertView.getTag();
				break;
			case 1:// 语音
				holder2 = (Holder2) convertView.getTag();
				break;
			}
		}

		// 设置资源
		switch (itemViewType) {
		case 0:// 文本
			holder1.phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri uri = Uri.parse("tel:"
							+ responseOrder.getPhone().replaceAll("//s", ""));
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					mActivity.startActivity(intent);
				}
			});
			holder1.snatchOrder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 抢单
				}
			});
			holder1.num.setText("" + (position + 1));
			holder1.price.setText(responseOrder.getPrice() + "￥");
			holder1.go_address.setText("从:" + responseOrder.getSouceAddress());
			holder1.to_address.setText("到:" + responseOrder.getDesAddress());

			break;
		case 1:// 语音

			holder2.phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri uri = Uri.parse("tel:"
							+ responseOrder.getPhone().replaceAll("//s", ""));
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					mActivity.startActivity(intent);
				}
			});
			holder2.snatchOrder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 抢单
				}
			});

			holder2.num.setText("" + (position + 1));

			holder2.go_address.setText("乘客位置:"
					+ responseOrder.getSouceAddress());
			break;
		}

		if (i == 0) {
			convertView.setBackgroundColor(mActivity.getResources().getColor(
					R.color.order_bg_1));
		} else {
			convertView.setBackgroundColor(mActivity.getResources().getColor(
					R.color.order_bg_2));
		}
		return convertView;
	}

	class Holder1 {
		TextView num;
		TextView price;
		TextView go_address;
		TextView to_address;
		Button snatchOrder;
		View phone;
	}

	class Holder2 {
		TextView num;
		TextView go_address;
		Button snatchOrder;
		View phone;
	}
}
