package champagne86.com.classconnect;

import org.junit.Before;
import org.junit.Test;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ClassroomTest {

    @Mock
 //   private String name;
   // private boolean isActive;
    //private String startDate;
   // private String endDate;
    private Map<String, String[]> dayTimeActive;

    @InjectMocks
    public Classroom injectedClass;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void setActiveDay() {
       // Classroom sampleClass = new Classroom("test");

        injectedClass.setActiveDay("1", "2", "3");
    }

    @Test
    public void setStartDate() {

    }

    @Test
    public void setEndDate() {
    }

    @Test
    public void addBlock() {
        Classroom sampleClass = new Classroom();
       // injectedClass.setActiveDay("1", "2", "3");
        //assertTrue(injectedClass.addBlock("1", "2", "3"));
        String s = "1";
        String retArr[] = {"1", "2", "3"};
        when(dayTimeActive.containsKey(s)).thenReturn(true);
        when(dayTimeActive.get(s)).thenReturn(retArr);
        assertTrue(injectedClass.addBlock("1", "2", "3"));
        assertFalse(injectedClass.addBlock("35345345", "2342323", "122322"));

    }

    @Test
    public void isActive() {
        injectedClass.isActive();
    }

    @Test
    public void getName() {
        Classroom sampleClass = new Classroom("test");
        assertTrue(sampleClass.getName().equals("test"));
    }
}