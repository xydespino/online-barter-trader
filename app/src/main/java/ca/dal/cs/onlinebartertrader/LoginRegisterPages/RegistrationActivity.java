package ca.dal.cs.onlinebartertrader.LoginRegisterPages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.dal.cs.onlinebartertrader.HomePages.HomePageActivity;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.StartActivity;
import ca.dal.cs.onlinebartertrader.User;

/**
 * Logic for the registration page
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Button registerButton = findViewById(R.id.registrationButton);
        registerButton.setOnClickListener(this);


        Button loginRedirectButton = findViewById(R.id.login);
        loginRedirectButton.setOnClickListener(v -> switchToLoginPage());

        initializeDatabase();
    }

    /**
     * Gets a snapshot of the root/users part of the database and saves it to local variables
     */
    protected void initializeDatabase() {
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
    }

    /**
     * gets the username from the UI
     *
     * @return a string of the username
     */
    protected String getUsername() {
        EditText username = findViewById(R.id.username);
        return username.getText().toString().trim();
    }

    /**
     * gets the first name from the UI
     *
     * @return a string of the first name
     */
    protected String getFirstName() {
        EditText fName = findViewById(R.id.firstName);
        return fName.getText().toString().trim();
    }

    /**
     * gets the last name from the UI
     *
     * @return a string of the last name
     */
    protected String getLastName() {
        EditText lName = findViewById(R.id.lastName);
        return lName.getText().toString().trim();
    }

    /**
     * gets the email name from the UI
     *
     * @return a string of the email
     */
    protected String getEmail() {
        EditText email = findViewById(R.id.emailAddress);
        return email.getText().toString().trim();
    }

    /**
     * gets the password from the UI
     *
     * @return a string of the password
     */
    protected String getPassword() {
        EditText password = findViewById(R.id.password);
        return password.getText().toString().trim();
    }

    /**
     * gets the first name from the UI
     *
     * @return a string of the first name
     */
    protected String getVerifyPassword() {
        EditText verPassword = findViewById(R.id.verifyPassword);
        return verPassword.getText().toString().trim();
    }

    private void switchToLoginPage() {
        startActivity(new Intent(RegistrationActivity.this, StartActivity.class));
    }

    private void switchToHomePage() {
        startActivity(new Intent(RegistrationActivity.this, HomePageActivity.class));
    }

    public void onClick(View v) {
        String firstName = getFirstName();
        String lastName = getLastName();
        String username = getUsername();
        String email = getEmail();
        String password = getPassword();
        String verifyPass = getVerifyPassword();

        boolean valid = RegistrationVerification.isValidRegistration(firstName, lastName, username, email, password, verifyPass);
        if (valid) {
            User user = new User(firstName, lastName, username, email, password);

            // Callback function: https://stackoverflow.com/questions/45351890/how-to-save-data-from-java-application-to-firebase
            userRef.child(username).setValue(user, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    System.out.println("Data could not be saved: " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                    switchToHomePage();
                }
            });

        } else {
            String errorMessage = RegistrationVerification.getErrorMessage();
            Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
