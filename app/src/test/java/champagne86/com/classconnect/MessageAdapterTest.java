package champagne86.com.classconnect;


import org.junit.Before;
import org.junit.Test;
import com.google.firebase.auth.FirebaseUser;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.modules.junit4.PowerMockRunner;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

public class MessageAdapterTest {
    @Mock
    private List<Classroom> classList;
    private FirebaseUser mUser;
    private Context mContext;

    @Test
    public void onCreateViewHolder() {
        FragmentActivity mockFrag = mock(FragmentActivity.class);
        Socket mockSocket = mock(Socket.class);
        MessageAdapter sampleAdapter = new MessageAdapter
                ( mContext, classList, mUser, mockSocket, mockFrag );
    }

    @Test
    public void remove() {
    }

    @Test
    public void onBindViewHolder() {
    }

    @Test
    public void clickToVote() {
        String sName = "Name";
        String sameName = "user";
        String uName = "user";

        FirebaseUser mockFBU = mock(FirebaseUser.class);
        List<String> usrList = new ArrayList<String>();
        usrList.add(uName);
        FragmentActivity mockFrag = mock(FragmentActivity.class);
        Socket mockSocket = mock(Socket.class);
        when(mockFBU.getUid()).thenReturn(sName);
        Message mockMessage = mock(Message.class);
        when(mockMessage.getId()).thenReturn("2");
        when(mockMessage.getUsersVotedList()).thenReturn(usrList);

        Context mockContext = mock(Context.class);
        when(mockContext.getString(R.string.new_upvote_event)).thenReturn("test");
        MessageAdapter sampleAdapter = new MessageAdapter
                ( mockContext, usrList, mockFBU, mockSocket, mockFrag );
        sampleAdapter.clickToVote(mockMessage);
        usrList.add(sameName);
        sampleAdapter.clickToVote(mockMessage);
    }

    @Test
    public void getItemCount() {
        String uName = "user";
        List<String> usrList = new ArrayList<String>();
        usrList.add(uName);
        FragmentActivity mockFrag = mock(FragmentActivity.class);
        Socket mockSocket = mock(Socket.class);
        MessageAdapter sampleAdapter = new MessageAdapter
                ( mContext, usrList, mUser, mockSocket, mockFrag );
        assertTrue(sampleAdapter.getItemCount() == 1);
    }

    @Test
    public void getItemViewType() {
        List<Message> cList = new ArrayList<Message>();
        Message MockMessage = mock(Message.class);
        Message MockMessage2 = mock(Message.class);
        FirebaseUser mockFBU = mock(FirebaseUser.class);
        when(mockFBU.getUid()).thenReturn("UID");
        when(MockMessage.getSenderID()).thenReturn("UID");
        when(MockMessage2.getSenderID()).thenReturn("NOT_UID");
        Socket mockSocket = mock(Socket.class);
        cList.add(MockMessage);
        cList.add(MockMessage2);
        FragmentActivity mockFrag = mock(FragmentActivity.class);
        MessageAdapter sampleAdapter = new MessageAdapter
                ( mContext, cList, mockFBU, mockSocket, mockFrag);
        assertTrue(sampleAdapter.getItemViewType(0) == 0);
        assertTrue(sampleAdapter.getItemViewType(1) == 1);



    }
}