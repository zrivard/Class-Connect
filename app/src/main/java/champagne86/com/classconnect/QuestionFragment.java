package champagne86.com.classconnect;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionFragment extends Fragment {

    private FragmentActivity questionFrgmt;

    private static final String TAG = QuestionFragment.class.getName();

    private int nextMessageID = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List questionList = new ArrayList();
    private String clasroomName;


    private ApiCaller mApiCaller;


    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = getArguments();
        clasroomName =  bundle.getString("class");
        return inflater.inflate(R.layout.fragment_question, container, false);

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        createRecyclerView(view);

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data;
            String senderId = "";
            String title = "";
            String body = "";
            String classroom = "";
            String senderDisplayName = "";
            String questionId = "";

            try {
                if(args[0].getClass().equals(JSONObject.class)){
                    data = (JSONObject) args[0];
                } else{
                    data = new JSONObject((String)args[0]);
                }

                Log.d(TAG, data.toString(4));

                senderDisplayName = data.getString("display_name");
                senderId = data.getString("user_id");
                classroom = data.getString("classroom");
                title = data.getString("title");
                body = data.getString("body");

                questionId = data.getString("question_id");

            } catch (JSONException e) {
                Log.i("Error", "ERROR");
            }

            // add the message to view
            Switch anonUser = (Switch) getView().findViewById(R.id.anonQuestionSwitch);

            if (!anonUser.isChecked()) {
                questionList.add(new Question(questionId, title, body, classroom, senderId, senderDisplayName));
            }
            else {
                questionList.add(new Question(questionId, title, body, classroom, senderId, "Anonymous"));
            }




            // add the message to view (sanity check that the message was for this room
            //String currentQuestion = "SOME_QUESTION";

                questionList.add(new Question(questionId, title, body, classroom, senderId, senderDisplayName));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView = (RecyclerView) getView().findViewById(R.id.questionsRecyclerView);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    }
                });




        }

    };



    private void createRecyclerView(View v){
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) v.findViewById(R.id.questionsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        questionFrgmt = getActivity();

        Socket mSocket;
        FirebaseAuth auth;
        FirebaseUser user;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mApiCaller = new ApiCaller(this.getContext());

        // setupLoginButton(auth);


        //messageList = HTTP REQUEST FOR JSON OBJECT

        //String room = BASE_APP_URL + "hash";
        try {
            mSocket = IO.socket(getString(R.string.app_url));
            mSocket.on(getString(R.string.new_question_event), onNewMessage);
            mSocket.connect();
            mAdapter = new QuestionAdapter(questionFrgmt, questionList, user, mSocket);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            setupSendQuestionButton(mSocket, user);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error!");
            Log.e(TAG, "\tError: " + e.getMessage());
        }
    }

    public void setupSendQuestionButton(final Socket socket, final FirebaseUser user) {
        Button sendQuestion = (Button) questionFrgmt.findViewById(R.id.postQuestionButton);
        final AutoCompleteTextView titleText = (AutoCompleteTextView) questionFrgmt.findViewById(R.id.questionTitleInput);
        final MultiAutoCompleteTextView bodyText = (MultiAutoCompleteTextView) questionFrgmt.findViewById(R.id.questionBodyInput);

        final Switch anon = (Switch) questionFrgmt.findViewById(R.id.anonQuestionSwitch);


        sendQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

                JSONObject args = new JSONObject();


                //boolean anon_login = anon.isChecked(); //This can be set somewhere?

                String display_name = anon.isChecked() ? "Anon" : user.getDisplayName();

                try {
                    //args.put("id", nextMessageID++);
                    args.put("display_name", display_name);
                    args.put("title", titleText.getText().toString());
                    args.put("body", bodyText.getText().toString());
                    args.put("user_id", user.getUid());


                    //These params will have to be updated

                    args.put("classroom", clasroomName);
                    args.put("question_id", "SOME_QUESTION");
                }
                catch (JSONException e) { }


                socket.emit(getString(R.string.new_question_event), args);

            }
        });


    }

}
