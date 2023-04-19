package ca.dal.cs.onlinebartertrader.LoginRegisterPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationVerification extends AppCompatActivity {
    static String errorMessage;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference().child("Users");

    static boolean check = false;

    protected static boolean isUsernameTaken(CharSequence username) {

        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference("Users").child(username.toString());

        currentUser.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    check = true;
                }else{
                    check = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*
                // Unused
                 */
            }
        });
       return check;
    }

    protected static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    protected static boolean isAlphanumeric(String word) {
        return word.matches("[A-Za-z0-9]+");
    }

    private static boolean containsUpperCase(String str) {
        String[] split = str.split("");
        for (int i = 0; i < str.length(); i++) {
            if (split[i].matches("[A-Z]")) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsLowerCase(String str) {
        String[] split = str.split("");
        for (int i = 0; i < str.length(); i++) {
            if (split[i].matches("[a-z]")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks that the given string follows the pattern of an email (example@email.com)
     *
     * @param email String value of email
     * @return true if string follows pattern, false otherwise
     */
    public static boolean isValidEmail(CharSequence email) {
        if(email == null || isEmpty(email)){
            errorMessage = "Email is empty";
            return false;
        }
        else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            errorMessage = "Invalid email";
            return false;
        }
        return true;
    }

    protected static boolean isValidUsername(String username) {
        if (username.isEmpty()) {
            errorMessage = "Username is empty";
            return false;
        } else if (!isAlphanumeric(username)) {
            errorMessage = "Username is not alphanumeric";
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            errorMessage = "Password is empty";
            return false;
        } else if (!isAlphanumeric(password)) {
            errorMessage = "Password is not alphanumeric";
            return false;
        } else if (!(password.length() >= 8 && containsLowerCase(password) && containsUpperCase(password))) {
            errorMessage = "Password must be at least 8 characters and contain an uppercase and lowercase letter";
            return false;
        }
        return true;
    }

    protected static boolean isValidRegistration(String fName, String lName, String username, String email, String password, String verifyPassword) {

        if (fName.isEmpty()) {
            errorMessage = "First name is empty";
            return false;
        } else if (lName.isEmpty()) {
            errorMessage = "Last name is empty";
            return false;
        } else if (!isValidUsername(username)) {
            return false;
        } else if (!isValidPassword(password)) {
            return false;
        } else if(!isValidEmail(email)){
            return false;
        } else if (isUsernameTaken(username)) {
            errorMessage = "Username is already in use";
            return false;
        }
        else if (!verifyPassword.equals(password)) {
            errorMessage = "Passwords do not match";
            return false;
        }
        return true;
    }

    protected static String getErrorMessage() {
        return errorMessage;
    }

}



