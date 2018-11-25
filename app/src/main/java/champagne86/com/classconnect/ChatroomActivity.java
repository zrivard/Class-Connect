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

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
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
import static com.facebook.FacebookSdk.getCacheDir;


public class ChatroomActivity extends Fragment {

    private FragmentActivity chatFrgmt;
    private static final String TAG = ChatroomActivity.class.getName();
    private static final String BASE_APP_URL = "https://classconnect-220321.appspot.com/";
    private static final String CHANGE_ROOM_SUFFIX = "change-room?class=";
    private static final String NEW_MSG_EVENT = "chat message";
    private static final String CHANGE_ROOM_EVENT = "change room";

    private int nextMessageID = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List messageList = new ArrayList();

    //HTTP request queue
    RequestQueue mRequestQueue;
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
    Network network = new BasicNetwork(new HurlStack());




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

            JSONObject data;
            String senderId;
            String message;
            String senderDisplayName;
            int thisId;

            try {
                if(args[0].getClass().equals(JSONObject.class)){
                    data = (JSONObject) args[0];
                } else{
                    data = new JSONObject((String)args[0]);
                }

                senderDisplayName = data.getString("display_name");
                senderId = data.getString("uuid");
                message = data.getString("message");
                thisId = data.getInt("id");
            } catch (JSONException e) {
                return;
            }

                    // add the message to view
            messageList.add(new Message(thisId, message, senderId, senderDisplayName));

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

        //Setup the queue for any HTTP requests
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();


       // setupLoginButton(auth);

        mAdapter = new MessageAdapter(chatFrgmt, messageList, user);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //messageList = HTTP REQUEST FOR JSON OBJECT

        //String room = BASE_APP_URL + "hash";
        try {
            mSocket = IO.socket(BASE_APP_URL);
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
                    args.put("uuid", user.getUid());
                    args.put("display_name", "TEMP_DISPLAY_NAME");

                    //This is TEMPORARY
                    args.put("class_hash", "CPEN_311");
                }
                catch (JSONException e) { }


                socket.emit(NEW_MSG_EVENT, args);

                //ALEX - This is the caling convention to change chat rooms
                //Comment out the above emit() call and uncomment the funciton call
                //to see it in action. (It will print all the messages into the log for you)

                //changeChatRoom(socket, "CPEN_311");

                try  {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                chatMsgInput.setText("");


            }
        });
    }


    /**
     * @brief - Changes the current chat room that the user is in.
     *              This is done by first getting all of the chat
     *              messages from the database in the new room, and
     *              then switching the socket room over
     *
     * @param socket - The socket we wish to switch over
     * @param newRoom - The new room that we wish to enter
     */
    private void changeChatRoom(final Socket socket, final String newRoom){

        //Create the url that will change the chat rooms
        String url = BASE_APP_URL + CHANGE_ROOM_SUFFIX + newRoom;

        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //ALEX - This is for you
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //Now that we have the messages, update our socket
                        socket.emit(CHANGE_ROOM_EVENT, newRoom);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Maybe do something?
                    }
                });

        //Add to the queue of requests to be sent
        mRequestQueue.add(jsonObjectRequest);
    }


//    public void signOut(final FirebaseAuth auth) {
//        auth.signOut();
//
//        LoginManager.getInstance().logOut();
//        Intent indexIntent = new Intent(ChatroomActivity.this, IndexActivity.class);
//        startActivity(indexIntent);
//        finish();
//    }

    public void fillWithNonsenseText(FirebaseUser user) {
        messageList.add(new Message(nextMessageID++, "I AM THE USER", user.getUid(), "User's Name"));
        messageList.add(new Message(nextMessageID++, "I AM ANOTHER STUDENT", "sdvbzdkjvzsljfvbFAKED", "Other Name"));
    }
}