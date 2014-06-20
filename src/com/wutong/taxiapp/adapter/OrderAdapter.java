package com.wutong.taxiapp.adapter;

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

public class OrderAdapter extends BaseAdapter {

	private LinkedList<ResponseOrder> responseOrders;
	private LayoutInflater inflater;
	private SnatchOrderActivity mActivity;

	public LinkedList<ResponseOrder> getResponseOrders() {
		return responseOrders;
	}

	public OrderAdapter(LinkedList<ResponseOrder> responseOrders,
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		int i = position % 2;

		Handler handler = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_snatch, null);
			handler = new Handler();
			handler.price = (TextView) convertView
					.findViewById(R.id.snatch_order_price);
			handler.num = (TextView) convertView
					.findViewById(R.id.snatch_order_num);
			handler.go_address = (TextView) convertView
					.findViewById(R.id.snatch_go_address);
			handler.to_address = (TextView) convertView
					.findViewById(R.id.snatch_to_address);
			handler.snatchOrder = (Button) convertView
					.findViewById(R.id.snatch_snatchOrder);
			handler.phone = (View) convertView.findViewById(R.id.snatch_phone);

			convertView.setTag(handler);

		} else {
			handler = (Handler) convertView.getTag();
		}

		handler.phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:"
						+ responseOrders.get(position).getPhone()
								.replaceAll("//s", ""));
				Intent intent = new Intent(Intent.ACTION_DIAL, uri);
				mActivity.startActivity(intent);
			}
		});
		handler.snatchOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 抢单
			}
		});
		handler.num.setText(""+(position+1));
		handler.price.setText(responseOrders.get(position).getPrice() + "￥");
		handler.go_address.setText("从:"
				+ responseOrders.get(position).getSouceAddress());
		handler.to_address.setText("到:"
				+ responseOrders.get(position).getDesAddress());

		if (i == 0) {
			convertView.setBackgroundColor(mActivity.getResources().getColor(
					R.color.order_bg_1));
		} else {
			convertView.setBackgroundColor(mActivity.getResources().getColor(
					R.color.order_bg_2));
		}

		return convertView;
	}

	class Handler {

		TextView num;
		TextView price;
		TextView go_address;
		TextView to_address;
		Button snatchOrder;
		View phone;

	}
}
