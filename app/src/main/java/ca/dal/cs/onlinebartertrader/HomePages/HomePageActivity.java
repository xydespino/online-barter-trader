package ca.dal.cs.onlinebartertrader.HomePages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import ca.dal.cs.onlinebartertrader.MessagePages.MessagesActivity;
import ca.dal.cs.onlinebartertrader.PostPages.Post;
import ca.dal.cs.onlinebartertrader.ProfilePages.ProfileActivity;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;
import ca.dal.cs.onlinebartertrader.StartActivity;
import ca.dal.cs.onlinebartertrader.ViewHolder.itemPostViewHolder;

/**
 * Contains logic for the home page
 */

public class HomePageActivity extends AppCompatActivity {
    private DatabaseReference PostRef;
    private FirebaseStorage storage;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private String errorMessage = "";
    private ImageButton searchButton;
    private ImageButton logoutButton;
    private ImageButton profileButton;
    private Spinner spinner;
    private BottomNavigationView bottomMenu;
    public static ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        SharedPreference.setPrefCurrent(this, 0);
        createHomePageLayout();

        buttonInitialization();

        initializeDatabase();
        storage = FirebaseStorage.getInstance();
        //Displaying content on Homepage
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetchPost();


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void createHomePageLayout() {
        bottomMenu = findViewById(R.id.bottomMenu);
        MessagesActivity.checkForUpdate(this, bottomMenu);

        searchButton = findViewById(R.id.searchButton);
        logoutButton = findViewById(R.id.logoutButton);
        profileButton = findViewById(R.id.profileMenu);

        spinner = findViewById(R.id.catListHome);
        spinner.setSelection(getCatPos());

        bottomMenu.setSelectedItemId(R.id.menuhome);


    }

    private void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        PostRef = database.getReference("Posts");
    }

    private void buttonInitialization() {
        searchButton.setOnClickListener(v -> goToSearchpage());
        profileButton.setOnClickListener(v -> goToProfilePage());

        logoutButton.setOnClickListener(v -> {
            SharedPreference.clearUserName(this);
            goToLoginPage();
        });
        new NavigationHandler(bottomMenu);
    }


    protected int getCatPos() {
        int in = 0;
        String[] cats = getResources().getStringArray(R.array.categories_array);
        for (int i = 0; i < cats.length; i++) {
            if (SharedPreference.getCategory(this).equals(cats[i])) {
                in = i;
            }
        }
        return in;
    }

    /**
     * Switches UI to the search page when called
     */
    protected void goToSearchpage() {
        if (!getSearch().isEmpty()) {
            if (!SearchLogic.isValidSearch(getSearch()).isEmpty()) {
                errorMessage = SearchLogic.isValidSearch(getSearch());
                setStatusMessage(errorMessage);
            } else {
                Intent intent = new Intent(this, SearchPageActivity.class);
                intent.putExtra("search", getSearch());
                intent.putExtra("cat", spinner.getSelectedItem().toString());
                startActivity(intent);
            }
        } else if (!spinner.getSelectedItem().toString().equals("Select Category")) {
            Intent intent = new Intent(this, SearchPageActivity.class);
            intent.putExtra("search", "");
            intent.putExtra("cat", spinner.getSelectedItem().toString());
            startActivity(intent);
        } else {
            errorMessage = "Empty Search!";
            setStatusMessage(errorMessage);
        }
    }

    /**
     * Switches to login page when called
     */
    protected void goToLoginPage() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Switches to profile page page when called
     */
    protected void goToProfilePage() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Retrieves input search text
     *
     * @return returns the text the user input into the search bar
     */
    protected String getSearch() {
        EditText searchbar = findViewById(R.id.searchText);
        return searchbar.getText().toString();
    }

    /**
     * Checks if search input is greater than 100 characters
     *
     * @return returns true if the length is greater than 100 characters
     */
    protected boolean checkCharacterAmount() {
        return getSearch().length() > 100;
    }

    /**
     * Sets the status label in case of error
     */
    protected void setStatusMessage(String message) {
        TextView statusLabel = findViewById(R.id.statusLabel2);
        statusLabel.setText(message);
    }


    private void fetchPost() {
        Query query = PostRef.orderByChild("status").equalTo("Active");


        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>().setQuery(query, new SnapshotParser<Post>() {
                    @NonNull
                    @Override
                    public Post parseSnapshot(@NonNull DataSnapshot snapshot) {
                        Post post = new Post();
                        post.setDistanceToUser(DistanceCalculator.calculateDistances((ArrayList<Double>) snapshot.child("location").getValue()));
                        post.setItemName(snapshot.child("itemName").getValue().toString());
                        post.setItemDesc(snapshot.child("itemDesc").getValue().toString());
                        post.setPostType(snapshot.child("postType").getValue().toString());
                        post.setPostId(Long.parseLong(Objects.requireNonNull(snapshot.getKey())));
                        post.setImagePath(snapshot.child("imagePath").getValue().toString());
                        return (post);
                    }
                })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Post, itemPostViewHolder>(options) {
            @Override
            public itemPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_preview, parent, false);

                return new itemPostViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull itemPostViewHolder holder, final int position, Post model) {
                holder.setTxtItemName(model.getItemName());
                holder.setTxtDesc(model.getItemDesc());
                holder.setDistance(model.getDistanceToUser());

                if (!model.getImagePath().equals("N/A")) {
                    StorageReference imgRef = storage.getReference();
                    String[] path = model.getImagePath().split("/");

                    for (String s : path) {
                        imgRef = imgRef.child(s);
                    }

                    imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(holder.itemView.getContext())
                                .load(uri)
                                .override(200, 200)
                                .into(holder.imageView);
                    });


                }


                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(HomePageActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }


}