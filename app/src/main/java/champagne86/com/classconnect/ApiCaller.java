package champagne86.com.classconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.grpc.Metadata;

import static com.facebook.FacebookSdk.getCacheDir;

public class ApiCaller {

    private static final String TAG = ApiCaller.class.getName();


    //HTTP request queue - There should only be one of these
    static RequestQueue mRequestQueue;
    static Cache cache;
    static Network network = new BasicNetwork(new HurlStack());

    private Context mContext;

    /*
     * ATTENTION - I suggest running each of the functions here at least once
     * so that you can see the format of the JSONObject responses as they will
     * be printed into the log (preformatted).
     */

    /*
     * Things needed:
     * - Get a list of classes for any given user:      getUserClasses
     * - Get all the fields for a given classroom:      getClassroomInfo
     * - Get all the questions for a given classroom:   getClassQuestions
     * - Add a new classroom to the user's class list:  setUserClasses
     * - Add a question to a class:                     askQuestion
     */


    public ApiCaller(Context context){

        if(mRequestQueue == null) {
            cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
            //Setup the queue for any HTTP requests
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }else{
            //We already have a queue. Do not add another
        }

        mContext = context;
    }

    /**
     * This should be called after the login to ensure that the user
     * object exists in the database. If it does not exist, it will
     * be created. If it exists, it will not be changed
     *
     * @param uuid The current users uuid
     */
    public void addUserToDB(String uuid, String name, final Intent chatIntent){
        //Create the url that will change the chat rooms
        String url =  mContext.getString(R.string.app_url)
                + mContext.getString(R.string.add_user_uuid_suffix)
                + uuid
                + mContext.getString(R.string.add_user_name_suffix)
                + name.replace(' ', '_');

        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //Will return the user object
                        mContext.startActivity(chatIntent);
                        //Call some UI updating function here based on `response`?


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

    /**
     * Changes the current chat room that the user is in.
     *   This is done by first getting all of the chat
     *   messages from the database in the new room, and
     *   then switching the socket room over
     *
     * @param socket The socket we wish to switch over
     * @param newRoom The new room that we wish to enter
     */
    public void changeChatRoom(final Socket socket, final String newRoom){

        //Create the url that will change the chat rooms
        String url =  mContext.getString(R.string.app_url)
                    + mContext.getString(R.string.get_question_messages_suffix)
                    + newRoom;

        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing all the messages of the
                        //room that you requested to enter

                        //Call some UI updating function here based on `response`?

                        //Now that we have the messages, update our socket
                        socket.emit(mContext.getString(R.string.change_room_event), newRoom);
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


    /*
     * Sends a question to the database
     *
     * @param questionText The actual question that should be displayed to the users
     * @param classroom The classroom in which the question should appear
     * @param user The user that is asking the question
     */
    public void askQuestion(String questionTitle, String questionBody, String classroom, String userID){

        //Create the url that will ask the question
        String url =  mContext.getString(R.string.app_url)
                    + mContext.getString(R.string.ask_question_suffix);

        // POST parameters for the request
        JSONObject params = new JSONObject();
        try{
            params.put("user_id", userID);
            params.put("body", questionBody);
            params.put("title", questionTitle);
            params.put("classroom", classroom);
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }


        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing the clsss the question was
                        //asked in, along with the full question object

                        //Call some UI updating function here based on `response`?

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


    /**
     * Retrieves all questions belonging to a classroom
     *
     * @param classRoom String name of the class
     */
    public void getClassQuestions(String classRoom, RecyclerView.Adapter adapter, List questionList){

        //Create the url that will change the chat rooms
        String url =  mContext.getString(R.string.app_url)
                    + mContext.getString(R.string.get_questions_suffix)
                    + classRoom;


        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing class requested and
                        //all of th questions in the class




                        //Call some UI updating function here based on `response`?
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


    /*
     * Get all the information associated with a given classroom
     *
     * @param classroom The name of the classroom
     */
    public void getClassroomInfo(String classroom){

        //Create the url that will get all the info
        String url =  mContext.getString(R.string.app_url)
                + mContext.getString(R.string.get_class_info_suffix)
                + classroom;


        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing the following
                        //-Name
                        //-Active times
                        //-Registered members
                        //-Asked questions

                        //Call some UI updating function here based on `response`?
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


    /**
     * Gets all the classes that the user is registered in.
     *
     * @param userID Current user's uuid
     */
    public void getUserClasses(String userID){
        //Create the url that will get all the info
        String url =  mContext.getString(R.string.app_url)
                + mContext.getString(R.string.get_user_classes_suffix)
                + userID;


        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing the following
                        //-Name
                        //-enrolledClasses

                        //Call some UI updating function here based on `response`?
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


    /**
     * Sets the classes that the user is registered for
     *
     * @param userID The current user's uuid
     * @param classes Hashmap detailing the names of the classes
     *                and their status in them
     */
    public void setUserClasses(String userID, HashMap<String, Boolean> classes){
        //Create the url that will get all the info
        String url =  mContext.getString(R.string.app_url)
                + mContext.getString(R.string.set_user_classes_suffix);


        // POST parameters for the request
        JSONObject params = new JSONObject();
        JSONObject enrolledClasses = new JSONObject();
        try{

            for(String key: classes.keySet()){
                enrolledClasses.put(key, classes.get(key));
            }

            params.put("user_id", userID);
            params.put("enrolledClasses", enrolledClasses);
            Log.d(TAG, params.toString(4));
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
        }


        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing the same
                        //data that you sent as params

                        //Call some UI updating function here based on `response`?
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


    /**
     * Closes a question to all new messages and triggers the
     * building of the RTF file for download if not already done
     *
     * @param questionID Question ID of the question to close
     */
    public void closeQuestion(String questionID){
        //Create the url that will get all the info
        String url =  mContext.getString(R.string.app_url)
                + mContext.getString(R.string.close_question_suffix)
                + questionID;


        //Create the request and what should happen on return
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        }catch(JSONException e){
                            Log.e(TAG, e.getMessage());
                        }

                        //ALEX - This is for you
                        //`response` is a JSONObject containing the
                        //question object

                        //Call some UI updating function here based on `response`?
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
}
