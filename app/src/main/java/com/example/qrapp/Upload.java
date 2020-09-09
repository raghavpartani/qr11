package com.example.qrapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Upload {
    String encodedimage;
    ProgressDialog pd;
    String url = "https://qrphp.000webhostapp.com/upload.php";

    public void uploading(final Context context, final String name, final String email) {
        pd = new ProgressDialog(context, R.style.MyAlertDialogStyle);
        pd.setTitle("Connecting Server");
        pd.setMessage("Storing at sever");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                if (response.equals("Saved Successfully"))
                    Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Please check your internet connection.Something went wrong." + error, Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("t1", name);
                map.put("upload", encodedimage);
                map.put("email", email);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }


    public void encodedbitmap(Bitmap qrBits) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        qrBits.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        encodedimage = Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

}
