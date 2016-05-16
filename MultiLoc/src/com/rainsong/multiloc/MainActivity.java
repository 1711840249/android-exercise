package com.rainsong.multiloc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.SDKInitializer;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
    MapView mMapView = null;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LatLng mCurrentPosition;
    
    boolean isFirstLoc = true;
    Button btn_current;
    Button btn_add;
    Button btn_clear;
    EditText lat;
	EditText lon;
	EditText et_distance;
	List<MeasurePoint> points = new ArrayList<MeasurePoint>();
	BitmapDescriptor bd;
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);
        btn_current = (Button) findViewById(R.id.btn_current);
		OnClickListener currentListener = new OnClickListener() {
			public void onClick(View v) {
				//mLocClient.requestLocation();
				if (mCurrentPosition != null) {
					lat.setText(Double.toString(mCurrentPosition.latitude));
					lon.setText(Double.toString(mCurrentPosition.longitude));
				}
			}
		};
		btn_current.setOnClickListener(currentListener);
		
		btn_add = (Button) findViewById(R.id.btn_add);
		OnClickListener addListener = new OnClickListener() {
			public void onClick(View v) {
				double latitude = Double.valueOf(lat.getText().toString());
				double longitude = Double.valueOf(lon.getText().toString());
				LatLng position = new LatLng(latitude, longitude);
				int distance = Integer.valueOf(et_distance.getText().toString());
				MeasurePoint point = new MeasurePoint(position, distance);
				points.add(point);
				OverlayOptions ooCircle = new CircleOptions().fillColor(0x000000FF)
						.center(position).stroke(new Stroke(1, 0xAA000000))
						.radius(distance);
				mBaiduMap.addOverlay(ooCircle);
				OverlayOptions ooMarker = new MarkerOptions().position(position).icon(bd)
						.zIndex(9).draggable(true);
				mBaiduMap.addOverlay(ooMarker);

			}
		};
		btn_add.setOnClickListener(addListener);

		btn_clear = (Button) findViewById(R.id.btn_clear);
		OnClickListener clearListener = new OnClickListener() {
			public void onClick(View v) {
				mBaiduMap.clear();
				points.clear();
			}
		};
		btn_clear.setOnClickListener(clearListener);
		bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);
		lat = (EditText) findViewById(R.id.latitude);
		lon = (EditText) findViewById(R.id.longitude);
		et_distance = (EditText) findViewById(R.id.distance);
        //��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
    }
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d(TAG, "onReceiveLocation");
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			mCurrentPosition = ll;
			Log.d(TAG, "mCurrentPosition: " + mCurrentPosition);
			if (isFirstLoc) {
				isFirstLoc = false;
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

    @Override  
    protected void onDestroy() {  
        super.onDestroy();
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();
    }
    private static class MeasurePoint {
		private final LatLng position;
		private final int distance;
		
		public MeasurePoint(LatLng position, int distance) {
			this.position = position;
			this.distance = distance;
		}
		
		public LatLng getPosition() {
			return this.position;
		}
		
		public int getDistance() {
			return this.distance;
		}
	}
}

