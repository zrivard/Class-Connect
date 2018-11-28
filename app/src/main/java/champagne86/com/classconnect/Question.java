package champagne86.com.classconnect;

public class Question {

    private String questionId;
    private String questionTitle;
    private String questionBody;
    private String questionClass;
    private String questionSender;
    private String questionSenderName;


    public Question() {

    }

    public Question(String id, String title, String body, String classroom, String senderId, String senderDisplayName) {

        questionBody = body;
        questionClass = classroom;
        questionId = id;
        questionSender = senderId;
        questionTitle = title;
        questionSenderName = senderDisplayName;

    }

    public String getId() {
        return questionId;
    }

    public String getClassroom() {
        return questionClass;
    }

    public String getTitle() {
        return questionTitle;
    }

    public String getBody() {
        return questionBody;
    }
    public String getSenderId(){
        return questionSender;
    }
    public String getSenderName(){
        return questionSenderName;
    }
}
