package champagne86.com.classconnect;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class IndexActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private static final String TAG = "FacebookLogin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mAuth = FirebaseAuth.getInstance();
        if(alreadyLoggedIn()){
            enterHomeActivity();
        }
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
    }

    public boolean alreadyLoggedIn() {
        AccessToken fbAccess = AccessToken.getCurrentAccessToken();
        return (fbAccess != null);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            enterHomeActivity();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                            //Toast.makeText(IndexActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void enterHomeActivity(){
        FirebaseUser user = mAuth.getCurrentUser();


        ApiCaller apiCall = new ApiCaller(this.getApplicationContext());
        updateUI(user);
        Intent chatIntent = new Intent(this, HomeActivity.class);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        apiCall.addUserToDB(user.getUid(),user.getDisplayName(), chatIntent);
    }


    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            findViewById(R.id.login_button).setVisibility(View.GONE);
        } else {

            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
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