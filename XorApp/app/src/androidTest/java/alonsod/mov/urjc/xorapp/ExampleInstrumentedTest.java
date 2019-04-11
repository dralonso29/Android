package alonsod.mov.urjc.xorapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("alonsod.mov.urjc.xorapp", appContext.getPackageName());
    }

    @Rule
    public ActivityTestRule<MainActivity> mNameActivityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Test
    public void testMainActivity() {
        int TOGGLE_A = 0;
        int TOGGLE_B = 1;
        int TOGGLE_C = 2;
        int TOGGLE_D = 3;

        Intent intent = new Intent();
        intent.putExtra("username", "Test");
        mNameActivityRule.launchActivity(intent);

        /*onView(withId(R.id.username_edittext))
                .perform(typeText("alonsod"), closeSoftKeyboard());
        onView(withId(R.id.summit)).perform(click());
        onView(withId(R.id.play)).perform(click());*/
        onView(withId(TOGGLE_A)).perform(click()).check(matches(isEnabled()));
        onView(withId(TOGGLE_B)).perform(click()).check(matches(isEnabled()));
        onView(withId(TOGGLE_D)).perform(click()).check(matches(isEnabled()));
        onView(withId(R.id.nextbut)).perform(click());
        onView(withId(TOGGLE_C)).perform(click()).check(matches(isEnabled()));
        onView(withId(R.id.nextbut)).perform(click());
        onView(withId(TOGGLE_A)).perform(click()).check(matches(isEnabled()));
        onView(withId(TOGGLE_B)).perform(click()).check(matches(isEnabled()));
        onView(withId(TOGGLE_C)).perform(click()).check(matches(isEnabled()));
        onView(withId(R.id.nextbut)).perform(click());
        onView(withId(TOGGLE_B)).perform(click()).check(matches(isEnabled()));
        onView(withId(R.id.nextbut)).perform(click());
    }
}
