package champagne86.com.classconnect;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;


public class ChatroomActivity extends AppCompatActivity {

    private int id = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List messageList = new ArrayList();
    private Socket mSocket;

    private static final String CHAT_URL = "https://classconnect-220321.appspot.com/";

    private static final String NEW_MSG_EVENT = "chat message";


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

                JSONObject data = (JSONObject) args[0];
                    String senderName;
                    String message;
                    int thisId;
                    try {

                        senderName = data.getString("senderName");
                        message = data.getString("message");
                        thisId = data.getInt("id");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
            messageList.add(new Message(thisId, message, senderName ));
           // ((BaseAdapter) ((RecyclerView)findViewById(R.id.dispChatRecyclerView)).getAdapter()).notifyDataSetChanged();
           // ((RecyclerView)findViewById(R.id.dispChatRecyclerView)).getAdapter().notifyDataSetChanged();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView = (RecyclerView) findViewById(R.id.dispChatRecyclerView);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
            });

//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {

//                }
//            });
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        mRecyclerView = (RecyclerView) findViewById(R.id.dispChatRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MessageAdapter(getBaseContext(), messageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Button sendMsg = (Button) findViewById(R.id.sendMsgButton);
        final TextInputEditText chatMsgInput = findViewById(R.id.chatMsgInput);

        try {
            mSocket = IO.socket(CHAT_URL);
        } catch (URISyntaxException e) {}

        mSocket.on(NEW_MSG_EVENT, onNewMessage);
        mSocket.connect();



        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V){

                //messageList.add(new Message(id++, chatMsgInput.getText().toString(), "USER" ));

                JSONObject args = new JSONObject();

                try {
                    args.put("id", id++);
                    args.put("message", chatMsgInput.getText().toString());
                    args.put("senderName", "USER");
                }
                catch (JSONException e) { }


                mSocket.emit(NEW_MSG_EVENT, args);
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }

                chatMsgInput.setText("");


            }
        });


        fillWithNonsenseText();
    }

    public void fillWithNonsenseText() {
        messageList.add(new Message(id++, "I AM THE USER", "USER"));
        messageList.add(new Message(id++, "I AM ANOTHER STUDENT", "STUDENT"));
    }
}