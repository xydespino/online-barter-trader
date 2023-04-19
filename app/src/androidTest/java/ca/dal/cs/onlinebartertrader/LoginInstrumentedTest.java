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
public class LoginInstrumentedTest {

    //Test Inputs
    String validUsername = "username";
    String missingUsername = "missingUsername";
    String invalidUsername = "!!!";
    String validPassword = "Password";
    String missingPassword = "missingPassword";
    String invalidPassword = "!!!";

    @Rule
    public ActivityScenarioRule<StartActivity> myRule = new ActivityScenarioRule<>(StartActivity.class);

    @BeforeClass
    public static void setup() {
        Intents.init();
    }

    /*** AT-1:1**/
    @Test
    public void loginPage() {
        onView(withId(R.id.username)).check(matches(withText("")));
        onView(withId(R.id.password)).check(matches(withText("")));
        onView(withId(R.id.loginButton)).check(matches(withText("Login")));
        onView(withId(R.id.statusLabel)).check(matches(withText("")));
    }

    /*** AT-1:3**/
    @Test
    public void invalidUsername() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText(invalidUsername));
        onView(withId(R.id.password)).perform(typeText(validPassword));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(200);
        onView(withId(R.id.statusLabel)).check(matches(withText("Username is not alphanumeric")));
    }

    /*** AT-1:3**/
    @Test
    public void emptyUsername() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText(""));
        onView(withId(R.id.password)).perform(typeText(validPassword));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(200);
        onView(withId(R.id.statusLabel)).check(matches(withText("Username is empty")));
    }

    /*** AT-1:3**/
    @Test
    public void invalidPassword() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText(validUsername));
        onView(withId(R.id.password)).perform(typeText(invalidPassword));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(200);
        onView(withId(R.id.statusLabel)).check(matches(withText("Password is not alphanumeric")));
    }

    /*** AT-1:3**/
    @Test
    public void emptyPassword() throws InterruptedException {
        onView(withId(R.id.username)).perform(typeText(validUsername));
        onView(withId(R.id.password)).perform(typeText(""));
        onView(withId(R.id.loginButton)).perform(click());
        Thread.sleep(200);
        onView(withId(R.id.statusLabel)).check(matches(withText("Password is empty")));
    }
}
