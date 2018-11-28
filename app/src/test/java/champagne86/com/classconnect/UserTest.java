package champagne86.com.classconnect;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class UserTest {
    static String SAMPLENAME = "Thomas";
    @Mock

    public FirebaseUser mUser;
    public String uId = "sample";
    public String userName;
    public List<Classroom> classList = new ArrayList();

    @InjectMocks
    public User testUser;

    @Test
    public void getUid() {
        when(mUser.getUid()).thenReturn("abcdef");
        User sampleUser = new User(mUser, SAMPLENAME);
        assertTrue(sampleUser.getUid().equals("abcdef"));
    }


    @Test
    public void setUserName() {
        when(mUser.getUid()).thenReturn("abcdef");
        User sampleUser = new User(mUser, SAMPLENAME);
        String NEWNAME = "newName";
        sampleUser.setUserName(NEWNAME);
        assertTrue(sampleUser.getUserName().equals(NEWNAME));
    }

    @Test
    public void getUserName() {
        when(mUser.getUid()).thenReturn("abcdef");
        User sampleUser = new User(mUser, SAMPLENAME);
        assertTrue(sampleUser.getUserName().equals(SAMPLENAME));
    }

    @Test
    public void getClassList() {
        assertTrue(testUser.getClassList().equals(classList));
    }

    @Test
    public void addClassList() {
        when(mUser.getUid()).thenReturn("abcdef");
        User sampleUser = new User(mUser, SAMPLENAME);
        List<Classroom> testList = new ArrayList();
        sampleUser.addClassList(testList);

    }

    @Test
    public void addClass() {
        when(mUser.getUid()).thenReturn("abcdef");
        User sampleUser = new User(mUser, SAMPLENAME);
        Classroom sampleClass = new Classroom();
        sampleUser.addClass(sampleClass);
    }

    @Test
    public void numClasses() {
        when(mUser.getUid()).thenReturn("abcdef");
        User sampleUser = new User(mUser, SAMPLENAME);
        assert (sampleUser.numClasses() == 0);
    }

    @Test
    public void blankConstructor(){
        User blankUser = new User();
    }
}