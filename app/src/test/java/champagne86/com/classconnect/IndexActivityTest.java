package champagne86.com.classconnect;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)

public class IndexActivityTest {
    @Mock
    View mockView;
    FirebaseAuth mockAuth;
    @Test
    public void onCreate() {
        IndexActivity testIndex = new IndexActivity();
    }

    @Test
    public void onStart() {
    }

    @Test
    public void onActivityResult() {
    }

    @Test
    public void usernameValid() {
        IndexActivity testIndex = new IndexActivity();
        assertTrue(testIndex.usernameValid("shouldbeValid"));
        assertFalse(testIndex.usernameValid("defsnotvalid%#($*#&$(* hekjhdak___"));
    }

    @Test
    public void signOut() {

    }

    @Test
    public void updateUI() {
//        IndexActivity testIndex = new IndexActivity();
//        testIndex.updateUI(null);

    }

    @Test
    public void checkUsrName() {
    }

    @Test
    public void onClick() {

        IndexActivity testIndex = new IndexActivity();
        when(mockView.getId()).thenReturn(0);
        testIndex.onClick(mockView);
    }
}