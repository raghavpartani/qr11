package com.example.qrapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {

    private static  final String TAG = "LocationListner";
    ProgressDialog dialog;
    LocationManager locationManager;
    Location loc ;
    Context context;
    MyLocListener ml ;

    boolean isGpsLocation ,isNetworklocation , mCanSendLatLong = false ;
    private Double mLatitude = null, mLongitude = null;

    public MyLocationListener(Context context , MyLocListener Ml ){

        this.context = context;
        this.ml = Ml ;
        dialog = new ProgressDialog(context);

    }
    public interface MyLocListener {
        // you can define any parameter as per your requirement
        void latLongVal(Double lat, Double lng);
    }

    public void getDeviceLocation() {
        //now all permission part complete now let's fetch location
        locationManager=(LocationManager)context.getSystemService(Service.LOCATION_SERVICE);

        dialog.setMessage("Fetching Location");
        dialog.show();
        isGpsLocation=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworklocation=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGpsLocation && !isNetworklocation ){

            Log.d(TAG, "getDeviceLocation() boolean network check ="+isNetworklocation);
            dialog.dismiss();
            getLastlocation();
            showSettingForLocation();
        }
        else{
            getFinalLocation();
        }
    }
    private void getLastlocation() {
        if(locationManager!=null) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria,false);
                Location location=locationManager.getLastKnownLocation(provider);
                Log.e(TAG, "getLastlocation() location value ="+ location);
            } catch (SecurityException e) {
                Log.e(TAG, "getLastlocation() :"+e.getMessage());
            }
        }
    }
    private void getFinalLocation() {
        try{
            if(isNetworklocation){
                locationManager.requestLocationUpdates
                        (LocationManager.NETWORK_PROVIDER,1000*7,10,this);

                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                        Log.d(TAG, "getFinalLocation() Network lat long"+loc);
                    }
                }
            }
            else if(isGpsLocation){

                locationManager.requestLocationUpdates
                        (LocationManager.GPS_PROVIDER,1000*7,10,this);
                if(locationManager!=null){
                    loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(loc!=null){
                        updateUi(loc);
                    }
                }
            }
            else{
                dialog.dismiss();
                Toast.makeText(context, "Can't Get Location", Toast.LENGTH_SHORT).show();
            }

        }catch (SecurityException e){
            dialog.dismiss();
            Toast.makeText(context, "Can't Get Location", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "getFinalLocation() "+e.getMessage() );
        }

    }
    private void updateUi(Location loc) {
        if(loc != null){
            if(loc.getLatitude()==0 && loc.getLongitude()==0){

                Log.d(TAG, "updateUi() going back to getDeviceLocation() .!!!");
                getDeviceLocation();
            }else{
                Log.d(TAG, "updateUi() lat long set");
                mCanSendLatLong = true ;
                mLatitude = loc.getLatitude() ;
                mLongitude = loc.getLongitude();
                ml.latLongVal(mLatitude ,mLongitude);
                dialog.dismiss();
            }
        }
        else{
            dialog.dismiss();
            Toast.makeText(context, "GPS disabled...", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "updateUi() locationManager removed ...!");
        // remove the listener , we don't need it anymore
        locationManager.removeUpdates(this);
    }
    public boolean canSendLatLong (){

        return mCanSendLatLong;
    }
    //    public Double[] latLongValue(){
//
//        Log.d(TAG, "latLongValue() meathod is called ?");
//        Double[] latLongVal = {mLatitude ,mLongitude};
//        dialog.dismiss();
//        return latLongVal ;
//    }
    private void showSettingForLocation() {
        AlertDialog.Builder al=new AlertDialog.Builder(context);
        al.setTitle("Location Not Enabled!");
        al.setMessage("Enable Location ?");
        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        al.show();
    }

    // Interface generated meathods DONOT delete
    public void onLocationChanged(Location location) {
        updateUi(location);
    }
    // Interface generated meathods DONOT delete
    public void onProviderDisabled(String provider) {
        updateUi(null);
    }
    // Interface generated meathods DONOT delete
    public void onProviderEnabled(String provider) {
    }
    // Interface generated meathods DONOT delete
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
