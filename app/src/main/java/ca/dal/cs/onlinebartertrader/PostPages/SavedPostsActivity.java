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
import ca.dal.cs.onlinebartertrader.SharedPreference;
import ca.dal.cs.onlinebartertrader.StartActivity;

public class SavedPostsActivity extends AppCompatActivity {
    private static DatabaseReference postRef = null;
    private static DatabaseReference userRef = null;
    private FirebaseStorage storage;
    private ArrayList<Post> savedPosts = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedposts);

        FirebaseAuth.getInstance().signInAnonymously();

        BottomNavigationView bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setSelectedItemId(R.id.menusaved);

        new NavigationHandler(bottomMenu);

        initializeDatabase();

        readPosts();
    }

    private void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        userRef = database.getReference("Users");
        postRef = database.getReference("Posts");
    }

    protected void readPosts(){
        savedPosts = new ArrayList<>();

        // Gets all posts saved by the currently logged in user and displays them
        Query query = userRef.child(SharedPreference.getUsername(this)).child("savedPosts");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    int index = getPostIndexById(Long.parseLong(Objects.requireNonNull(userSnapshot.getKey())));
                    Post post = StartActivity.posts.get(index);
                    savedPosts.add(post);
                }

                // After each post has been added to the array, display them
                displayUserPosts();

                // Remove the event listener to fix a memory leak issue
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("RtlHardcoded")
    private void displayUserPosts() {
        if(!savedPosts.isEmpty()) {
            TextView noPostsText = findViewById(R.id.noPosts);
            noPostsText.setVisibility(View.GONE);

            TableLayout tableLayout = findViewById(R.id.postTable);
            Context ctx = this;

            // Loop to create a new TableRow for each of the user's posts
            for(int i = 0; i < savedPosts.size(); i++) {
                Post post = savedPosts.get(i);
                TableRow tableRow = new TableRow(this);
                tableRow.setPadding(0,0,0,40);
                tableRow.setBackgroundColor(getResources().getColor(R.color.grey));

                // Finds the post by the given ID, then adds a click listener that brings it to the post
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
                imgView.setPadding(20,20,20,20);

                if(!post.getImagePath().equals("N/A")) {
                    insertImage(post, imgView);
                }

                textView.setText(savedPosts.get(i).getItemName());

                // Add button layout to vertical layout
                verticalLayout.addView(buttonLayout);

                tableRow.addView(imgView);
                tableRow.addView(verticalLayout);

                tableLayout.addView(tableRow);
            }
        }
    }

    private void insertImage(Post post, ImageView imgView) {
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
                        .override(200,200)
                        .into(imgView));
    }

    private int getPostIndexById(long postId) {
        int index = -1;
        for(int i = 0; i < StartActivity.posts.size(); i++) {
            if(StartActivity.posts.get(i).getPostId() == postId) {
                index = i;
                break;
            }
        }
        return index;
    }
}
