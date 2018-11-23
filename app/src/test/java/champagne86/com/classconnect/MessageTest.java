package champagne86.com.classconnect;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void getSenderName() {
        Message testMsg1 = new Message(0, "abc", "sender");
        assertTrue(testMsg1.getSenderName().equals("sender"));
    }

    @Test
    public void setSenderName() {
        Message testMsg1 = new Message(0, "abc", "sender");
        testMsg1.setSenderName("newSender");
        assertTrue(testMsg1.getSenderName().equals("newSender"));
    }

    @Test
    public void getMessage() {
        Message testMsg1 = new Message(0, "abc", "sender");
        assertTrue(testMsg1.getMessage().equals("abc"));
    }

    @Test
    public void setMessage() {
        Message testMsg1 = new Message(0, "abc", "sender");
        testMsg1.setMessage("newMessage");
        assertTrue(testMsg1.getMessage().equals("newMessage"));
    }

    @Test
    public void getId() {
        Message testMsg1 = new Message(0, "abc", "sender");
        assertTrue(testMsg1.getId() == 0);
    }

    @Test
    public void setId() {
        Message testMsg1 = new Message(0, "abc", "sender");
        testMsg1.setId(2);
        assertTrue(testMsg1.getId() == 2);
    }
}