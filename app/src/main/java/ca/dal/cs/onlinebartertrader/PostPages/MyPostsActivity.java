package ca.dal.cs.onlinebartertrader.PostPages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import ca.dal.cs.onlinebartertrader.HomePages.NavigationHandler;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;

/**
 * Contains logic for viewing the currently logged in user's posts
 */

public class MyPostsActivity extends AppCompatActivity implements View.OnClickListener {
    private static DatabaseReference postRef = null;
    private FirebaseStorage storage;
    private ArrayList<Post> userPosts = null;
    private ToggleButton showInactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts);

        FirebaseAuth.getInstance().signInAnonymously();

        BottomNavigationView bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setSelectedItemId(R.id.menuitems);

        new NavigationHandler(bottomMenu);
        showInactive = findViewById(R.id.showInactiveButton);
        showInactive.setOnClickListener(this);

        initializeDatabase();

        readPosts();
    }

    private void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        postRef = database.getReference("Posts");
    }

    /**
     * Switches UI to the editing feature when called
     */
    protected void goToEditPost(long postId) {
        Intent intent = new Intent(this, EditPostActivity.class);
        intent.putExtra("ca.dal.cs.onlinebartertrader.postId", postId);
        startActivity(intent);
    }

    /**
     * Gets all posts created by the currently logged in user and displays them
     */
    protected void readPosts() {
        userPosts = new ArrayList<>();

        Query query = postRef.orderByChild("sellerUsername").equalTo(SharedPreference.getUsername(this));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = new Post();
                    post.setImagePath(Objects.requireNonNull(postSnapshot.child("imagePath").getValue()).toString());
                    post.setItemName(Objects.requireNonNull(postSnapshot.child("itemName").getValue()).toString());
                    post.setPostType(Objects.requireNonNull(postSnapshot.child("postType").getValue()).toString());
                    post.setPostId(Long.parseLong(Objects.requireNonNull(postSnapshot.getKey())));
                    post.setStatus(Objects.requireNonNull(postSnapshot.child("status").getValue()).toString());


                    if (post.getStatus().equals("Active")) {
                        userPosts.add(post);
                    } else if (showInactive.isChecked()) {
                        userPosts.add(post);
                    }
                }

                displayUserPosts();

                // Remove the event listener to fix a memory leak issue
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                /*
                // Unused
                 */
            }
        });
    }

    @SuppressLint("RtlHardcoded")
    private void displayUserPosts() {
        if (!userPosts.isEmpty()) {
            TextView noPostsText = findViewById(R.id.noPosts);
            noPostsText.setVisibility(View.GONE);

            TableLayout tableLayout = findViewById(R.id.postTable);

            // Loop to create a new TableRow for each of the user's posts
            for (int i = 0; i < userPosts.size(); i++) {
                Post post = userPosts.get(i);
                TableRow tableRow = new TableRow(this);
                tableRow.setPadding(0, 0, 0, 40);

                LinearLayout verticalLayout = new LinearLayout(this);
                verticalLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout buttonLayout = new LinearLayout(this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Creating new elements for the TableRow
                ImageView imgView = new ImageView(this);
                TextView textView = new TextView(this);
                Button editPost = new Button(this);
                Button markTraded = new Button(this);

                verticalLayout.addView(textView);

                // Set the placeholder image
                imgView.setImageResource(R.drawable.yourpost_ic);

                if (!post.getImagePath().equals("N/A")) {
                    StorageReference imgRef = storage.getReference();
                    String[] path = post.getImagePath().split("/");

                    // Use the path from Firebase to find the image
                    for (String s : path) {
                        imgRef = imgRef.child(s);
                    }

                    // Using Glide to download the image into the image view
                    imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .placeholder(R.drawable.yourpost_ic)
                                .circleCrop()
                                .override(200, 200)
                                .into(imgView);
                    });
                }

                textView.setText(userPosts.get(i).getItemName());


                // Init edit post button
                editPost.setText(R.string.edit_post);
                editPost.setOnClickListener(v -> goToEditPost(post.getPostId()));

                // Init mark traded button
                markTraded.setText(post.getStatus().equals("Active") ? R.string.mark_as_traded : R.string.mark_as_available);
                markTraded.setOnClickListener(v -> markAsTraded(post.getPostId(), post.getStatus()));

                // Add buttons to button layout
                buttonLayout.addView(editPost);
                buttonLayout.addView(markTraded);

                // Add button layout to vertical layout
                verticalLayout.addView(buttonLayout);

                tableRow.addView(imgView);
                tableRow.addView(verticalLayout);

                tableLayout.addView(tableRow);
            }
        }
    }

    private void markAsTraded(long postId, String currentStatus) {
        DatabaseReference post = postRef.child(String.valueOf(postId));

        switch (currentStatus) {
            case "Active":
                post.child("status").setValue("Traded");
                break;
            case "Traded":
                post.child("status").setValue("Active");
                break;
        }

        this.recreate();
    }

    private void clearTable() {
        TableLayout table = findViewById(R.id.postTable);
        table.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        clearTable();
        readPosts();
        displayUserPosts();
    }
}
