package champagne86.com.classconnect;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import com.google.firebase.auth.FirebaseUser;
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
import com.github.nkzawa.socketio.client.Socket;

@RunWith(PowerMockRunner.class)
public class QuestionAdapterTest {
    @Mock
    private List<Classroom> QList;
    private FirebaseUser mUser;
    private Context mContext;
    private Classroom mClass;
    private Socket mSocket;
    @Test
    public void onCreateViewHolder() {
        QuestionAdapter sampleAdapter = new QuestionAdapter( mContext, QList, mUser, mSocket );
    }

    @Test
    public void onBindViewHolder() {
    }

    @Test
    public void getItemCount() {

        List<Classroom> filledList = new ArrayList<Classroom>();
        filledList.add(mClass);
        QuestionAdapter sampleAdapter = new QuestionAdapter( mContext, filledList, mUser, mSocket );
        assertTrue(sampleAdapter.getItemCount() == 1);
    }

    @Test
    public void getItemViewType() {
        List<Classroom> cList = new ArrayList<Classroom>();
        Classroom MockClass = mock(Classroom.class);
        when(MockClass.isActive()).thenReturn(true);
        cList.add(MockClass);
        QuestionAdapter sampleAdapter = new QuestionAdapter( mContext, QList, mUser, mSocket );
        assertTrue(sampleAdapter.getItemViewType(0) == 1);
    }
}