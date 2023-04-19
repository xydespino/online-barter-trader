package ca.dal.cs.onlinebartertrader.HomePages;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ca.dal.cs.onlinebartertrader.MessagePages.MessagesActivity;
import ca.dal.cs.onlinebartertrader.PostPages.CreatePostActivity;
import ca.dal.cs.onlinebartertrader.PostPages.MyPostsActivity;
import ca.dal.cs.onlinebartertrader.PostPages.SavedPostsActivity;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;

/**
 * Controls the functionality of the bottom UI bar
 */

public class NavigationHandler {

    public NavigationHandler(BottomNavigationView menu) {
        Context ctx = menu.getContext();
        menu.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menuhome) {
                goToHomePage(ctx);
            } else if (itemId == R.id.menuitems) {
                goToMyPostsPage(ctx);
            } else if (itemId == R.id.menupost) {
                goToPostCreationPage(ctx);
            } else if(itemId == R.id.menusaved) {
                goToSavedPosts(ctx);
            } else if(itemId == R.id.menumessages) {
                goToMessages(ctx);
            }
            return false;
        });
    }

    /**
     * Switches the UI to the home page when called
     *
     * @param ctx the current instance of the application
     */
    public void goToHomePage(Context ctx) {
        Intent intent = new Intent(ctx, HomePageActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * Switches the UI to the post creation page when called
     *
     * @param ctx the current instance of the application
     */
    public void goToPostCreationPage(Context ctx) {
        Intent intent = new Intent(ctx, CreatePostActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * Switches the UI to the my posts page when called
     *
     * @param ctx the current instance of the application
     */
    public void goToMyPostsPage(Context ctx) {
        Intent intent = new Intent(ctx, MyPostsActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * Planned to be used to switch the UI to the saved posts page when called
     *
     * @param ctx the current instance of the application
     */
    public void goToSavedPosts(Context ctx) {
        Intent intent = new Intent(ctx, SavedPostsActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * Switches the UI to the messages page when called
     *
     * @param ctx the current instance of the application
     */
    public void goToMessages(Context ctx) {
        Intent intent = new Intent(ctx, MessagesActivity.class);

        //Updates the Shared Preference to assume all messages are now read; the messages that dictate the banner are set to 0
        SharedPreference.setPrefRead(ctx, SharedPreference.getPrefCurrent(ctx) + SharedPreference.getRead(ctx));
        SharedPreference.setPrefCurrent(ctx, 0);
        ctx.startActivity(intent);
    }
}
