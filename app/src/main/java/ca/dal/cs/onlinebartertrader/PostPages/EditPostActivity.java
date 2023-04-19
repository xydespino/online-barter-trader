package ca.dal.cs.onlinebartertrader.PostPages;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ca.dal.cs.onlinebartertrader.HomePages.HomePageActivity;
import ca.dal.cs.onlinebartertrader.HomePages.NavigationHandler;
import ca.dal.cs.onlinebartertrader.R;

/**
 * Contains logic for editing a post
 */

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int IMAGE_REQUEST_CODE = 1;
    static DatabaseReference postRef = null;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = null;
    StorageReference fileReference;
    private long postId;
    private String imagePath;
    private String location;
    private Button submitButton;
    private ImageButton imageButton;
    private EditText name;
    private EditText desc;
    private EditText quantity;
    private RatingBar quality;
    private Spinner category;
    private ToggleButton type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpost);

        // Get the extras sent from the MyPostsActivity containing the Post ID
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                postId = -1;
            } else {
                postId = extras.getLong("ca.dal.cs.onlinebartertrader.postId");
            }
        } else {
            postId = (long) savedInstanceState.getSerializable("ca.dal.cs.onlinebartertrader.postId");
        }

        initializeDatabase();
        getPageLayout();

        new NavigationHandler(findViewById(R.id.bottomMenu));

        if (postId != -1) {
            getPostById(postId);
        }

        imageButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    /**
     * Initializes the database and stores certain aspects to class variables
     */
    protected void initializeDatabase() {
        //initialize your database and related fields here
        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("Posts");
    }

    // Searches the database for a post with the given post id, and fills in the fields on the page
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readData(long postId) {

        postRef.child(String.valueOf(postId)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());

            } else {
                Log.d("firebase", String.valueOf(task.getResult()));

                // Get each part of the post from firebase
                String nameText = (String) Objects.requireNonNull(task.getResult()).child("itemName").getValue();
                String descText = (String) task.getResult().child("itemDesc").getValue();
                String quantityText = (String) task.getResult().child("quantity").getValue();
                String qualityRating = (String) task.getResult().child("quality").getValue();
                String categoryText = (String) task.getResult().child("category").getValue();
                boolean isOfferingPost = Objects.equals(task.getResult().child("postType").getValue(), "Offering");
                imagePath = (String) task.getResult().child("imagePath").getValue();

                // Find out which position the category is on the list
                List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_array));
                int index = categories.indexOf(categoryText);

                // Set the text fields to the database values
                name.setText(nameText);
                desc.setText(descText);
                quantity.setText(quantityText);
                assert qualityRating != null;
                quality.setRating(Float.parseFloat(qualityRating));
                category.setSelection(index);
                type.setChecked(isOfferingPost);

                if (!imagePath.equals("N/A")) {
                    displayImage();
                } else {
                    imageButton.setImageResource(R.drawable.yourpost_ic);
                }
            }
        });
    }

    /**
     * Switches UI to the homepage when called
     */
    protected void goToHomepage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        startActivity(intent);
    }

    private void getPageLayout() {
        submitButton = findViewById(R.id.submitPostButton);
        imageButton = findViewById(R.id.imageButton);
        name = findViewById(R.id.itemName);
        desc = findViewById(R.id.itemDesc);
        quantity = findViewById(R.id.itemQuantity);
        quality = findViewById(R.id.itemQuality);
        category = findViewById(R.id.categorySpinner);
        type = findViewById(R.id.offeringWanting);

        BottomNavigationView bnv = findViewById(R.id.bottomMenu);
        bnv.setSelectedItemId(R.id.menuitems);
    }

    private void getPostById(long postId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            readData(postId);
        }
    }

    // Gets the values put in the text boxes
    private String getItemName() {
        return name.getText().toString();
    }

    private String getItemDesc() {
        return desc.getText().toString();
    }

    private String getItemQuantity() {
        return quantity.getText().toString();
    }

    private String getItemQuality() {
        return String.valueOf(quality.getRating());
    }

    private String getCategory() {
        return category.getSelectedItem().toString();
    }

    private String getPostType() {
        return type.isChecked() ? "Offering" : "Wanting";
    }

    // Displays the given post onto the image button
    private void displayImage() {
        StorageReference imgRef = storage.getReference();
        String[] path = imagePath.split("/");

        for (String s : path) {
            imgRef = imgRef.child(s);
        }

        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
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

            StorageReference fileRef = directoryRef.child(String.valueOf(postId));
            UploadTask uploadTask = fileRef.putFile(file);

            uploadTask.addOnSuccessListener(v -> displayImage());

            return fileRef;
        }
        return null;
    }

    /**
     * Updates the class' fileReference and filePath given the inputs are validated
     *
     * @param data
     * @param requestCode
     * @param resultCode
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            fileReference = uploadFile(filePath, location);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton) {
            chooseImageFromGallery("PostImg");
        } else if (v.getId() == R.id.submitPostButton) {
            boolean valid = PostVerification.isValidPost(getItemName(), getItemDesc(), getItemQuantity(), getItemQuality(), getCategory());
            if (valid) {
                savePostChanges();
                goToHomepage();
            } else {
                String errorMessage = PostVerification.getErrorMessage();
                Toast.makeText(EditPostActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Glorified setter that updates firebase with the object's local variables
     */
    protected void savePostChanges() {
        //This DatabaseReference is the current user object
        DatabaseReference post = postRef.child(String.valueOf(postId));

        //Update the Database w/ what is currently in the text fields
        post.child("itemName").setValue(getItemName());
        post.child("itemDesc").setValue(getItemDesc());
        post.child("quantity").setValue(getItemQuantity());
        post.child("quality").setValue(getItemQuality());
        post.child("category").setValue(getCategory());
        post.child("postType").setValue(getPostType());
        post.child("imagePath").setValue(fileReference != null ? "PostImg/" + postId : imagePath);
    }
}
