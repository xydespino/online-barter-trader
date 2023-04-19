package ca.dal.cs.onlinebartertrader.HomePages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ca.dal.cs.onlinebartertrader.PostPages.Post;
import ca.dal.cs.onlinebartertrader.PostPages.ViewPostActivity;
import ca.dal.cs.onlinebartertrader.R;

/**
 * Contains logic for the search page
 */
public class SearchPageActivity extends AppCompatActivity {

    String search;
    String category;
    private FirebaseStorage storage;
    Spinner spinner;
    boolean check;

    /**
     * ArrayList to store posts to be shown
     */
    public static ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        Bundle bun = intent.getExtras();
        //Get search string and category from homepage
        search = bun.getString("search");
        category = bun.getString("cat");
        TextView searchText = findViewById(R.id.searchedText);
        searchText.setText(search);
        spinner = findViewById(R.id.catList);
        spinner.setSelection(getCatPos());
        //bottom menu
        BottomNavigationView bottomMenu = findViewById(R.id.bottomMenu);
        new NavigationHandler(bottomMenu);

        checkForItem();

        Button searchButtonSP = findViewById(R.id.searchButtonSP);
        searchButtonSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForItem();
            }
        });

    }

    /**
     * Returns the index of a selected category
     *
     * @return returns the index of a selected category
     */
    protected int getCatPos() {
        int in = 0;
        String[] cats = getResources().getStringArray(R.array.categories_array);
        for (int i = 0; i < cats.length; i++) {
            if (category.equals(cats[i])) {
                in = i;
            }
        }
        return in;
    }

    /**
     * Searches for item using search bar on the search page
     */
    public void searchForItem() {
        EditText searchbarSP = findViewById(R.id.searchBarSP);
        search = searchbarSP.getText().toString();
        TextView searchText = findViewById(R.id.searchedText);
        searchText.setText(search);
        if (!search.isEmpty()) {
            if (!SearchLogic.isValidSearch(search).isEmpty()) {
                setStatusMessage(SearchLogic.isValidSearch(search));
            } else {
                Intent intent = getIntent();
                intent.putExtra("search", search);
                intent.putExtra("cat", spinner.getSelectedItem().toString());
                startActivity(intent);
            }
        } else if (!spinner.getSelectedItem().toString().equals("Select Category")) {
            Intent intent = getIntent();
            intent.putExtra("search", "");
            intent.putExtra("cat", spinner.getSelectedItem().toString());
            startActivity(intent);
        } else {
            setStatusMessage("Empty Search!");
        }
    }

    /**
     * Calls to display post depending on search and category
     */
    public void checkForItem() {
        posts.clear();
        check = false;
        setStatusMessage("");
        if (search.isEmpty()) {
            check = SearchLogic.findItemCat(category);
            if (!check) {
                setStatusMessage("Item not found!");
            } else {
                displayUserPostsEmpty();
            }
        } else if (category.equals("Select Category")) {
            check = SearchLogic.findItemSearch();
            if (!check) {
                setStatusMessage("Item not found!");
            } else {
                displayUserPosts();
            }
        } else {
            check = SearchLogic.findItemBoth(category);
            if (!check) {
                setStatusMessage("Item not found!");
            } else {
                displayUserPostsWithCat();
            }
        }
    }

    protected void setStatusMessage(String message) {
        TextView statusLabel = findViewById(R.id.statusLabel4);
        statusLabel.setText(message);
    }

    /**
     * Starts new activity and pass it index of post to be shown
     *
     * @param postIndex index of the post
     */
    protected void goToPost(int postIndex) {
        Intent intent = new Intent(this, ViewPostActivity.class);
        intent.putExtra("postIndexSearch", postIndex);
        intent.putExtra("postIndexUser", -1);
        startActivity(intent);
    }

    //Display post only based on search
    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    private void displayUserPosts() {
        TableLayout tableLayout = findViewById(R.id.postTable);
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(10, 10, 10, 10);
            ImageView imgView = new ImageView(this);
            TextView textView = new TextView(this);
            Button button = new Button(this);
            imgView.setImageResource(R.drawable.yourpost_ic);
            if (!post.getImagePath().equals("N/A")) {
                StorageReference imgRef = storage.getReference();
                String[] path = post.getImagePath().split("/");

                for (String s : path) {
                    imgRef = imgRef.child(s);
                }

                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.yourpost_ic)
                            .circleCrop()
                            .override(200, 200)
                            .into(imgView);
                });


            }

            textView.setText(posts.get(i).getItemName());
            button.setText("View");
            int index = i;
            button.setOnClickListener(v -> {
                goToPost(index);
            });

            tableRow.addView(imgView);
            tableRow.addView(textView);
            tableRow.addView(button);
            tableLayout.addView(tableRow);

        }
    }

    //Display posts based only on category
    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    private void displayUserPostsEmpty() {
        TableLayout tableLayout = findViewById(R.id.postTable);
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(10, 10, 10, 10);
            ImageView imgView = new ImageView(this);
            TextView textView = new TextView(this);
            Button button = new Button(this);
            imgView.setImageResource(R.drawable.yourpost_ic);
            if (!post.getImagePath().equals("N/A")) {
                StorageReference imgRef = storage.getReference();
                String[] path = post.getImagePath().split("/");

                for (String s : path) {
                    imgRef = imgRef.child(s);
                }

                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.yourpost_ic)
                            .circleCrop()
                            .override(200, 200)
                            .into(imgView);
                });


            }

            textView.setText(posts.get(i).getItemName());
            button.setText("View");
            int index = i;
            button.setOnClickListener(v -> {
                goToPost(index);
            });

            tableRow.addView(imgView);
            tableRow.addView(textView);
            tableRow.addView(button);
            tableLayout.addView(tableRow);

        }
    }

    //Display posts based on both search and category
    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    private void displayUserPostsWithCat() {
        TableLayout tableLayout = findViewById(R.id.postTable);
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(10, 10, 10, 10);
            ImageView imgView = new ImageView(this);
            TextView textView = new TextView(this);
            Button button = new Button(this);
            imgView.setImageResource(R.drawable.yourpost_ic);
            if (!post.getImagePath().equals("N/A")) {
                StorageReference imgRef = storage.getReference();
                String[] path = post.getImagePath().split("/");

                for (String s : path) {
                    imgRef = imgRef.child(s);
                }

                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.yourpost_ic)
                            .circleCrop()
                            .override(200, 200)
                            .into(imgView);
                });


            }

            textView.setText(posts.get(i).getItemName());
            button.setText("View");
            int index = i;
            button.setOnClickListener(v -> {
                goToPost(index);
            });

            tableRow.addView(imgView);
            tableRow.addView(textView);
            tableRow.addView(button);
            tableLayout.addView(tableRow);

        }
    }
}

