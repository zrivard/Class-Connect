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
import java.util.HashMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private FragmentActivity settFrgmt;
    private ApiCaller mApi;
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
            /** Slightly hacky with hardcoding path, might need to be changed but I think all android devices
             * have an android folder and when I tried to use the constant above it wasn't working
             * */

            File icsFile = new File("/sdcard/Download/ical.ics");
            Log.d(TAG, "Created new FILE\n");

            FileInputStream fis = new FileInputStream(icsFile);
            Log.d(TAG, "PASSED INPUT STREAM\n");

            if (fis != null) {
                TextView textView = (TextView) getView().findViewById(R.id.courseList);
                textView.setText("");
                //Should deregister users from classes here since they'll be uploading a new timetable
                InputStreamReader isr = new InputStreamReader(fis);

                BufferedReader buff = new BufferedReader(isr);
                HashMap<String, Boolean> enrolledClasses = new HashMap<>();

                String line = null;
                FirebaseAuth auth;
                FirebaseUser user;
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                User currUser = new User(user, user.getDisplayName());
                while ((line = buff.readLine()) != null) {
                    if (line.contains("SUMMARY:")) {
                        Log.d(TAG, line);
                        String parsed = parseClass(line);
                        Log.d(TAG, parsed);
                        enrolledClasses.put(parsed, true);
                    }
                }
                mApi = new ApiCaller(this.getContext());
                mApi.setUserClasses(user.getUid() ,enrolledClasses);
                fis.close();
            }
            else{
                Toast.makeText(settFrgmt, "Could not find calendar file. Download from ubc ssc", Toast.LENGTH_LONG).show();
                fis.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "FAILED SOMEWHERE\n");
        }
        return 1;
    }

    public String parseClass(String oldClass){
        String noSummary = oldClass.substring(oldClass.indexOf(":")+1);
        Log.d(TAG, "NO SUMMARY === " + noSummary);
        int space1 = noSummary.indexOf(" ");
        Log.d(TAG,"INDEX 1 === " + space1);
        int space2 = noSummary.indexOf(" ", space1 +1);
        Log.d(TAG,"INDEX 2 === " + space2);

        String rep = noSummary.substring(0, space2);//.replace(' ', '_');
        return rep.replace(' ', '_');
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
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        Log.d(TAG, "ENTERING REQUESTING STORAGE PERMISSION\n");

            ActivityCompat.requestPermissions(settFrgmt,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(settFrgmt, "Got permission", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(settFrgmt, "Permission request denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
