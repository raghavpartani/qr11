package com.example.qrapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;




public class LoginActivity extends AppCompatActivity {
    int RC_SIGN_IN = 0;
    ImageView google;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog pd;

    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        google = findViewById(R.id.google);


        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        String s = preferences.getString("login", "null");
        if (s.equals("on")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            startActivity(intent);
            finish();
        } else {

            google.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();

                }
            });

        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                // The Task
                // returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onStart() {

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        super.onStart();
    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        //overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }

    public void login(View view) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        String dataemail = email.getText().toString().trim();
        String datapass = password.getText().toString().trim();
        if (dataemail.isEmpty()) {
            email.setError("Value Required.");
        }
        else  if(datapass.isEmpty()){
            password.setError("Value Required.");
        }

        else if(!email.getText().toString().trim().matches(emailPattern)){
            email.setError("Invalid email address");
        }
        else {
            pd = new ProgressDialog(LoginActivity.this,R.style.MyAlertDialogStyle);
            pd.setTitle("Connecting Server");
            pd.setMessage("Verifying user details");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            String url = "https://qrphp.000webhostapp.com/login.php";
            StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "" + response.toString(), Toast.LENGTH_SHORT).show();
                    if (response.trim().equals("Login Successful")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        SharedPreferences pre = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pre.edit();
                        editor.putString("login", "on");
                        editor.putString("email", email.getText().toString());
                        editor.apply();
                        startActivity(intent);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(LoginActivity.this, "Please check your internet connection.Something went wrong.", Toast.LENGTH_SHORT).show();

                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("email", email.getText().toString());
                    map.put("password", password.getText().toString());

                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);
        }
    }

}


