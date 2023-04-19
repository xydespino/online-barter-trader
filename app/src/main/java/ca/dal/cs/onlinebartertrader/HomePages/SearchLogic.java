package ca.dal.cs.onlinebartertrader.HomePages;

import ca.dal.cs.onlinebartertrader.StartActivity;

/**
 * Contains the logic for handling search queries
 */
public class SearchLogic {


    static String searched = "";
    String itemName = "";

    /**
     * checks whether a search is valid
     *
     * @param search a string of the search query
     * @return returns an error message if the string is invalid
     */
    protected static String isValidSearch(String search) {
        setSearch(search);
        if (checkCharacterAmount(search)) {
            return "Search over 100 character limit!";
        } else {
            return "";
        }
    }
    //check search character amount

    /**
     * checks whether a string is is over 100 characters
     *
     * @param search a string of the search query
     * @return returns true if a string is over 100
     */
    protected static boolean checkCharacterAmount(String search) {
        return search.length() > 100;
    }

    /**
     * Adds the search query to class variable
     *
     * @param search a string of the search query
     */
    protected static void setSearch(String search) {
        searched = search.toLowerCase();
    }

    /**
     * finds an item based on the class' search string
     *
     * @return returns a boolean if a match is found
     */
    protected static boolean findItemSearch() {
        boolean check = false;
        for (int i = 0; i < StartActivity.posts.size(); i++) {
            if (StartActivity.posts.get(i).getItemName().equalsIgnoreCase(searched)) {
                SearchPageActivity.posts.add(StartActivity.posts.get(i));
                check = true;
            }
        }
        return check;
    }

    /**
     * find items using selected category
     *
     * @return returns a boolean if a match is found
     */
    protected static boolean findItemCat(String cat) {
        boolean check = false;
        for (int i = 0; i < StartActivity.posts.size(); i++) {
            if (StartActivity.posts.get(i).getCategory().equals(cat)) {
                SearchPageActivity.posts.add(StartActivity.posts.get(i));
                check = true;
            }
        }
        return check;
    }

    /**
     * find items using selected category and search
     *
     * @return returns a boolean if a match is found
     */
    protected static boolean findItemBoth(String cat) {
        boolean check = false;
        for (int i = 0; i < StartActivity.posts.size(); i++) {
            if (StartActivity.posts.get(i).getItemName().equalsIgnoreCase(searched) && StartActivity.posts.get(i).getCategory().equals(cat)) {
                SearchPageActivity.posts.add(StartActivity.posts.get(i));
                check = true;
            }
        }
        return check;
    }

}
