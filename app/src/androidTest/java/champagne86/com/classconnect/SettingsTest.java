package champagne86.com.classconnect;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import champagne86.com.classconnect.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void settingsTest() {
        ViewInteraction loginButton = onView(
allOf(withId(R.id.login_button), withText("Continue with Facebook"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
0),
isDisplayed()));
        loginButton.perform(click());
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(3598186);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
        
        ViewInteraction viewGroup = onView(
allOf(withId(R.id.toolbar),
childAtPosition(
allOf(withId(R.id.content_frame),
childAtPosition(
withId(R.id.drawer_layout),
0)),
0),
isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
        
        ViewInteraction appCompatImageButton = onView(
allOf(withContentDescription("Navigate up"),
childAtPosition(
allOf(withId(R.id.toolbar),
childAtPosition(
withId(R.id.content_frame),
0)),
1),
isDisplayed()));
        appCompatImageButton.perform(click());
        
        ViewInteraction checkedTextView = onView(
allOf(withId(R.id.design_menu_item_text),
childAtPosition(
childAtPosition(
withId(R.id.design_navigation_view),
3),
0),
isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));
        
        ViewInteraction navigationMenuItemView = onView(
allOf(childAtPosition(
allOf(withId(R.id.design_navigation_view),
childAtPosition(
withId(R.id.nav_view),
0)),
3),
isDisplayed()));
        navigationMenuItemView.perform(click());
        
        ViewInteraction button = onView(
allOf(withId(R.id.uploadIcsBtn),
childAtPosition(
childAtPosition(
withId(R.id.content_frame),
1),
1),
isDisplayed()));
        button.check(matches(isDisplayed()));
        
        ViewInteraction textView = onView(
allOf(withText("Settings"),
childAtPosition(
allOf(withId(R.id.toolbar),
childAtPosition(
withId(R.id.content_frame),
0)),
1),
isDisplayed()));
        textView.check(matches(withText("Settings")));
        
        ViewInteraction viewGroup2 = onView(
allOf(withId(R.id.toolbar),
childAtPosition(
allOf(withId(R.id.content_frame),
childAtPosition(
withId(R.id.drawer_layout),
0)),
0),
isDisplayed()));
        viewGroup2.check(matches(isDisplayed()));
        
        }

        private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
