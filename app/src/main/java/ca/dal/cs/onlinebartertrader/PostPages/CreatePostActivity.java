package ca.dal.cs.onlinebartertrader.PostPages;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import ca.dal.cs.onlinebartertrader.HomePages.HomePageActivity;
import ca.dal.cs.onlinebartertrader.HomePages.NavigationHandler;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;
import ca.dal.cs.onlinebartertrader.UserLocation;

/**
 * Contains logic for creating a post
 */

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int IMAGE_REQUEST_CODE = 1;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DatabaseReference postRef;
    private ImageButton imageButton;
    private StorageReference fileReference;
    private FirebaseStorage storage;
    private String location;
    private long postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        // Post ID is generated at startup of the Activity as it helps with image naming
        postID = System.currentTimeMillis();

        // Submit button
        Button submitButton = findViewById(R.id.submitPostButton);
        submitButton.setOnClickListener(this);

        // Set bottom Menu selected icon to post creation
        BottomNavigationView bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setSelectedItemId(R.id.menupost);

        new NavigationHandler(bottomMenu);

        getLayout();
        initializeDatabase();


        imageButton.setOnClickListener(v -> chooseImageFromGallery("PostImg"));
    }

    private void displayImage() {
        StorageReference imgRef = storage.getReference();
        String[] path = ("PostImg/" + postID).split("/");

        for (String s : path) {
            imgRef = imgRef.child(s);
        }

        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageButton.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.yourpost_ic)
                    .circleCrop()
                    .override(300, 300)
                    .into(imageButton);
        });
    }

    /**
     * Opens the gallery app/chosen photos app and allows the user to pick an image to upload
     *
     * @param location: The directory on Firebase which the image will be stored
     */
    public void chooseImageFromGallery(String location) {
        this.location = location;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_REQUEST_CODE);

    }

    /**
     * Upload a file to Firebase Storage
     *
     * @param file:     Uri to the selected file
     * @param location: What directory to save into on Firebase
     * @return A reference to the uploaded file
     */
    private StorageReference uploadFile(Uri file, String location) {
        if (file != null) {
            StorageReference directoryRef = storage.getReference(location);

            StorageReference fileRef = directoryRef.child(String.valueOf(postID));
            UploadTask uploadTask = fileRef.putFile(file);

            uploadTask.addOnSuccessListener(v -> displayImage());

            return fileRef;
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            fileReference = uploadFile(filePath, location);
        }
    }

    private void getLayout() {
        imageButton = findViewById(R.id.imageButton);
    }

    protected void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        postRef = database.getReference("Posts");
        storage = FirebaseStorage.getInstance();
    }

    private String getItemName() {
        EditText itemName = findViewById(R.id.itemName);
        return itemName.getText().toString();
    }

    private String getItemDesc() {
        EditText itemDesc = findViewById(R.id.itemDesc);
        return itemDesc.getText().toString();
    }

    private String getItemQuantity() {
        EditText itemQuantity = findViewById(R.id.itemQuantity);
        return itemQuantity.getText().toString();
    }

    private String getItemQuality() {
        RatingBar itemQuality = findViewById(R.id.itemQuality);
        return String.valueOf(itemQuality.getRating());
    }

    private String getCategory() {
        Spinner itemCategory = findViewById(R.id.categorySpinner);
        return itemCategory.getSelectedItem().toString();
    }

    private String getPostType() {
        ToggleButton offeringWanting = findViewById(R.id.offeringWanting);
        return offeringWanting.isChecked() ? "Offering" : "Wanting";
    }

    private String getImagePath() {
        return fileReference != null ? "PostImg/" + postID : "N/A";
    }

    private String getSellerUsername() {
        return SharedPreference.getUsername(this);
    }

    protected void goToHomepage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String name = getItemName();
        String desc = getItemDesc();
        String quantity = getItemQuantity();
        String quality = getItemQuality();
        String category = getCategory();
        String type = getPostType();
        String imagePath = getImagePath();
        String sellerUsername = getSellerUsername();
        ArrayList<Double> locationArray = UserLocation.getUserLocationArrayList();

        boolean valid = PostVerification.isValidPost(name, desc, quantity, quality, category);
        if (valid) {
            DatabaseReference post = postRef.child(String.valueOf(postID));

            //Update the Database w/ what is currently in the text fields
            post.child("itemName").setValue(name);
            post.child("itemDesc").setValue(desc);
            post.child("quantity").setValue(quantity);
            post.child("quality").setValue(quality);
            post.child("location").setValue(locationArray);
            post.child("category").setValue(category);
            post.child("postType").setValue(type);
            post.child("sellerUsername").setValue(sellerUsername);
            post.child("imagePath").setValue(imagePath);
            post.child("status").setValue("Active");


            post.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String message = "Post created successfully";
                    Toast.makeText(CreatePostActivity.this, message, Toast.LENGTH_SHORT).show();
                    goToHomepage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    String errorMessage = error.getMessage();
                    Toast.makeText(CreatePostActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String errorMessage = PostVerification.getErrorMessage();
            Toast.makeText(CreatePostActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
