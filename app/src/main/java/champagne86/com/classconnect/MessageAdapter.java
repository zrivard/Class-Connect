package champagne86.com.classconnect;


import android.app.Activity;
import android.content.Context;
//import android.support.annotation.NonNull;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.String;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import android.os.Bundle;


import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class MessageAdapter extends RecyclerView.Adapter {

    private List<Message> messageList;


    private FirebaseUser mUser;

    private Context context;

    private Socket mSocket;
    MessageAdapter msgA;
    FragmentActivity frag;


    public static final int SENDER = 0;
    public static final int RECEIVER = 1;
    int hasVoted = 0;

    public MessageAdapter(Context context, List messages, FirebaseUser user, Socket socket, FragmentActivity frag) {
        messageList = messages;
        mUser = user;
        mSocket = socket;
        this.frag = frag;

        //votedMap = new HashMap<>();
        //msgVoteStatus =
        msgA = this;

        this.context = context;
    }





    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView usrIdTextView;
        public TextView msgUpvotes;
        public final ImageView upVoteIcon;

        public ViewHolder(LinearLayout v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.msgText);
            usrIdTextView = (TextView) v.findViewById(R.id.usrID);
            msgUpvotes = (TextView) v.findViewById(R.id.msgVotes);
            upVoteIcon = (ImageView) v.findViewById(R.id.upvoteIcon);


        }

    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_white, parent, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_blue, parent, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
            return vh;
        }
    }

    public void remove(int pos) {
        int position = pos;
        messageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messageList.size());

    }




    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        Emitter.Listener onNewUpvote = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data;
                String senderId = "";
                // String messageId;

                String thisId = "";

                try {
                    if (args[0].getClass().equals(JSONObject.class)) {
                        data = (JSONObject) args[0];
                    } else {
                        data = new JSONObject((String) args[0]);
                    }
                    senderId = data.getString("userId");
                    thisId = data.getString("msgId");
                } catch (JSONException e) {
                    Log.i("ERROR", "error");
                }

                Message thisMessage;

                for (int i = 0; i < messageList.size(); i++) {
                    Message msgIter = messageList.get(i);

                    if (msgIter.getId().equals(thisId)) {
                        thisMessage = msgIter;

                        if (!thisMessage.getUsersVotedList().contains(senderId)) {
                            thisMessage.upVoteMsg(senderId);
                        } else if (thisMessage.getUsersVotedList().contains(senderId)) {
                            thisMessage.downVoteMsg(senderId);
                        }


                        break;
                    }
                }

                frag.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });


            }
        };

        mSocket.on(context.getString(R.string.new_upvote_event), onNewUpvote);
        mSocket.on(context.getString(R.string.new_downvote_event), onNewUpvote);

        final Message thisMessage = messageList.get(position);
        ViewHolder msg;
        msg = (ViewHolder) holder;

        msg.mTextView.setText(thisMessage.getMessage());
        msg.usrIdTextView.setText(thisMessage.getSenderDisplayName());
        msg.msgUpvotes.setText(thisMessage.getUpvotes());

        msg.upVoteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                clickToVote(thisMessage);
            }
        });



    }


    public void clickToVote(Message message) {

        String user = mUser.getUid();
        JSONObject args = new JSONObject();
        String msgId = message.getId();
        List<String> usrVotedList = message.getUsersVotedList();



        try {
            args.put("msgId", msgId);
            args.put("userId", user);

        } catch (JSONException e) {
        }
        if (!usrVotedList.contains(user)) {
            mSocket.emit(context.getString(R.string.new_upvote_event), args);
        }
        else if (usrVotedList.contains(user)) {
            mSocket.emit(context.getString(R.string.new_downvote_event), args);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);


        //CHANGE USER TO USER ID WHEN NECESSARY
        if (message.getSenderID().equals(mUser.getUid())) {
            return SENDER;
        } else {
            return RECEIVER;
        }

    }

}
