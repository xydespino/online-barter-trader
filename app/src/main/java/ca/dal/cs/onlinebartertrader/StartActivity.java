package ca.dal.cs.onlinebartertrader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.dal.cs.onlinebartertrader.HomePages.HomePageActivity;
import ca.dal.cs.onlinebartertrader.LoginRegisterPages.LoginLogic;
import ca.dal.cs.onlinebartertrader.LoginRegisterPages.RegistrationActivity;
import ca.dal.cs.onlinebartertrader.PostPages.Post;

/**
 * This class is first accessed upon the application launching.
 * It mainly what the first screen the user should see based on whether they've logged in or not,
 * and the login screen information
 */

public class StartActivity extends AppCompatActivity {
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    FirebaseDatabase database = null;
    static DatabaseReference postRef = null;
    static DatabaseReference userRef = null;

    String errorMessage = "";
    User user = null;
    public static ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeDatabase();
        readPosts(posts -> {
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        //If user previously logged in
        if (SharedPreference.getUsername(this).length() != 0) {
            //This now switches to HomePage directly, as switchToHomePage() now checks the username / password fields
            Intent i = new Intent(StartActivity.this, HomePageActivity.class);
            startActivity(i);
        }

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> switchToHomePage());

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> switchToRegisterPage());
    }

    private void initializeDatabase() {
        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("Posts");
    }

    protected void setStatusMessage(String message) {
        TextView statusLabel = findViewById(R.id.statusLabel);
        statusLabel.setText(message);
    }

    protected String getUsername() {
        EditText username = findViewById(R.id.username);
        return username.getText().toString().trim();
    }

    protected String getPassword() {
        EditText password = findViewById(R.id.password);
        return password.getText().toString().trim();
    }

    protected static DatabaseReference getUserRef() {
        return userRef;
    }

    protected void switchToRegisterPage() {
        Intent i = new Intent(StartActivity.this, RegistrationActivity.class);
        startActivity(i);
    }

    //Code for switching to Home Page
    protected void switchToHomePage() {

        //Getting the context of LoginActivity here since the context in onDataChange would be different
        StartActivity context = this;

        /*Pull only the object of Users/username (where username is what was typed into the username
        field), if you type a username that doesn't exist in  the DB it returns an object with the
        key of that username and value null which is handy*/

        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference("Users").child(getUsername());

        //I made it so the thread waits for the DB to finish updating here rather than in a different class
        currentUser.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //LoginLogic now takes three parameters, more about that in the respective method, also DB validation happens there
                boolean check = LoginLogic.isValidLogin(dataSnapshot, getUsername(), getPassword());

                if (check) {

                    //update Shared preference
                    SharedPreference.setUsername(context, getUsername());
                    setStatusMessage("");

                    //Switch to home page
                    Intent i = new Intent(StartActivity.this, HomePageActivity.class);
                    Toast.makeText(StartActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(i);

                } else {

                    //Error out otherwise
                    errorMessage = LoginLogic.getErrorMessage();
                    setStatusMessage(errorMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*
                // Unused
                 */
            }
        });
    }

    public interface MyCallback { //DB Interface
        void onCallback(List<Post> posts);
    }

    protected static void readPosts(MyCallback myCallback) {
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        assert post != null;
                        post.setPostId(Long.parseLong(Objects.requireNonNull(snapshot.getKey())));
                        posts.add(post);
                    }
                    UserLocation.setPostList(posts);
                } catch (Exception e) {
                    Log.d("Error:", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                /*
                // Unused
                 */
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {


                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {

                            requestNewLocationData();
                        } else {
                            UserLocation.setUserLatitude(location.getLatitude()); //Save location data to global variable.
                            UserLocation.setUserLongitude(location.getLongitude());


                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            UserLocation.setUserLongitude(mLastLocation.getLongitude());
            UserLocation.setUserLatitude(mLastLocation.getLatitude());

        }
    };

    private boolean checkPermissions() {

        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}
