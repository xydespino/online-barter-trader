package ca.dal.cs.onlinebartertrader.LoginRegisterPages;

import com.google.firebase.database.DataSnapshot;

import ca.dal.cs.onlinebartertrader.User;

/**
 * Contains logic for determining if a login is valid
 */
public class LoginLogic {

    static String errorMessage = "";
    static User user;
    static Boolean check = false;

    private LoginLogic() {
    }

    /**
     * Returns the string of the error message
     *
     * @return returns the string of the error message
     */
    public static String getErrorMessage() {
        return errorMessage;
    }

    /*isValidLogin now takes in a DataSnapshot from the first parameter, this means that
    the database query can happen elsewhere, and this just takes the snapshot. not a big distinction
    now but hopefully this makes it more modular in future*/

    /**
     * Main logic for determining if the inputted credentials are correct; checks against firebase
     *
     * @param DB       a DataSnapshot of the root/users table of the database
     * @param username the username the user input at login
     * @param password the password the user input at login
     * @return returns true if credentials match the database; false otherwise
     */
    public static boolean isValidLogin(DataSnapshot DB, String username, String password) {

        //check if username / password are valid, if null entries aren't caught here it will crash
        if (!RegistrationVerification.isValidUsername(username) || !RegistrationVerification.isValidPassword(password)) {
            errorMessage = RegistrationVerification.getErrorMessage();
            return false;
        } else {
            errorMessage = "Wrong username or password";
            //Check if Username exists in database; the value is null if it doesn't exist.
            if (DB.getValue() == null) {
                check = false;
            }

            //Check if Password is matches the DB; would be trivial to update this if we decide to hash passwords
            else if (!password.equals(DB.child("password").getValue().toString())) {
                check = false;
            }

            //return true if it passes the above cases
            else {
                errorMessage = "";
                check = true;
            }
        }
        return check;
    }
}
