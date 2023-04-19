package ca.dal.cs.onlinebartertrader.PostPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.dal.cs.onlinebartertrader.HomePages.SearchPageActivity;
import ca.dal.cs.onlinebartertrader.MessagePages.ChatActivity;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;
import ca.dal.cs.onlinebartertrader.StartActivity;

/**
 * Contains logic for viewing a post
 */
public class ViewPostActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 1;
    static DatabaseReference postRef = null;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = null;
    StorageReference fileReference;
    private int postID;
    private Post post;
    private TextView username;
    private ImageView postImage;
    private TextView itemName;
    private RatingBar quality;
    private TextView description;
    private String imagePath;
    private boolean isSaved;
    ImageView saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item);

        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        if (bun.getInt("postIndexSearch") == -1) {
            postID = bun.getInt("postIndexUser");
            getPostByIdLogin(postID);
        } else {
            postID = bun.getInt("postIndexSearch");
            getPostByIdSearch(postID);
        }

        initializeDatabase();
        getPageLayout();

        displayPost();

        ImageView messageButton = findViewById(R.id.message);
        messageButton.setOnClickListener(v -> chatWithPoster());

        saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(v -> savePost());
    }

    protected void initializeDatabase() {
        //initialize your database and related fields here
        database = FirebaseDatabase.getInstance();
        postRef = database.getReference("Posts");
    }

    private void getPageLayout() {
        username = findViewById(R.id.username);
        description = findViewById(R.id.description);
        quality = findViewById(R.id.itemQuality);
        itemName = findViewById(R.id.itemName);
        postImage = findViewById(R.id.postImage);
    }

    private void displayPost(){
        // Set the text fields to the database values
        username.setText(post.getSellerUsername());
        itemName.setText(post.getItemName());
        description.setText(post.getItemDesc());
        quality.setRating(Float.parseFloat(post.getQuality()));
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
                        .into(postImage);
            });
        }

        isPostSaved();
    }

    private void getPostByIdSearch(int postId) {
        post = SearchPageActivity.posts.get(postId);
    }

    private void getPostByIdLogin(int postId) {
        post = StartActivity.posts.get(postId);
    }

    private String getUsername() {
        return username.getText().toString().trim();
    }

    private void isPostSaved() {
        isSaved = false;

        DatabaseReference userRef = database.getReference("Users/" + SharedPreference.getUsername(this)).child("savedPosts");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    assert key != null;
                    if(key.equals(String.valueOf(post.getPostId()))) {
                        isSaved = true;
                    }
                }

                if(isSaved) {
                    saveButton.setImageResource(R.drawable.saved_ic);
                } else {
                    saveButton.setImageResource(R.drawable.save_ic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void savePost() {
        DatabaseReference userRef = database.getReference("Users/" + SharedPreference.getUsername(this)).child("savedPosts");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If the post is already saved, remove it from the database
                if (isSaved) {
                    snapshot.child(String.valueOf(post.getPostId())).getRef().removeValue();
                    isSaved = false;
                    saveButton.setImageResource(R.drawable.save_ic);
                // If the post has not already been saved, push it to the database
                } else {
                    snapshot.child(String.valueOf(post.getPostId())).getRef().setValue(post.getItemName());
                    isSaved = true;
                    saveButton.setImageResource(R.drawable.saved_ic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void chatWithPoster(){
        String user = SharedPreference.getUsername(this);
        String poster = getUsername();

        if (user.equals(poster)) {
            Toast.makeText(ViewPostActivity.this, "Cannot chat with self!", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(ViewPostActivity.this, ChatActivity.class);
            i.putExtra("username", user);
            i.putExtra("chatWith", poster);
            startActivity(i);
        }
    }

}
