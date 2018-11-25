package champagne86.com.classconnect;

public class Message {

    protected int id;
    protected String message;
    protected String senderID;
    protected  String senderDisplayName;

    public Message(int id, String message, String senderID, String senderDisplayName) {
        this.id = id;
        this.message = message;
        this.senderID = senderID;
        this.senderDisplayName = senderDisplayName;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}


