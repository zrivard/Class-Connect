package champagne86.com.classconnect;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void getSenderDisplayName() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        assertTrue(testMsg1.getSenderDisplayName().equals("sendDisplayName"));
    }

    @Test
    public void setSenderDisplayName() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        testMsg1.setSenderDisplayName("newSender");
        assertTrue(testMsg1.getSenderDisplayName().equals("newSender"));
    }

    @Test
    public void votingTest(){
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        testMsg1.upVoteMsg("thisUser");
        assertTrue(testMsg1.getUpvotes().equals(Integer.toString(1)));
        testMsg1.downVoteMsg("anotherUser");
        assertTrue(testMsg1.getUpvotes().equals(Integer.toString(0)));
        assertTrue(testMsg1.getRating() == 0);
        List<String> retList = testMsg1.getUsersVotedList();
        assertTrue(retList.contains("thisUser") && retList.contains("anotherUser"));
    }
    @Test
    public void getSenderID() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        assertTrue(testMsg1.getSenderID().equals("sender"));
    }

    @Test
    public void setSenderID() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        testMsg1.setSenderID("newSender");
        assertTrue(testMsg1.getSenderID().equals("newSender"));
    }

    @Test
    public void getMessage() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        assertTrue(testMsg1.getMessage().equals("abc"));
    }

    @Test
    public void setMessage() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        testMsg1.setMessage("newMessage");
        assertTrue(testMsg1.getMessage().equals("newMessage"));
    }

    @Test
    public void getId() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        assertTrue(testMsg1.getId() == "0");
    }

    @Test
    public void setId() {
        Message testMsg1 = new Message("0", "abc", "sender", "sendDisplayName");
        testMsg1.setId("2");
        assertTrue(testMsg1.getId() == "2");
    }
}