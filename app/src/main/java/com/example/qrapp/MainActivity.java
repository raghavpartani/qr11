package com.example.qrapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.WriterException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    Button generateBtn,scanBtn;
    ImageView qrImage;
    GoogleSignInClient mGoogleSignInClient;
    int select=0;
    long baskpress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //qrvalue = findViewById(R.id.qrInput);
        generateBtn = findViewById(R.id.generateBtn);
        scanBtn = findViewById(R.id.scanBtn);
        qrImage = findViewById(R.id.qrPlaceHolder);




        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Scanner.class));
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Select_QR_Code.class));
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (acct != null) {
            select=1;


            String personEmail = acct.getEmail();
            SharedPreferences pre=getSharedPreferences("login",MODE_PRIVATE);
            SharedPreferences.Editor editor=pre.edit();
            editor.putString("login","on");
            editor.putString("email",personEmail);
            editor.apply();
            }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signout, menu);
        return true;
    }

    public void signout(MenuItem item) {

        if(select==1) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("login","off");
                    editor.putString("email",null);
                    editor.commit();
                    Toast.makeText(MainActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
        else {
            SharedPreferences preferences=getSharedPreferences("login",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("login","off");
            editor.putString("email",null);
            editor.commit();
            Toast.makeText(MainActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
    public void onBackPressed() {
//        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
//
//        ab.setTitle("warning");
//        ab.setMessage("are u sure to exit?");
//
//        ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                moveTaskToBack(true);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(1);
//            }
//        });
//        ab.setNegativeButton("no", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog ad =ab.create();
//
//        ad.show();


        if(baskpress+2000>System.currentTimeMillis()){
            moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

        }
        else {
            Toast.makeText(getBaseContext(),"press back again to exit",Toast.LENGTH_LONG).show();
        }
        baskpress =System.currentTimeMillis();

    }



}
