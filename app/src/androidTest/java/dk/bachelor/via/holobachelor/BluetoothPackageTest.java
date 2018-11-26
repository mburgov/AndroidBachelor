package dk.bachelor.via.holobachelor;

import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import Broadcaster.Broadcaster;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@LargeTest
public class BluetoothPackageTest {

        @Rule
        public ActivityTestRule<MainActivity> mActivityRule =
                new ActivityTestRule<>(MainActivity.class);

        @Test
        public void TestPanMap() {
                onView(withId(R.id.gesture_view)).check(matches(isDisplayed()));
                onView(withId(R.id.button2)).perform(click());
                waitFor(1);
                assertEquals(Broadcaster.getInstance(null, null, null).getData()
                        .getManufacturerSpecificData().get(1775)[0], (byte) 1);
        }

        @Test
        public void TestSingleTap() {
                onView(withId(R.id.gesture_view)).check(matches(isDisplayed()));
                onView(withId(R.id.gesture_view)).perform(click());
                waitFor(1);
                assertEquals(Broadcaster.getInstance(null, null, null).getData()
                        .getManufacturerSpecificData().get(1775)[0], (byte) 6);
        }

        @Test
        public void TestInput() {
                onView(withId(R.id.navigation_input)).perform(click());
                onView(withId(R.id.editText)).check(matches(isDisplayed()));
                onView(withId(R.id.editText)).perform(typeText("6"));
                onView(withId(R.id.button3)).perform(click());
                waitFor(1);
                assertEquals(Broadcaster.getInstance(null, null, null).getData()
                        .getManufacturerSpecificData().get(1775)[0], (byte) 4);
        }


        public void waitFor(int seconds) {
                seconds = seconds < 0 ? 0 : seconds;
                while (--seconds >= 0) {
                        try {
                                Thread.sleep(1000);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }

}
