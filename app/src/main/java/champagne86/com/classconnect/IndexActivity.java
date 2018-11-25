package champagne86.com.classconnect;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import android.support.v7.app.AlertDialog;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class IndexActivity extends AppCompatActivity {
    //final String TAG = "indexActivity";
    private int STORAGE_PERMISSION_CODE = 1;
    final String FILENAME = "/ical.ics";
    private static final String TAG = "FacebookLogin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Button icsUploadButton = findViewById(R.id.icsUploadBtn);
        icsUploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                /*Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
                //Log.d(TAG, "BUTTON CLICED\n");
                readCalendar();
            }


        });
    }


    int readCalendar() {
        Log.d(TAG, "Entered read calendar\n");
        if (!isExternalStorageReadable()) {
            Log.d(TAG, "NOT READABLE\n");
            return 0;
        }
        requestPermissions();
        Log.d(TAG, "SUCCESSFULLY GOT PERMISSIONS\n");
        StringBuilder sb = new StringBuilder();
        try {
            Log.d(TAG, "HIT TRY BLOCK\n");
            Log.d(TAG, Environment.DIRECTORY_DOWNLOADS);
            //File icsFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + FILENAME);
            // Hacky version: needs to be changed
            File icsFile = new File("/sdcard/Download/ical.ics");
            ////////////
            Log.d(TAG, "Created new FILE\n");

            FileInputStream fis = new FileInputStream(icsFile);
            Log.d(TAG, "PASSED INPUT STREAM\n");

            if (fis != null) {

                InputStreamReader isr = new InputStreamReader(fis);
                Log.d(TAG, "ISR\n");

                BufferedReader buff = new BufferedReader(isr);
                Log.d(TAG, "BUFFER READER\n");

                String line = null;
                Log.d(TAG, "COURSE LIST:\n");
                while ((line = buff.readLine()) != null) {
                    if (line.contains("SUMMARY:")) {
                        Log.d(TAG, line);
                    }
                }
                fis.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "FAILED SOMEWHERE\n");
        }
        return 1;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    void requestPermissions(){
        if (ContextCompat.checkSelfPermission(IndexActivity.this,
               android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(IndexActivity.this, "Read oern",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        Log.d(TAG, "ENTERING REQUESTING STORAGE PERMISSION\n");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(IndexActivity.this,
                                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}