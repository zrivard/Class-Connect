package champagne86.com.classconnect;

import java.util.ArrayList;
import java.util.List;

public class Message {

    protected String id;
    protected String message;
    protected String senderID;
    protected  String senderDisplayName;
    protected int rating;
    protected List<String> usersVoted;

    public Message(String id, String message, String senderID, String senderDisplayName) {
        this.id = id;
        this.message = message;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
        this.usersVoted = new ArrayList<>();

    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderName) {
        this.senderID = senderName;
    }

    public String getSenderDisplayName() { return senderDisplayName; }

    public void setSenderDisplayName(String senderDisplayName) { this.senderDisplayName = senderDisplayName; }

    public String getMessage() {


            return message;
    }

    public String getUpvotes(){
        return Integer.toString(rating);
    }
    public void upVoteMsg(String user){
        usersVoted.add(user);
        this.rating ++;
    }
    public void downVoteMsg(String user){
        usersVoted.remove(user);
        this.rating--;
    }

    public int getRating(){
        return this.rating;
    }
    public List<String> getUsersVotedList(){
        return usersVoted;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}


