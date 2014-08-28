package com.neolee.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class MainService extends Service {
	private LocationManager locationManager;
	private Location location;
	private String provider;
	private Address address;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

    @Override
    public void onCreate() {
        // The service is being created
    	Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show(); 
    	
    	// Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        Thread thr = new Thread(null, mTask, "MainService");
        thr.start();
    }

    /**
     * The function that runs in our worker thread
     */
    Runnable mTask = new Runnable() {
        public void run() {
            // Normally we would do some work here...
        	
        	// ��ȡLocationManager����
            locationManager =(LocationManager) MainService.this.getSystemService(Context.LOCATION_SERVICE);

            // ��ȡLocation Provider
            getProvider();

            // ���δ����λ��Դ����GPS���ý���
            //openGPS();
            
            // ��ȡλ��
            location = locationManager.getLastKnownLocation(provider);

            // ��ʾλ����Ϣ
            updateWithNewLocation(location);

            // ע�������locationListener����2��3���������Կ��ƽ���gps��Ϣ��Ƶ���Խ�ʡ��������2������Ϊ���룬
            // ��ʾ����listener�����ڣ���3������Ϊ��,��ʾλ���ƶ�ָ�������͵���listener
            locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);

        	while(true);
        	
            // Done with our work...  stop the service!
            //MainService.this.stopSelf();
        }
    };
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
    	Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        
    	// We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    

    @Override
    public void onDestroy() {
      Toast.makeText(this, "stop service", Toast.LENGTH_SHORT).show(); 
    }

    // ��ȡLocation Provider
    private void getProvider(){
    	// ����λ�ò�ѯ����
        Criteria criteria = new Criteria();

        // ��ѯ���ȣ���
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // �Ƿ��ѯ��������
        //criteria.setAltitudeRequired(false);

        // �Ƿ��ѯ��λ��:��
        //criteria.setBearingRequired(false);

        // �Ƿ������ѣ���
        //criteria.setCostAllowed(true);

        // ����Ҫ�󣺵�
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        // ��������ʵķ���������provider����2������Ϊtrue˵��,���ֻ��һ��provider����Ч��,�򷵻ص�ǰprovider
        provider = locationManager.getBestProvider(criteria,true);   

    }

    // �ж��Ƿ���GPS����δ��������GPS���ý���
    private void openGPS() {        

        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        ||locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        ) {

           Toast.makeText(this, "λ��Դ�����ã�", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "λ��Դδ���ã�", Toast.LENGTH_SHORT).show();

        // ת��GPS���ý���
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);

        startActivity(intent); 

    }
    
    // Gps��Ϣ������
    private final LocationListener locationListener = new LocationListener(){

    	// λ�÷����ı�����
        public void onLocationChanged(Location location) {

       updateWithNewLocation(location);

        }

        // provider���û��رպ����
        public void onProviderDisabled(String provider){

        updateWithNewLocation(null);

        }

        // provider���û����������
        public void onProviderEnabled(String provider){ }

        // provider״̬�仯ʱ����
        public void onStatusChanged(String provider, int status, Bundle extras){ }

    };


    // Gps���������ã�����λ����Ϣ
    private void updateWithNewLocation(Location location) {

        String latLongString;

        //TextView myLocationText= (TextView)findViewById(R.id.text);

        if (location != null) {

        	double lat =location.getLatitude();

        	double lng =location.getLongitude();

        	latLongString = "γ��:" + lat + "/n����:" + lng;

        } else {

        	latLongString = "�޷���ȡ������Ϣ";

        }

        //Toast.makeText(this, latLongString, Toast.LENGTH_LONG).show();
        //myLocationText.setText("����ǰ��λ����:/n" +

        //latLongString+"/n"+getAddressbyGeoPoint(location));

    }


}
