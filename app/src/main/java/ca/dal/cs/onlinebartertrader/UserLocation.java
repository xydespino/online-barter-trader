package ca.dal.cs.onlinebartertrader;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import ca.dal.cs.onlinebartertrader.PostPages.Post;

/**
 * Used for storing / updating the current User's location
 */
public class UserLocation extends Application {
    private static Double userLatitude;
    private static Double userLongitude;
    private static String locationAccessor;

    //Note: Post list will duplicate if changes are applied w/o restarting the app.
    private static List<Post> postList;

    //getters
    public static double getUserLatitude() {
        return userLatitude;
    }

    public static double getUserLongitude() {
        return userLongitude;
    }
    public static String getLocationAccessor() { return locationAccessor;}



    public static void setLocationAccessor (String s) { locationAccessor = s;}

    public static ArrayList<Double> getUserLocationArrayList() {
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(getUserLatitude());
        userLocation.add(getUserLongitude());
        return userLocation;
    }

    //setters


    public static void setUserLongitude(double longitude) {
        UserLocation.userLongitude = longitude;
    }
    public static void setUserLatitude(double latitude) {
        UserLocation.userLatitude = latitude;
    }


    public static List<Post> getPostList() {
        return postList;
    }

    public static void setPostList(List<Post> postList) {
        UserLocation.postList = postList;
    }
}