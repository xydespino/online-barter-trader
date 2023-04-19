package ca.dal.cs.onlinebartertrader.PostPages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import ca.dal.cs.onlinebartertrader.StartActivity;

/**
 * Contains logic for displaying other user's posts
 */
public class OtherUserPostsActivity extends AppCompatActivity {
    private static DatabaseReference postRef = null;
    private FirebaseStorage storage;
    private ArrayList<Post> userPosts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherusersposts);

        FirebaseAuth.getInstance().signInAnonymously();

        // Get the extras sent from the MyPostsActivity containing the Post ID
        String username;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                username = "";
            } else {
                username = extras.getString("ca.dal.cs.onlinebartertrader.username");
            }
        } else {
            username = savedInstanceState.getString("ca.dal.cs.onlinebartertrader.username");
        }

        BottomNavigationView bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setSelectedItemId(R.id.menuitems);

        new NavigationHandler(bottomMenu);

        initializeDatabase();

        readPosts(username);
    }

    private void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        postRef = database.getReference("Posts");
    }

    /**
     * Gets all posts created by a and displays them
     *
     * @param username username for whom's posts will be read
     */
    protected void readPosts(String username) {
        userPosts = new ArrayList<>();

        Query query = postRef.orderByChild("sellerUsername").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (postSnapshot.child("status").getValue().toString().equals("Active")) {
                        Post post = new Post();
                        post.setImagePath(Objects.requireNonNull(postSnapshot.child("imagePath").getValue()).toString());
                        post.setItemName(Objects.requireNonNull(postSnapshot.child("itemName").getValue()).toString());
                        post.setPostType(Objects.requireNonNull(postSnapshot.child("postType").getValue()).toString());
                        post.setPostId(Long.parseLong(Objects.requireNonNull(postSnapshot.getKey())));
                        post.setStatus(Objects.requireNonNull(postSnapshot.child("status").getValue()).toString());


                        userPosts.add(post);
                    }
                }


                // After each post has been added to the array, display them
                displayUserPosts();

                // Remove the event listener to fix a memory leak issue
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                /*
                Unused
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
            Context ctx = this;

            // Loop to create a new TableRow for each of the user's posts
            for (int i = 0; i < userPosts.size(); i++) {
                Post post = userPosts.get(i);
                TableRow tableRow = new TableRow(this);
                tableRow.setPadding(0, 0, 0, 40);
                tableRow.setBackgroundColor(getResources().getColor(R.color.grey));


                int index = getPostIndexById(post.getPostId());
                tableRow.setOnClickListener(v -> {
                    Intent intent = new Intent(ctx, ViewPostActivity.class);
                    intent.putExtra("postIndexUser", index);
                    intent.putExtra("postIndexSearch", -1);
                    startActivity(intent);
                });

                LinearLayout verticalLayout = new LinearLayout(this);
                verticalLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout buttonLayout = new LinearLayout(this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Creating new elements for the TableRow
                ImageView imgView = new ImageView(this);
                TextView textView = new TextView(this);

                verticalLayout.addView(textView);

                // Set the placeholder image
                imgView.setImageResource(R.drawable.yourpost_ic);
                imgView.setPadding(20, 20, 20, 20);

                if (!post.getImagePath().equals("N/A")) {
                    StorageReference imgRef = storage.getReference();
                    String[] path = post.getImagePath().split("/");

                    // Use the path from Firebase to find the image
                    for (String s : path) {
                        imgRef = imgRef.child(s);
                    }

                    // Using Glide to download the image into the image view
                    imgRef.getDownloadUrl().addOnSuccessListener(uri ->
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .placeholder(R.drawable.yourpost_ic)
                                    .circleCrop()
                                    .override(200, 200)
                                    .into(imgView));
                }

                textView.setText(userPosts.get(i).getItemName());

                // Add button layout to vertical layout
                verticalLayout.addView(buttonLayout);

                tableRow.addView(imgView);
                tableRow.addView(verticalLayout);

                tableLayout.addView(tableRow);
            }
        }
    }

    private int getPostIndexById(long postId) {
        int index = -1;
        for (int i = 0; i < StartActivity.posts.size(); i++) {
            if (StartActivity.posts.get(i).getPostId() == postId) {
                index = i;
                break;
            }
        }
        return index;
    }
}
