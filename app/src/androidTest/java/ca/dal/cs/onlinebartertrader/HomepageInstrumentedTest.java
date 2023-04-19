package ca.dal.cs.onlinebartertrader;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.dal.cs.onlinebartertrader.HomePages.SearchPageActivity;
import ca.dal.cs.onlinebartertrader.ProfilePages.ProfileActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class HomepageInstrumentedTest {

    @Rule
    public ActivityScenarioRule<StartActivity> testrule = new ActivityScenarioRule<>(StartActivity.class);

    @Test
    public void checkIfMovedToSearchPage() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.searchText)).perform(typeText("test"));
        onView(withId(R.id.searchButton)).perform(click());
        Thread.sleep(1000);
        intended(hasComponent(SearchPageActivity.class.getName()));
        onView(withId(R.id.menuhome)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.logoutButton)).perform(click());
        Intents.release();
    }

    @Test
    public void checkIfMovedToLoginPage() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.logoutButton)).perform(click());
        Thread.sleep(1000);
        intended(hasComponent(StartActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void checkIfMovedToProfilePage() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.profileMenu)).perform(click());
        Thread.sleep(1000);
        intended(hasComponent(ProfileActivity.class.getName()));
        onView(withId(R.id.homeButton)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
        Intents.release();
    }

    @Test
    public void checkIfEmptySearch() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.searchButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.statusLabel2)).check(matches(withText("Empty Search!")));
        onView(withId(R.id.logoutButton)).perform(click());
        Intents.release();
    }

    @Test
    public void checkIfSearchGreaterThan100() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.searchText)).perform(typeText("This is a test to see if the error message works if more than 100 characters are entered into the search"));
        onView(withId(R.id.searchButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.statusLabel2)).check(matches(withText("Search over 100 character limit!")));
        onView(withId(R.id.logoutButton)).perform(click());
        Intents.release();
    }

    @Test
    public void checkIfItemFound() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.searchText)).perform(typeText("test post"));
        onView(withId(R.id.searchButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.searchedText)).check(matches(withText("test post")));
        Thread.sleep(1000);
        onView(withId(R.id.menuhome)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
        Intents.release();
    }

    @Test
    public void checkIfItemNotFound() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("Password"));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.searchText)).perform(typeText("Android"));
        onView(withId(R.id.searchButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.statusLabel4)).check(matches(withText("Item not found!")));
        onView(withId(R.id.menuhome)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.logoutButton)).perform(click());
        Intents.release();
    }


}
