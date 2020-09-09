package com.example.qrapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Text extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Text Class";
    // variable name changed .
    boolean mPermission = false;
    boolean isQRGenerated = false;


    ProgressDialog pd;
    String encodedimage;
    String url = "https://qrphp.000webhostapp.com/upload.php";
    GoogleSignInClient mGoogleSignInClient;


    EditText editText;
    ImageView imageView;
    Button button;
    Bitmap qrBits;
    String email;
    int RC_SIGN_IN = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editText = findViewById(R.id.text_input);
        imageView = findViewById(R.id.qrcode_image);
        button = findViewById(R.id.creare_btn);

        // create qr code btn
        button.setOnClickListener(this);
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        email = preferences.getString("email", "null");


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
                final Dialog d=new Dialog(Text.this);
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
//                        pd = new ProgressDialog(Text.this,R.style.MyAlertDialogStyle);
//                        pd.setTitle("Connecting Server");
//                        pd.setMessage("Storing at sever");
//                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                        pd.show();
//                        encodedbitmap(qrBits);
//                        upload();
//                        d.cancel();

                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                        mGoogleSignInClient = GoogleSignIn.getClient(Text.this, gso);
                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Text.this);
                        if (acct == null) {
                            final Dialog d = new Dialog(Text.this);
                            d.setContentView(R.layout.popuogoogleicon);
                            ImageView google = d.findViewById(R.id.google);
                            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            d.show();
                            google.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    signIn();
                                    d.dismiss();
                                }
                            });
                        } else {
                            Upload upload = new Upload();
                            upload.encodedbitmap(qrBits);
                            String name = "Text:" + editText.getText().toString();
                            upload.uploading(Text.this, name, email);
                        }
                        d.cancel();
                    }
                });
                break;

            case R.id.delete:
                deleteImage();
                break;

            case R.id.share:
                if (!isQRGenerated) {
                    Toast.makeText(this, "Please Create QR Code.!", Toast.LENGTH_SHORT).show();
                } else {
                    shareImage();
                }
                break;


            default:

        }
        return super.onOptionsItemSelected(item);

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
            Toast.makeText(this, "You have successfully logged in now you can use our cloud services also", Toast.LENGTH_SHORT).show();
            String personEmail = account.getEmail();
            SharedPreferences pre = getSharedPreferences("login", MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();
            editor.putString("login", "on");
            editor.putString("email", personEmail);
            editor.apply();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(Text.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }


    private void deleteImage() {
        if (!isQRGenerated) {
            Toast.makeText(this, "QR code not generated.!", Toast.LENGTH_SHORT).show();
        } else {
            imageView.setImageDrawable(null);
            editText.getText().clear();
            Toast.makeText(this, "Delete QR Code", Toast.LENGTH_SHORT).show();
            imageView.setBackgroundColor(Color.rgb(128, 128, 128));
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


    private void saveToGallery() {
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
    private boolean checkpermission() {
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
                            Toast.makeText(Text.this, "Permissions granted", Toast.LENGTH_SHORT).show();
                        } else {
                            mPermission = false;
                            Toast.makeText(Text.this, "Permissions are Required.", Toast.LENGTH_SHORT).show();
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
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    @Override
    public void onClick(View v) {
        // create qr code btn
        if (v == button) {
            String data = editText.getText().toString();
            if (data.isEmpty()) {
                editText.setError("Value Required.");

            } else {
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
    }
}
