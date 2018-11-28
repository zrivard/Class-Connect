package champagne86.com.classconnect;

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

@RunWith(PowerMockRunner.class)
public class ClasslistAdapterTest {
    @Mock
    private List<Classroom> classList;
    private FirebaseUser mUser;
    private Context mContext;


    @Test
    public void onCreateViewHolder() {
        ClasslistAdapter sampleAdapter = new ClasslistAdapter( mContext, classList, mUser );
    }

    @Test
    public void viewHolder(){
    }

    @Test
    public void onBindViewHolder() {
    }

    @Test
    public void getItemCount() {

        ClasslistAdapter sampleAdapter = new ClasslistAdapter( mContext, classList, mUser );
        assertTrue(sampleAdapter.getItemCount() == 0);
    }

    @Test
    public void getItemViewType() {
        List<Classroom> cList = new ArrayList<Classroom>();
        Classroom MockClass = mock(Classroom.class);
        when(MockClass.isActive()).thenReturn(false);
        cList.add(MockClass);
        ClasslistAdapter sampleAdapter = new ClasslistAdapter( mContext, cList, mUser);
        assertTrue(sampleAdapter.getItemViewType(0) == 0);

    }

    @Test
    public void getItemViewTypeTrue() {
        List<Classroom> cList = new ArrayList<Classroom>();
        Classroom MockClass = mock(Classroom.class);
        when(MockClass.isActive()).thenReturn(true);
        cList.add(MockClass);
        ClasslistAdapter sampleAdapter = new ClasslistAdapter( mContext, cList, mUser);
        assertTrue(sampleAdapter.getItemViewType(0) == 1);

    }
}