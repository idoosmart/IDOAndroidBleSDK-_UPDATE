package test.com.ido.connect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ido.ble.bluetooth.device.BLEDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import test.com.ido.R;

public class ScanAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<BLEDevice> entities;
	private BLEDevice selectDevice = null;

	public ScanAdapter(Context context, List<BLEDevice> refCourse) {
		setEntities(refCourse);
		this.inflater = LayoutInflater.from(context);
	}

	public void setEntities(List<BLEDevice> entities) {
		if (entities != null) {
			this.entities = entities;
		} else {
			this.entities = new ArrayList<BLEDevice>();
		}

	}

	public void upDada(BLEDevice device) {
		this.entities .add(device);
		Collections.sort(entities);
		this.notifyDataSetChanged();
	}

	public void clear(){
		this.selectDevice = null;
		this.entities.clear();
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return entities.size();
	}
	
	public void setSelectDevice(BLEDevice device){
		this.selectDevice = device;
		notifyDataSetChanged();
	}

	@Override
	public BLEDevice getItem(int index) {
		return entities.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_device, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_basic_name);
			holder.tvMac = (TextView) convertView.findViewById(R.id.tv_basic_mac);
			holder.view = (View) convertView.findViewById(R.id.view_select);
			holder.tvRssi=(TextView) convertView.findViewById(R.id.tvRssi);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final BLEDevice course = entities.get(position);
		holder.tvName.setText(course.mDeviceName);
		holder.tvMac.setText(course.mDeviceAddress);
		holder.tvRssi.setText(course.mRssi+"");
		if(selectDevice != null && selectDevice.mDeviceAddress.equals(course.mDeviceAddress)){
			holder.view.setVisibility(View.VISIBLE);
		}else {
			holder.view.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		private TextView tvName;
		private TextView tvMac;
		private View view;
		TextView tvRssi;

	}
}
