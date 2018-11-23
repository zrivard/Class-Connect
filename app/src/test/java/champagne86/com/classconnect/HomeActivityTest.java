package champagne86.com.classconnect;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.internal.util.reflection.FieldSetter;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class HomeActivityTest {
    @Mock
    MenuItem mockMenuItem;
    DrawerLayout mockDrawer;
    Bundle mockBundle;
    @Test
    public void onCreate() {

    }

    @Test
    public void onOptionsItemSelected() {
        HomeActivity testActivity = new HomeActivity();
        when(mockMenuItem.getItemId()).thenReturn(0);
//       testActivity.onOptionsItemSelected(mockMenuItem);

    }

    @Test
    public void setDefaultFragment() {
//        HomeActivity testActivity = new HomeActivity();
//        testActivity.setDefaultFragment();
    }

    @Test
    public void selectDrawerItem() {
// testActivity = new HomeActivity();
//        when(mockMenuItem.getItemId()).thenReturn(0);
//        testActivity.selectDrawerItem(mockMenuItem);
    }

    @Test
    public void signOut() {
    }
}