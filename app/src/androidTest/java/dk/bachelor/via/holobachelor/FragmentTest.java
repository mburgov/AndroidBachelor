package dk.bachelor.via.holobachelor;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import Broadcaster.Broadcaster;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@LargeTest
public class FragmentTest {

        @Rule
        public ActivityTestRule<MainActivity> mActivityRule =
                new ActivityTestRule<>(MainActivity.class);

        @Test
        public void NavigationIsLoaded() {
            onView(withId(R.id.gesture_view)).check(matches(isDisplayed()));
        }

        @Test
        public void AccessInput() {
            onView(withId(R.id.navigation_input)).perform(click());
            onView(withId(R.id.editText)).check(matches(isDisplayed()));
        }

        @Test
        public void AccessSettings() {
            onView(withId(R.id.navigation_settings)).perform(click());
            onView(withId(R.id.textView30)).check(matches(isDisplayed()));
        }

        @Test
        public void AccessNavigation() {
            onView(withId(R.id.navigation_navigation)).perform(click());
            onView(withId(R.id.gesture_view)).check(matches(isDisplayed()));
        }
}
