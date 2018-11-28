package champagne86.com.classconnect;

import org.junit.Test;

import static org.junit.Assert.*;

public class QuestionTest {

    @Test
    public void getId() {
        Question testQ = new Question
                ("id", "title", "body", "classroom", "senderId", "senderDisplayName");
        assertTrue(testQ.getId().equals("id"));
    }

    @Test
    public void getClassroom() {
        Question testQ = new Question
                ("id", "title", "body", "classroom", "senderId", "senderDisplayName");
        assertTrue(testQ.getClassroom().equals("classroom"));
    }

    @Test
    public void getTitle() {
        Question blankQ = new Question();
        Question testQ = new Question
                ("id", "title", "body", "classroom", "senderId", "senderDisplayName");
        assertTrue(testQ.getTitle().equals("title"));
    }

    @Test
    public void getBody() {
        Question testQ = new Question
                ("id", "title", "body", "classroom", "senderId", "senderDisplayName");
        assertTrue(testQ.getBody().equals("body"));
    }

    @Test
    public void getSenderId() {
        Question testQ = new Question
                ("id", "title", "body", "classroom", "senderId", "senderDisplayName");
        assertTrue(testQ.getSenderId().equals("senderId"));
    }

    @Test
    public void getSenderName() {
        Question testQ = new Question
                ("id", "title", "body", "classroom", "senderId", "senderDisplayName");
        assertTrue(testQ.getSenderName().equals("senderDisplayName"));
    }
}