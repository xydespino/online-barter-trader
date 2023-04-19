package ca.dal.cs.onlinebartertrader;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EditProfilePageInstrumentedTest {

    //Test inputs
    String user = "username";
    String pass = "Password";
    String email = "test@test.ca";
    String name = "test";
    String fullName = name + " " + name;
    String newName = "new";
    String newFName = newName + name;
    String newLName = name + newName;
    String newEmail = "new@new.ca";

    @Rule
    public ActivityScenarioRule<StartActivity> myRule = new ActivityScenarioRule<>(StartActivity.class);


    @BeforeClass
    public static void setup() {
        Intents.init();
    }


    /*** AT-4:1**/
    @Test
    public void displayUserInfo() throws InterruptedException {
        //Get to profile page
        onView(withId(R.id.username)).perform(typeText(user));
        onView(withId(R.id.password)).perform(typeText(pass));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.profileMenu)).perform(click());
        Thread.sleep(5000);

        //Test
        onView(withId(R.id.displayUsername)).check(matches(withText(user)));
        onView(withId(R.id.displayEmail)).check(matches(withText(email)));
        onView(withId(R.id.displayName)).check(matches(withText(fullName)));

        onView(withId(R.id.homeButton)).perform(click());
        onView(withId(R.id.logoutButton)).perform(click());
    }
}

