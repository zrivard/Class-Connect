package champagne86.com.classconnect;

import org.junit.Before;
import org.junit.Test;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.modules.junit4.PowerMockRunner;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ClasslistAdapterTest {
    @Mock
    private List<Classroom> classList;
    private FirebaseUser mUser;
    private Context mContext;
    @Spy
    Classroom sampleClass1 = new Classroom();
    Classroom sampleClass2 = new Classroom();

    @Test
    public void onCreateViewHolder() {
        ClasslistAdapter sampleAdapter = new ClasslistAdapter( mContext, classList, mUser );
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

    }
}