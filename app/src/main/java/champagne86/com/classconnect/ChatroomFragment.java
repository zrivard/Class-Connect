package champagne86.com.classconnect;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

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


public class ChatroomFragment extends Fragment {

    private FragmentActivity chatFrgmt;
    private static final String TAG = ChatroomFragment.class.getName();
    private static final String CHAT_URL = "https://classconnect-220321.appspot.com/";
    private static final String NEW_MSG_EVENT = "chat message";

    private int nextMessageID = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List messageList = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chatroom, container, false);
    }

   //View v = getView();


    public void onViewCreated(View view, Bundle savedInstanceState) {

        createRecyclerView(view);

    }




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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView = (RecyclerView) getView().findViewById(R.id.dispChatRecyclerView);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
            });

        }

    };



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatFrgmt = getActivity();

        Socket mSocket;
        FirebaseAuth auth;
        FirebaseUser user;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


       // setupLoginButton(auth);

        mAdapter = new MessageAdapter(chatFrgmt, messageList, user);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //messageList = HTTP REQUEST FOR JSON STRING

        //String room = CHAT_URL + "hash";
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

    private void createRecyclerView(View v){
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) v.findViewById(R.id.dispChatRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

//    private void setupLoginButton(final FirebaseAuth auth){
//        LoginButton loginButton = v.findViewById(R.id.login_button);
//        loginButton.setReadPermissions("email", "public_profile");
//
//        loginButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View V) {
//
//                signOut(auth);
//            }
//
//        });
//    }

    private void setupSendMessageButton(final Socket socket, final FirebaseUser user){

        //LinearLayout msgLayout = (LinearLayout) getView().findViewById(R.id.chatInputLayout);
        Button sendMsg = (Button) getView().findViewById(R.id.sendMsgButton);
        final TextInputEditText chatMsgInput = getView().findViewById(R.id.chatMsgInput);

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
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                chatMsgInput.setText("");


            }
        });
    }

//    public void signOut(final FirebaseAuth auth) {
//        auth.signOut();
//
//        LoginManager.getInstance().logOut();
//        Intent indexIntent = new Intent(ChatroomFragment.this, IndexActivity.class);
//        startActivity(indexIntent);
//        finish();
//    }

    public void fillWithNonsenseText(FirebaseUser user) {
        messageList.add(new Message(nextMessageID++, "I AM THE USER", user.getUid()));
        messageList.add(new Message(nextMessageID++, "I AM ANOTHER STUDENT", "Example Student"));
    }
}