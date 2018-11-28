package champagne86.com.classconnect;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FragmentActivity homeFrgmt;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    public  List<Classroom> classList = new ArrayList<Classroom>();
    public User user = new User();
    private String uid;
    private String userName;


    private static final String TAG = "AccessUser";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeFrgmt = getActivity();
        mAdapter = new ClasslistAdapter(homeFrgmt, classList, mUser);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getClasses();

    }

    private void updateClassList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView = (RecyclerView) getView().findViewById(R.id.questionsRecyclerView);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        });

    }


    private void getUsername() {

        uid = mUser.getUid();

        DocumentReference docRef = mDb.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> userDoc = document.getData();
                        String userNm = (String) userDoc.get("name");
                        user.setUserName(userNm);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void getClasses() {
        uid = mUser.getUid();

        DocumentReference docRef = mDb.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "DocumentSnapshot data: " + document.getData());

                        Map<String, Object> userDoc = document.getData();
                        HashMap<String, Boolean> strClassMap = (HashMap<String, Boolean>) userDoc.get("enrolledClasses");
                        for (String key : strClassMap.keySet()) {
                            Classroom newclass = new Classroom(key);
                            if(strClassMap.get(key)) {
                                classList.add(newclass);
                                getClassTimes(key);
                            }

                            Log.i(TAG, "___________________REACHED________________");
                            Log.i(TAG, "LIST: " + classList);

                        }

                    } else {
                        Log.i(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void getClassTimes(final String classname){


        DocumentReference docRef = mDb.collection("classes").document(classname);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "DocumentSnapshot data: " + document.getData());

                        Map<String, Object> classDoc = document.getData();
                        HashMap<String, ArrayList<String>> timeMap = (HashMap<String, ArrayList<String>>) classDoc.get("dayTimeActive");

                        Classroom classroom = null;
                        for(Classroom c : classList){
                            if(c.getName().equals(classname)){
                                classroom = c;
                                break;
                            }
                        }

                        if(classroom == null){
                            return;
                        }

                        for (String day : timeMap.keySet()) {
                            ArrayList<String> times = timeMap.get(day);
                            for(int i = 0; i < times.size(); i += 2){
                                classroom.setActiveDay(day, times.get(i), times.get(i+1));
                            }

                        }

                        updateClassList();

                    } else {
                        Log.i(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void createRecyclerView(View v) {
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) v.findViewById(R.id.questionsRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        createRecyclerView(view);

    }

}
