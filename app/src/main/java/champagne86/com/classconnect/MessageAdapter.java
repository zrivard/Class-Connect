package champagne86.com.classconnect;


import android.content.Context;
//import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;



public class MessageAdapter extends RecyclerView.Adapter {

    private List<Message> messageList;
    private FirebaseUser mUser;

    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    public MessageAdapter(Context context, List messages, FirebaseUser user) {
        messageList = messages;
        mUser = user;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView usrIdTextView;

        public ViewHolder(LinearLayout v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.msgText);
            usrIdTextView = (TextView) v.findViewById(R.id.usrID);
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


    public void onBindViewHolder(RecyclerView.ViewHolder  holder, final int position) {
        MessageAdapter.ViewHolder msg;
        msg =(MessageAdapter.ViewHolder) holder;
        msg.mTextView.setText(messageList.get(position).getMessage());
        msg.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*remove(position);*/
                return;
            }
        });
        msg.usrIdTextView.setText(messageList.get(position).getSenderDisplayName());
        msg.usrIdTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                remove(position);
            }
        });
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

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//        ((MessageAdapter.ViewHolder) viewHolder).bindData(messageList.get(i));
//
//    }
}
