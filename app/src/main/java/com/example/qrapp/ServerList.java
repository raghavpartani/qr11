package com.example.qrapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerList extends AppCompatActivity {
    ArrayList<String> ls;
    ArrayAdapter<String> a;
    ListView l;
    String email;
    int i;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        email = preferences.getString("email", "null");
        String url1="https://qrphp.000webhostapp.com/select_for_one.php?email="+email;

        pd = new ProgressDialog(ServerList.this,R.style.MyAlertDialogStyle);
        pd.setTitle("Connecting Server");
        pd.setMessage("Fetching from server");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

            ls=new ArrayList<>();;
            l=(ListView) findViewById(R.id.lst);
            a=new ArrayAdapter<>(ServerList.this,android.R.layout.simple_list_item_1,ls);
            l.setAdapter(a);
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    pd.dismiss();
                    if(response==null)
                    {
                        Toast.makeText(ServerList.this, "You have not stored anything at server side", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    //Toast.makeText(ServerList.this, ""+response, Toast.LENGTH_SHORT).show();
                    for(int i=0;i<response.length();i++)
                    {
                        try {
                            JSONObject jsonObject=response.getJSONObject(i);
                            String name= null;
                            name = jsonObject.getString("name");
                            ls.add(name);
                            a.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Toast.makeText(ServerList.this, "You have not stored anything at server side or your internet is not working", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue queue=Volley.newRequestQueue(ServerList.this);
            queue.add(jsonArrayRequest);
//
//        StringRequest jsonArrayRequest=new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                pd.dismiss();
//
//                Toast.makeText(ServerList.this, ""+response, Toast.LENGTH_SHORT).show();
//
//                try {
//                    JSONArray jsonArray=new JSONArray(response);
//                    for(i=0;i<jsonArray.length();i++){
//                        JSONObject jsonObject=jsonArray.getJSONObject(i);
//                        String name=jsonObject.getString("Name");
//                        ls.add(name);
//                        a.notifyDataSetChanged();
//                    }
//                    Toast.makeText(ServerList.this, "you have stored nothing at server side"+i, Toast.LENGTH_SHORT).show();
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String,String> map=new HashMap<>();
//                    map.put("email",email);
//                    return map;
//                }
//            };
//            RequestQueue queue= Volley.newRequestQueue(getApplicationContext());
//            queue.add(jsonArrayRequest);


            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tv = (TextView) view;
                    String s="https://qrphp.000webhostapp.com/"+tv.getText().toString()+".png";
                    //Toast.makeText(ServerList.this, ""+s, Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ServerList.this,Suggestion.class);
                    intent.putExtra("key","server");
                    intent.putExtra("url",s);
                    startActivity(intent);



                }
            });
        }
    }