package champagne86.com.classconnect;

import android.content.Context;
//import android.support.annotation.NonNull;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ClasslistAdapter extends RecyclerView.Adapter {

    private List<Classroom> classList;
    private FirebaseUser mUser;

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;


    public ClasslistAdapter(Context context, List classes, FirebaseUser user) {
        classList = classes;
        mUser = user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView statusTextView;

        public ViewHolder(LinearLayout v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.className);
            mTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    /*remove(position);*/
                    return;
                }
            });
            statusTextView = (TextView) v.findViewById(R.id.classStatus);
        }
    }


    @NonNull
    @Override
    public ClasslistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.class_active, viewGroup, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
            return vh;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.class_inactive, viewGroup, false);
            ViewHolder vh = new ViewHolder((LinearLayout) v);
            return vh;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ClasslistAdapter.ViewHolder classroom;
        classroom =(ClasslistAdapter.ViewHolder) viewHolder;
        classroom.mTextView.setText(classList.get(i).getName());
        if (classList.get(i).isActive()){
            classroom.statusTextView.setText("Class Active");
        }
        else {
            classroom.statusTextView.setText("Class Inactive");
        }

    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Classroom classroom = classList.get(position);


        //CHANGE USER TO USER ID WHEN NECESSARY
        if (classroom.isActive()){
            return ACTIVE;
        } else {
            return INACTIVE;
        }

    }
}
