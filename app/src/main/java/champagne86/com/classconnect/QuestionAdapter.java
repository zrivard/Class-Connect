package champagne86.com.classconnect;

import android.content.Context;
//import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.google.api.Distribution;
import com.google.common.io.LineReader;



import com.google.api.Distribution;
import com.google.common.io.LineReader;





import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter {

    private List<Question> questionList;
    private FirebaseUser mUser;
    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    private Socket mSocket;


    public QuestionAdapter(Context context, List questions, FirebaseUser user, Socket socket) {
        questionList = questions;
        mUser = user;
        mSocket = socket;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView question;
        public TextView author;
        public LinearLayout questionItem;

        public ViewHolder(LinearLayout v) {
            super(v);
            title = (TextView) v.findViewById(R.id.questionTitle);
            question = (TextView) v.findViewById(R.id.questionBody);
            author = (TextView) v.findViewById(R.id.questionAuthor);

            questionItem = (LinearLayout) v.findViewById(R.id.questionLayout);


        }
    }


    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

//        if (i == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_has_replies, viewGroup, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
            return vh;
//        }
//        else {
//            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_no_replies, viewGroup, false);
//            ViewHolder vh = new ViewHolder((LinearLayout) v);
//            return vh;
//        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        QuestionAdapter.ViewHolder question;
        question =(QuestionAdapter.ViewHolder) viewHolder;
        final Question thisQuestion = questionList.get(i);
        final String classroom = thisQuestion.getClassroom();
        question.title.setText(thisQuestion.getTitle());
        question.question.setText(thisQuestion.getBody());
        question.author.setText(thisQuestion.getSenderName());



        question.questionItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*remove(position);*/

                Bundle bundle = new Bundle();
                bundle.putString("question", thisQuestion.getId());
                bundle.putString("class", classroom);



                Log.d("Selected class:", thisQuestion.getId());

                Fragment fragment = null;
                Class fragmentClass = ChatroomFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fragment.setArguments(bundle);

                // Insert the fragment by replacing any existing fragment
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("TAG").commit();
                return;
            }
        });



    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Question question = questionList.get(position);


        return ACTIVE;

        //CHANGE USER TO USER ID WHEN NECESSARY
//        if (classroom.isActive()){
//            return ACTIVE;
//        } else {
//            return INACTIVE;
//        }

    }
}
