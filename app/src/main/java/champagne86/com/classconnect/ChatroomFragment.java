package champagne86.com.classconnect;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;


import com.github.nkzawa.emitter.Emitter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    private int nextMessageID = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    public static List messageList = new ArrayList();
    public static HashSet<String> messageIDs = new HashSet<String>();
    private static String questionName;
    private String classroom;

    public static boolean gotMessages = false;

    private ApiCaller mApiCaller;
    private static Socket mSocket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        if(bundle != null) {
            questionName = bundle.getString("question");
            classroom = bundle.getString("class");
        }

        return inflater.inflate(R.layout.activity_chatroom, container, false);
    }

   //View v = getView();


    public void onViewCreated(View view, Bundle savedInstanceState) {

        createRecyclerView(view);

    }

    private void updateChatUI(){
        chatFrgmt.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView = (RecyclerView) chatFrgmt.findViewById(R.id.dispChatRecyclerView);
                mAdapter.notifyDataSetChanged();
                if(mAdapter.getItemCount() > 0)
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }




    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data;
            String senderId = "";
            String message = "";

            String senderDisplayName = "";
            String thisId = "";


            String questionID= "";

            try {
                if(args[0].getClass().equals(JSONObject.class)){
                    data = (JSONObject) args[0];
                } else{
                    data = new JSONObject((String)args[0]);
                }

                Log.d(TAG, data.toString(4));

                senderDisplayName = data.getString("display_name");
                senderId = data.getString("user_id");
                message = data.getString("message");

                thisId = data.getString("message_id");
                questionID = data.getString("question_id");
            } catch (JSONException e) {
                Log.i("Error", "ERROR");
            }


            // add the message to view (sanity check that the message was for this room
            if(questionName.equals(questionID) /*&& !messageIDs.contains(questionID)*/) {
                messageIDs.add(questionID);
                messageList.add(new Message(thisId, message, senderId, senderDisplayName));
                updateChatUI();
            }else{
                Log.d(TAG, questionID);
                Log.d(TAG, "Got a message destined for another chat room!");
            }



        }

    };



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatFrgmt = getActivity();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try{
            mSocket = IO.socket(getString(R.string.app_url));
            mSocket.on(getString(R.string.new_msg_event), onNewMessage);
            mSocket.connect();

        }catch (URISyntaxException e){
            Log.e(TAG, e.getMessage());
        }

        final Socket socket = mSocket;

        mApiCaller = new ApiCaller(this.getContext());
        mApiCaller.changeChatRoom(mSocket, questionName, new ServerCallback() {
            @Override
            public void onSuccess() {

                mAdapter = new MessageAdapter(chatFrgmt, messageList, user, socket, chatFrgmt);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());


                setupSendMessageButton(socket, user);
                updateChatUI();
            }
        });

       // setupLoginButton(auth);





        //messageList = HTTP REQUEST FOR JSON OBJECT

        //String room = BASE_APP_URL + "hash";



    }

    private void createRecyclerView(View v){
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) v.findViewById(R.id.dispChatRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }



    private void setupSendMessageButton(final Socket socket, final FirebaseUser user){

        //LinearLayout msgLayout = (LinearLayout) getView().findViewById(R.id.chatInputLayout);
        Button sendMsg = (Button) getView().findViewById(R.id.sendMsgButton);
        final TextInputEditText chatMsgInput = getView().findViewById(R.id.chatMsgInput);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){


                JSONObject args = new JSONObject();

                Switch anonUser = (Switch) getView().findViewById(R.id.anonUserSwitch);
                String display_name = anonUser.isChecked() ? "Anonymous User" : user.getDisplayName();

                try {
                    args.put("id", nextMessageID++);
                    args.put("display_name", display_name);
                    args.put("message", chatMsgInput.getText().toString());
                    args.put("uuid", user.getUid());


                    //These params will have to be updated
                    args.put("classroom", classroom);
                    args.put("question_id", questionName);
                }
                catch (JSONException e) { }


                socket.emit(getString(R.string.new_msg_event), args);

                //ALEX - This is the caling convention to change chat rooms
                //Comment out the above emit() call and uncomment the function call
                //to see it in action. (It will print all the messages into the log for you)


                //mApiCaller.askQuestion("Question Title", "Question body", "CPEN_311", user.getUid());
                //mApiCaller.getClassQuestions("CPEN_311");
                //mApiCaller.getClassroomInfo("CPEN_311");
                //mApiCaller.getUserClasses(user.getUid());
                /*HashMap<String, Boolean> enrolledClasses = new HashMap<>();
                enrolledClasses.put("CPEN_311", false);
                enrolledClasses.put("CPEN_321", false);
                enrolledClasses.put("CPEN_331", true);
                mApiCaller.setUserClasses(user.getUid(), enrolledClasses);*/
                //mApiCaller.closeQuestion("dE1DrP33GtTw4k98BuaP");



                try  {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                chatMsgInput.setText("");


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        mSocket.off(getString(R.string.new_msg_event));
        mSocket.disconnect();
        mSocket.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSocket.off(getString(R.string.new_msg_event));
        mSocket.disconnect();
        mSocket.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.off(getString(R.string.new_msg_event));
        mSocket.disconnect();
        mSocket.close();


    }

    @Override
    public void onDetach() {
        super.onDetach();

        mSocket.off(getString(R.string.new_msg_event));
        mSocket.disconnect();
        mSocket.close();
    }

    //    public void signOut(final FirebaseAuth auth) {
//        auth.signOut();
//
//        LoginManager.getInstance().logOut();
//        Intent indexIntent = new Intent(ChatroomFragment.this, IndexActivity.class);
//        startActivity(indexIntent);
//        finish();
//    }

}