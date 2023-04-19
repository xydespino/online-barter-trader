package ca.dal.cs.onlinebartertrader.ProfilePages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;

/**
 * Contains logic the edit profile page
 */
public class EditProfileActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        String email = bun.getString("email");
        String fName = bun.getString("fName");
        String lName = bun.getString("lName");

        EditText emailField = findViewById(R.id.editProfileEmailAddress);
        emailField.setText(email);
        EditText fNameField = findViewById(R.id.editProfileFirstName);
        fNameField.setText(fName);
        EditText lNameField = findViewById(R.id.editProfileLastName);
        lNameField.setText(lName);

        Button editProfileSaveButton = findViewById(R.id.editProfileSaveButton);
        editProfileSaveButton.setOnClickListener(v -> {

            saveProfileChanges(fNameField, lNameField, emailField);
            //Call readData to update the profile page
            switchToProfilePage();
        });

    }

    /**
     * Switches to the profile page when called
     */
    protected void switchToProfilePage() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Glorified set method to save changes to the database
     *
     * @param emailField contains what the new email will be in the database
     * @param fNameField contains what the new first name will be in the database
     * @param lNameField contains what the new last name will be in the database
     */
    protected void saveProfileChanges(EditText fNameField, EditText lNameField, EditText emailField) {
        //Initialize database to be edited
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

        //This DatabaseReference is the current user object
        DatabaseReference test = userRef.child(SharedPreference.getUsername(this));

        //Update the Database w/ what is currently in the text fields
        test.child("firstName").setValue(fNameField.getText().toString());
        test.child("lastName").setValue(lNameField.getText().toString());
        test.child("emailAddress").setValue(emailField.getText().toString());

    }


}

