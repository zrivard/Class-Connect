package champagne86.com.classconnect;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ClasslistAdapter extends RecyclerView.Adapter {

    private static List<Classroom> classList;
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
                    Classroom classroom = classList.get(getAdapterPosition());

                    if(!classroom.isActive()){
                        Toast.makeText(v.getContext(), R.string.class_is_inactive, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("class", classroom.getName());

                    Log.d("Selected class:", classroom.getName());

                    Fragment fragment = null;
                    Class fragmentClass = QuestionFragment.class;
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
