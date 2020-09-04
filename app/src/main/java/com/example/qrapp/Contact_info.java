package com.example.qrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Contact_info extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener {

    private static final String TAG = "Contact_info Class";
    // variable name changed .
    boolean mPermission = false;
    boolean isQRGenerated = false;
    EditText editText_first_name,editText_last_name,editText_org,editText_email,editText_phone,editText_address,editText_city,editText_state,editText_contry,editText_pincode,editText_url;
    ImageView imageView;
    Button button;
    private ScrollView scrollView;
    private CardView cardView;
    private RelativeLayout root;
    private CountryCodePicker ccp;
    ProgressDialog pd;
    Bitmap qrBits;
    String email;

    String encodedimage;
    String url = "https://qrphp.000webhostapp.com/upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editText_first_name = findViewById(R.id.first_name_input);
        editText_last_name = findViewById(R.id.last_name_input);
        editText_org = findViewById(R.id.org_input);
        editText_email = findViewById(R.id.email_input);
        editText_phone = findViewById(R.id.phone_input);
        editText_address = findViewById(R.id.address_input);
        editText_city = findViewById(R.id.city_input);
        editText_state = findViewById(R.id.state_input);
        editText_contry = findViewById(R.id.country_input);
        editText_pincode = findViewById(R.id.pincode_input);
        editText_url = findViewById(R.id.url_web_input);

        imageView = findViewById(R.id.qrcode_image);
        button = findViewById(R.id.creare_btn);
        ccp = findViewById(R.id.ccp);

        scrollView = findViewById(R.id.scroll);
        root = findViewById(R.id.root);
        cardView = findViewById(R.id.cv_marker);
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        email = preferences.getString("email", "null");

        //create qr code btn
        button.setOnClickListener(this);

        //down button
        cardView.setOnClickListener(this);


        root.setOnTouchListener(this);

    }

    // Action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.download:
                if (!isQRGenerated) {
                    Toast.makeText(this, "QR code not generated.!", Toast.LENGTH_SHORT).show();
                    break;
                }
                final Dialog d=new Dialog(Contact_info.this);
                d.setContentView(R.layout.server_or_internal);
                Button server=d.findViewById(R.id.server);
                Button internal=d.findViewById(R.id.internal);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                d.show();

                internal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // checkpermission() called and then if-else
                        // used to confirm for status of permission.
                        checkpermission();
                        if (!checkpermission()) {
                            checkpermission();
                        } else {
                            saveToGallery();
                        }
                        d.cancel();
                    }
                });
                server.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd = new ProgressDialog(Contact_info.this,R.style.MyAlertDialogStyle);
                        pd.setTitle("Connecting Server");
                        pd.setMessage("Storing at sever");
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();
                        encodedbitmap(qrBits);
                        upload();
                        d.cancel();
                    }
                });
                break;
            case R.id.delete:
                deleteImage();
                break;

            case R.id.share:
                if(!isQRGenerated){
                    Toast.makeText(this, "Please Create QR Code.!", Toast.LENGTH_SHORT).show();
                }else {
                    shareImage();
                }
                break;


            default:

        }
        return super.onOptionsItemSelected(item);

    }


    private void deleteImage() {
        if (!isQRGenerated) {
            Toast.makeText(this, "QR code not generated.!", Toast.LENGTH_SHORT).show();
        } else {
            imageView.setImageDrawable(null);
            editText_first_name.getText().clear();
            editText_last_name.getText().clear();
            editText_org.getText().clear();
            editText_email.getText().clear();
            editText_phone.getText().clear();
            editText_address.getText().clear();
            editText_city.getText().clear();
            editText_state.getText().clear();
            editText_contry.getText().clear();
            editText_pincode.getText().clear();
            editText_url.getText().clear();
            Toast.makeText(this, "Delete QR Code", Toast.LENGTH_SHORT).show();
            imageView.setBackgroundColor(Color.rgb(128,128,128));
            isQRGenerated = false;
        }
    }


    private void shareImage() {
        // share using File Provider

        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "image.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/png");

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void saveToGallery () {
        // checking for imageview is empty or not.

        if (!isQRGenerated) {
            Toast.makeText(this, "QR code not generated.!", Toast.LENGTH_SHORT).show();
        } else {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            FileOutputStream fileOutputStream = null;
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath() + "/Qr code");
            dir.mkdir();

            String filename = String.format("%d.png", System.currentTimeMillis());
            File outfile = new File(dir, filename);

            try {
                fileOutputStream = new FileOutputStream(outfile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText(this, "QR code saved \nInternal storage/Qr code", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d(TAG, "saveToGallery() EXCEPTION : " + e.getMessage());
                Toast.makeText(this, "Could not Download.!!!", Toast.LENGTH_SHORT).show();
            }
        }

    }
// permission for storage
    private boolean checkpermission () {
        // checkpermission returns boolean value.

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        Dexter.withActivity(this)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Log.d(TAG, "onPermissionsChecked() Report = " + report);
                        if (report.areAllPermissionsGranted()) {
                            mPermission = true;
                            Toast.makeText(Contact_info.this, "Permissions granted", Toast.LENGTH_SHORT).show();
                        } else {
                            mPermission = false;
                            Toast.makeText(Contact_info.this, "Permissions are Required.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();
        return mPermission;
    }

    // used to confirm if imageview is empty or not
    // BUT in this case its never empty as you have made its background grey  .
    private boolean hasImage (@NonNull ImageView view){
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }


    // scroll down code
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                isInBound();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                isInBound();
                return true;
            case (MotionEvent.ACTION_UP) :
                isInBound();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                isInBound();
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
    private void isInBound(){
        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (button.getLocalVisibleRect(scrollBounds)) {

            cardView.setVisibility(View.GONE);
        } else {

            cardView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        //create qr code btn
        if(v == button){
            String data = (editText_first_name.getText().toString())+" "+ (editText_last_name.getText().toString())+"\n"+ (editText_org.getText().toString())+"\n"+(editText_email.getText().toString())+"\n"+(editText_phone.getText().toString())+"\n"+(editText_address.getText().toString())+","+(editText_city.getText().toString())+","+(editText_state.getText().toString())+","+(editText_contry.getText().toString())+"-"+(editText_pincode.getText().toString())+"\n"+(editText_url.getText().toString());

            String data_first = editText_first_name.getText().toString() ;
            String data_last = editText_last_name.getText().toString() ;
            String data_org = editText_org.getText().toString() ;
            String data_email = editText_email.getText().toString() ;
            String data_phone = editText_phone.getText().toString() ;
            String data_address = editText_address.getText().toString() ;
            String data_city = editText_city.getText().toString() ;
            String data_state = editText_state.getText().toString() ;
            String data_country = editText_contry.getText().toString() ;
            String data_pincode = editText_pincode.getText().toString() ;
            String data_url = editText_url.getText().toString() ;

            if (data_first.trim().isEmpty()) {
                editText_first_name.setError("Value Required.");
            } else if(data_last.trim().isEmpty()){
                editText_last_name.setError("Value Required.");
            }else if (data_org.trim().isEmpty()){
                editText_org.setError("Value Required.");
            }else if(data_email.trim().isEmpty()){
                editText_email.setError("Value Required.");
            } else if(data_phone.trim().isEmpty()){
                editText_phone.setError("Value Required.");
            }else if(data_address.trim().isEmpty()){
                editText_address.setError("Value Required.");
            }else if(data_city.trim().isEmpty()){
                editText_city.setError("Value Required.");
            }else  if(data_state.trim().isEmpty()){
                editText_state.setError("Value Required.");
            }else if(data_country.trim().isEmpty()){
                editText_contry.setError("Value Required.");
            } else if(data_pincode.trim().isEmpty()){
                editText_pincode.setError("Value Required.");
            }else if(data_url.trim().isEmpty()){
                editText_url.setError("Value Required.");
            } else if(!editText_first_name.getText().toString().matches("[a-z,A-Z]*")){
                editText_first_name.setError("Enter Only Character.");
            } else if(!editText_last_name.getText().toString().matches("[a-z,A-Z]*")){
                editText_last_name.setError("Enter Only Character.");
            }  else if(!Patterns.EMAIL_ADDRESS.matcher(editText_email.getText().toString()).matches()){
                editText_email.setError("Please Enter Valid Email.");
            } else if(editText_phone.length()<4 || editText_phone.length()>12){
                editText_phone.setError("Enter Valid Phone Number.");
            }
            else if(!editText_pincode.getText().toString().matches("[0-9]{6}")){
                editText_pincode.setError("Enter Only 6 Digit Number.");
            }
            else {
                QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 300);

                try {
                    qrBits = qrgEncoder.getBitmap();

                    imageView.setImageBitmap(qrBits);
                    isQRGenerated = true;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // dwon btn
        if(v == cardView){
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            button.isShown();

        }


    }
    private void upload() {


        final String name="Contact_info:"+editText_first_name.getText().toString()+" "+editText_last_name.getText().toString();

        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Toast.makeText(Contact_info.this, ""+response.toString(), Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Contact_info.this, "Please check your internet connection.Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("t1",name);
                map.put("upload",encodedimage);
                map.put("email",email);
                return map;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }



    private void encodedbitmap(Bitmap qrBits) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        qrBits.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        encodedimage= Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
}

