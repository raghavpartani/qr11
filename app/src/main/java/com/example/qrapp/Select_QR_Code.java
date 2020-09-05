package com.example.qrapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Select_QR_Code extends AppCompatActivity implements RecyclerAdapter.OnqrcodeListener {

    private static final String TAG = "Select_QR_Code";

    private  String[] names = {"Create QR Code for Email","Create QR Code for Wifi","Create QR Code for Event","Create QR Code for Contact","Create QR Code for Text","Create QR Code for Website","Create QR Code for Location","Create QR Code for Phone","Create QR Code for Message"};

    private  int [] image = {R.drawable.email,R.drawable.wifi,R.drawable.event,R.drawable.contact,R.drawable.text,R.drawable.url,R.drawable.location,R.drawable.phone,R.drawable.sms};

    private List<QRcode> qRcodeList = new ArrayList<>();
    private RecyclerView recyclerView;
  LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__qr__code);
        recyclerView = findViewById(R.id.home_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        preparedTheList();
        RecyclerAdapter adapter = new RecyclerAdapter(qRcodeList,this);
        recyclerView.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void preparedTheList(){

        int count = 0;
        for(String name : names){

            QRcode qRcode = new QRcode(name,image[count]);
            qRcodeList.add(qRcode);
            count ++;
        }

    }

    //Intent pass to all activity...............

    @Override
    public void onqrClick(int position) {


        final Intent intent;
        switch (position){

            case 0:
                 intent = new Intent(getApplicationContext(),Email.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;
            case 1:
                 intent = new Intent(getApplicationContext(),Wifi.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;

            case 2:
                intent = new Intent(getApplicationContext(),Event.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(getApplicationContext(),Contact_info.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;

            case 4:
                intent = new Intent(getApplicationContext(),Text.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(getApplicationContext(),URL.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;

            case 6:
                intent = new Intent(getApplicationContext(),GeoLocation.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(getApplicationContext(),Phone.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;

            case 8:
                intent = new Intent(getApplicationContext(),SMS.class);
                intent.putExtra("qrcodename",qRcodeList.get(position).getQrcodename());
                startActivity(intent);
                break;

                default:
                    break;

        }



    }
}
