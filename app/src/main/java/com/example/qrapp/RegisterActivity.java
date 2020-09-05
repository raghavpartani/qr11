package com.example.qrapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    ImageView google;
    GoogleSignInClient mGoogleSignInClient;

    int select=0;
    ProgressDialog pd;

    EditText name,email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
    }



    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }


    public void register(View view) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String dataemail = email.getText().toString().trim();
        String datapass = password.getText().toString().trim();
        String dataename = name.getText().toString().trim();
        if (dataemail.isEmpty()) {
            email.setError("Value Required.");
        }
        else  if(datapass.isEmpty()){
            password.setError("Value Required.");
        }
        else  if(dataename.isEmpty()){
            name.setError("Value Required.");
        }
        else if(!email.getText().toString().trim().matches(emailPattern)){
            email.setError("Invalid email address");
        }
        else {

            pd = new ProgressDialog(RegisterActivity.this,R.style.MyAlertDialogStyle);
            pd.setTitle("Connecting Server");
            pd.setMessage("Verifying user details");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            String url = "https://qrphp.000webhostapp.com/select.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "" + response.toString(), Toast.LENGTH_SHORT).show();
                    if (response.trim().equals("successfully registered"))
                        finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(RegisterActivity.this, "Please check your internet connection.Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name.getText().toString());
                    map.put("email", email.getText().toString());
                    map.put("password", password.getText().toString());

                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
        }
    }

    public void openLoginPage(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
