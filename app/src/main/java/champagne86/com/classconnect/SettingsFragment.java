package champagne86.com.classconnect;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.github.nkzawa.socketio.client.IO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private FragmentActivity settFrgmt;

    private int STORAGE_PERMISSION_CODE = 1;
    final String FILENAME = "/ical.ics";
    private static final String TAG = "SettingsFrag";

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settFrgmt = getActivity();
        setupSendMessageButton();


    }

    private void setupSendMessageButton(){

        //LinearLayout msgLayout = (LinearLayout) getView().findViewById(R.id.chatInputLayout);
        Button uploadButton = (Button) getView().findViewById(R.id.uploadIcsBtn);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){
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
                TextView textView = (TextView) getView().findViewById(R.id.courseList);
                textView.setText("");
                InputStreamReader isr = new InputStreamReader(fis);
                Log.d(TAG, "ISR\n");

                BufferedReader buff = new BufferedReader(isr);
                Log.d(TAG, "BUFFER READER\n");

                String line = null;
                FirebaseAuth auth;
                FirebaseUser user;
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                User currUser = new User(user, user.getDisplayName());
                Log.d(TAG, "COURSE LIST:\n");
                while ((line = buff.readLine()) != null) {
                    if (line.contains("SUMMARY:")) {
                        Log.d(TAG, line);
                        String[] parts = line.split(": ");
                        String className = line.substring(line.indexOf(":")+1);
                        Classroom currClass = new Classroom(className);
                        currUser.addClass(currClass);
                        textView.append(className + "\n");
                    }
                }

                fis.close();
            }
            else{
                Toast.makeText(settFrgmt, "Could not find calendar file. Download from ubc ssc", Toast.LENGTH_LONG).show();

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
        if (ContextCompat.checkSelfPermission(settFrgmt,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(settFrgmt, "Read oern",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        Log.d(TAG, "ENTERING REQUESTING STORAGE PERMISSION\n");
        if (ActivityCompat.shouldShowRequestPermissionRationale(settFrgmt,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(settFrgmt)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(settFrgmt,
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
            ActivityCompat.requestPermissions(settFrgmt,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(settFrgmt, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(settFrgmt, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
