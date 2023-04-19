package ca.dal.cs.onlinebartertrader;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.dal.cs.onlinebartertrader.ProfilePages.PreferenceActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PreferenceInstrumentedTest {

    @Rule
    public ActivityScenarioRule<PreferenceActivity> testrule = new ActivityScenarioRule<PreferenceActivity>(PreferenceActivity.class);

    @Test
    public void checkIfDistGreater1000(){
        onView(withId(R.id.distanceText)).perform(replaceText("10000"));
        onView(withId(R.id.savePref)).perform(click());
        onView(withId(R.id.statusLabel3)).check(matches(withText("Distance Greater then 1000 Km!")));
    }

    @Test
    public void checkIfDistEmpty(){
        onView(withId(R.id.distanceText)).perform(replaceText(""));
        onView(withId(R.id.savePref)).perform(click());
        onView(withId(R.id.statusLabel3)).check(matches(withText("Must have distance set!")));
    }

    @Test
    public void checkIfCategorySelected(){
        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Select Category")));

        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Arts")));

        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Audio")));

        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(3).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Baby Items")));

        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(4).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Books")));

        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(5).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Cameras")));

        onView(withId(R.id.catListPreference)).perform(click());
        onData(anything()).atPosition(7).perform(click());
        onView(withId(R.id.catListPreference)).check(matches(withSpinnerText("Clothing")));


    }
}