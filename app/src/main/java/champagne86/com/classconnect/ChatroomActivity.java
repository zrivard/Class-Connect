package champagne86.com.classconnect;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.emitter.Emitter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = ChatroomActivity.class.getName();
    private static final String CHAT_URL = "https://classconnect-220321.appspot.com/";
    private static final String NEW_MSG_EVENT = "chat message";

    private int nextMessageID = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List messageList = new ArrayList();




    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

                JSONObject data = (JSONObject) args[0];
                    String senderName;
                    String message;
                    int thisId;
                    try {

                        senderName = data.getString("sender");
                        message = data.getString("message");
                        thisId = data.getInt("id");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
            messageList.add(new Message(thisId, message, senderName ));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView = (RecyclerView) findViewById(R.id.dispChatRecyclerView);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
            });

        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        Socket mSocket;
        FirebaseAuth auth;
        FirebaseUser user;


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        createRecyclerView();
        setupLoginButton(auth);

        mAdapter = new MessageAdapter(getBaseContext(), messageList, user);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        try {
            mSocket = IO.socket(CHAT_URL);
            mSocket.on(NEW_MSG_EVENT, onNewMessage);
            mSocket.connect();
            setupSendMessageButton(mSocket, user);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error!");
            Log.e(TAG, "\tError: " +  e.getMessage());
        }


        fillWithNonsenseText(user);
    }

    private void createRecyclerView(){
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.dispChatRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupLoginButton(final FirebaseAuth auth){
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {

                signOut(auth);
            }

        });
    }

    private void setupSendMessageButton(final Socket socket, final FirebaseUser user){
        Button sendMsg = (Button) findViewById(R.id.sendMsgButton);
        final TextInputEditText chatMsgInput = findViewById(R.id.chatMsgInput);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){


                JSONObject args = new JSONObject();

                try {
                    args.put("id", nextMessageID++);
                    args.put("message", chatMsgInput.getText().toString());
                    args.put("sender", user.getUid());
                }
                catch (JSONException e) { }


                socket.emit(NEW_MSG_EVENT, args);

                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                chatMsgInput.setText("");


            }
        });
    }

    public void signOut(final FirebaseAuth auth) {
        auth.signOut();

        LoginManager.getInstance().logOut();
        Intent indexIntent = new Intent(ChatroomActivity.this, IndexActivity.class);
        startActivity(indexIntent);
        finish();
    }

    public void fillWithNonsenseText(FirebaseUser user) {
        messageList.add(new Message(nextMessageID++, "I AM THE USER", user.getUid()));
        messageList.add(new Message(nextMessageID++, "I AM ANOTHER STUDENT", "Example Student"));
    }
}