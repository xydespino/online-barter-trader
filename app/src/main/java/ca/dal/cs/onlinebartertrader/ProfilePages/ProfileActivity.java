package ca.dal.cs.onlinebartertrader.ProfilePages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.dal.cs.onlinebartertrader.HomePages.HomePageActivity;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;

/**
 * Contains Home Page logic
 */

public class ProfileActivity extends AppCompatActivity {

    FirebaseDatabase database = null;
    static DatabaseReference userRef = null;
    private String username;
    private String email;
    private String name;
    private String fName;
    private String lName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button homeButton = findViewById(R.id.homeButton);
        Button editProfileButton = findViewById(R.id.editProfileButton);
        Button preferenceButton = findViewById(R.id.preferenceButton);

        homeButton.setOnClickListener(v -> switchToHomePage());
        editProfileButton.setOnClickListener(v -> switchToEditPage());
        preferenceButton.setOnClickListener(v -> switchToPreferencePage());

        getUserInfo();

        TextView usernameField = findViewById(R.id.displayUsername);
        usernameField.setText(username);
        TextView emailField = findViewById(R.id.displayEmail);
        emailField.setText(email);
        TextView fNameField = findViewById(R.id.displayName);
        fNameField.setText(name);
    }

    protected void getUserInfo() {
        //Initialize database to be edited
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

        //Create a current user object of the currently logged in user to read data from the database
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference("Users").child(SharedPreference.getUsername(this));
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue().toString();
                email = dataSnapshot.child("emailAddress").getValue().toString();
                fName = dataSnapshot.child("firstName").getValue().toString();
                lName = dataSnapshot.child("lastName").getValue().toString();
                name = fName + " " + lName;

                TextView usernameField = findViewById(R.id.displayUsername);
                usernameField.setText(username);
                TextView emailField = findViewById(R.id.displayEmail);
                emailField.setText(email);
                TextView fNameField = findViewById(R.id.displayName);
                fNameField.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*
                // Unused
                 */
            }
        });
    }

    protected void switchToHomePage() {
        Intent i = new Intent(ProfileActivity.this, HomePageActivity.class);
        startActivity(i);
    }

    protected void switchToEditPage() {
        Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
        i.putExtra("email", email);
        i.putExtra("fName", fName);
        i.putExtra("lName", lName);
        startActivity(i);
    }

    protected void switchToPreferencePage() {
        Intent i = new Intent(ProfileActivity.this, PreferenceActivity.class);
        startActivity(i);
    }

}

