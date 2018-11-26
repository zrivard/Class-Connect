package champagne86.com.classconnect;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.Reference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseApp mApp;
    private FirebaseFirestore mDb;
    private CallbackManager mCallbackManager;
    final Context context = this;

    private String uid;
    private User user;

    private static final String TAG = "FacebookLogin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);


        Button logoutButton = findViewById(R.id.logoutButton);
        Button continueButton = findViewById(R.id.continueButton);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        mDb = mDb.getInstance();



        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Switch anonymousUser = findViewById(R.id.anonymousUsr);
                TextInputEditText usernameInput = findViewById(R.id.usernameInput);
                final FirebaseUser currentUser = mAuth.getCurrentUser();
                final String username;

                username = usernameInput.getText().toString();
                if (!anonymousUser.isChecked()) {
                    checkUsrName();
                }


                if (currentUser == null) {
                    checkFb(currentUser);
                }



               final Map<String, Object> data1 = new HashMap<>();
                if (anonymousUser.isChecked()) {
                    data1.put("name", "Anonymous");
                }
                else {
                    data1.put("name", username);
                }
                data1.put("classes", Arrays.asList("NO CLASSES"));


                DocumentReference docRef = mDb.collection("users").document(uid);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Map<String, Object> data = document.getData();
                                if (anonymousUser.isChecked()){
                                    data.put("name", "Anonymous");
                                }
                                else {
                                    data.put("name", username);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                CollectionReference users = mDb.collection("users");
                                users.document(uid).set(data1);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                if (currentUser != null && (usernameValid(username) || anonymousUser.isChecked())) {
                    Intent homeIntent = new Intent(IndexActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                }
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser != null)                 public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        final TextInputEditText usernameInput = findViewById(R.id.usernameInput);
        Switch anonymousUser = findViewById(R.id.anonymousUsr);


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseAuth auth;
                            auth = FirebaseAuth.getInstance();
                            updateUI(user);
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            //Toast.makeText(IndexActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    public boolean usernameValid(String username) {


        if (username.matches("[A-Za-z0-9]+")) {
            return true;
        } else {
            return false;
        }
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }


    public void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.continueButton).setVisibility(View.GONE);
            findViewById(R.id.login_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.continueButton).setVisibility(View.VISIBLE);
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
        }
    }


    public void checkUsrName() {

        TextInputEditText input = (TextInputEditText) findViewById(R.id.usernameInput);
        String username = input.getText().toString();

        if (username.length() == 0) {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.usrname_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set usrNamePromptPrompt.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userPromptInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // get user input and set it to result
                            // edit text
                            TextInputEditText input = (TextInputEditText) findViewById(R.id.usernameInput);
                            input.setText(userPromptInput.getText());
                        }
                    });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    public void checkFb(FirebaseUser user) {


        if (user == null) {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.fblogin_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set usrNamePromptPrompt.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            // set dialog message
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();


        //    if (i == R.id.buttonFacebookSignout) {
        //        signOut();
        //     }
    }
}

