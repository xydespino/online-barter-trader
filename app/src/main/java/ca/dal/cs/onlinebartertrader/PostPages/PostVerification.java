package ca.dal.cs.onlinebartertrader.PostPages;

/**
 * Contains logic for validating posts
 */

public class PostVerification {
    private static String errorMessage;

    private PostVerification() {
    }

    /**
     * Determines whether the name is valid
     *
     * @param name name to be tested
     */
    public static boolean isNameValid(String name) {
        return !(name.isEmpty() || !isAlphanumeric(name) || name.length() > 100);
    }

    /**
     * Determines whether the description is valid
     *
     * @param desc description to be tested
     */
    public static boolean isDescValid(String desc) {
        return !(desc.isEmpty() || !isAlphanumeric(desc) || desc.length() > 500);
    }

    /**
     * Determines whether the quantity is valid
     *
     * @param quantity quantity to be tested
     */
    private static boolean isQuantityValid(String quantity) {
        return !(quantity.isEmpty() || Integer.parseInt(quantity) > 255 || Integer.parseInt(quantity) <= 0);
    }

    /**
     * Determines whether the quality is valid
     *
     * @param quality quality to be tested
     */
    private static boolean isQualityValid(String quality) {
        return !(quality.isEmpty() || Double.parseDouble(quality) > 5 || Double.parseDouble(quality) < 0);
    }

    /**
     * Determines whether the input string is alphanumeric
     *
     * @param word word to be tested
     */
    protected static boolean isAlphanumeric(String word) {
        return word.matches("[A-Za-z0-9\\s\\-_,.;:()&]+");
    }

    /**
     * Performs validation as to whether the post details are valid
     *
     * @param name     name associated with post
     * @param category category associated with post
     * @param desc     description associated with post
     * @param quality  quality associated with post
     * @param quantity quantity associated with post
     */
    public static boolean isValidPost(String name, String desc, String quantity, String quality, String category) {
        if (!isNameValid(name)) {
            errorMessage = "Item name is invalid";
            return false;
        } else if (!isDescValid(desc)) {
            errorMessage = "Item description is invalid";
            return false;
        } else if (!isQuantityValid(quantity)) {
            errorMessage = "Item quantity is invalid";
            return false;
        } else if (!isQualityValid(quality)) {
            errorMessage = "Item quality is invalid";
            return false;
        } else if (category.equals("Select Category")) {
            errorMessage = "Please choose a category";
            return false;
        }
        return true;
    }

    /**
     * Returns the error message
     */
    protected static String getErrorMessage() {
        return errorMessage;
    }
}
