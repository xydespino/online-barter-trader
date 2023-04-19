package ca.dal.cs.onlinebartertrader.HomePages;

import java.util.ArrayList;
import java.util.List;

import ca.dal.cs.onlinebartertrader.PostPages.Post;
import ca.dal.cs.onlinebartertrader.UserLocation;

/**
 * Used to calculate distance between posts and the user's location
 */
public class DistanceCalculator {
    private DistanceCalculator() {
    }

    /**
     * Calculation Block for distances to user, quick-sort.
     *
     * @param degrees: Degree value
     * @return The radian value of the degrees entered
     */
    protected static Double degreesToRadians(Double degrees) {
        return degrees * Math.PI / 180;
    }

    static void swap(List<Post> arr, int i, int j) {
        Post temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }

    static int partition(List<Post> arr, int low, int high) {
        int pivot = arr.get(high).getDistanceToUser();

        // Index of smaller element and indicates the right position of pivot found so far
        int i = (low - 1);

        for (int j = low; j <= high - 1; j++) {
            // If current element is smaller than the pivot
            if (arr.get(j).getDistanceToUser() < pivot) {
                // Increment index of smaller element
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    static void quickSort(List<Post> arr, int low, int high) {
        if (low < high) {

            // pi is partitioning index, arr[p] is now at right place
            int pi = partition(arr, low, high);

            // Separately sort elements before partition and after partition
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    /**
     * Calculates the distance between an array of distances and the user's distance
     *
     * @return an int of the distance
     */

    public static int calculateDistances(ArrayList<Double> location) {
        int earthRadiusKm = 6371;
        double userLatitude = UserLocation.getUserLatitude();
        double userLongitude = UserLocation.getUserLongitude();


        double currLatitude = location.get(0);
        double currLongitude = location.get(1);

        double dLat = degreesToRadians(currLatitude - userLatitude);
        double dLon = degreesToRadians(currLongitude - userLongitude);
        double lat1 = degreesToRadians(userLatitude);
        double lat2 = degreesToRadians(currLatitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = (earthRadiusKm * c);
        return (int) Math.round(distance);
    }

}