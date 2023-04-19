package ca.dal.cs.onlinebartertrader.MessagePages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import ca.dal.cs.onlinebartertrader.PostPages.OtherUserPostsActivity;
import ca.dal.cs.onlinebartertrader.PostPages.Post;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;
import ca.dal.cs.onlinebartertrader.UserLocation;

/**
 * Contains logic for a given chat window
 */
public class ChatActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout2;
    TextView chatterName;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference;
    ImageButton messagesButton;
    Button userPostsButton;
    Button openGoogleMaps;
    String chatWith;
    String locationOfChatWith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getPageLayout();
        Firebase.setAndroidContext(this);

        //Get the usernames of the two users chatting
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        chatWith = extras.getString("chatWith");
        chatterName.setText(chatWith);
        getOtherLocation(chatWith);
        //Get the database name of the chat (username_username)
        String chatRef = ChatLogic.getChatRef(username, chatWith);

        //Reference to Chat firebase url
        reference = new Firebase("https://online-barter-trader-89a92-default-rtdb.firebaseio.com/Messages/" + chatRef);

        messagesButton.setOnClickListener(v -> switchToMessagesPage());
        userPostsButton.setOnClickListener(v -> switchToUserPostsPage());
        sendButton.setOnClickListener(v -> sendMessage(username));
        openGoogleMaps.setOnClickListener(v-> openGoogleMaps());

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(username)) {
                    addMessageBox(message, 1);
                } else {
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /*
                // Unused
                 */
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*
                // Unused
                 */
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                /*
                // Unused
                 */
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                /*
                // Unused
                 */
            }
        });
    }

    private void getPageLayout() {
        layout = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        chatterName = findViewById(R.id.chatterName);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        messagesButton = findViewById(R.id.messages);
        userPostsButton = findViewById(R.id.userPosts);
        openGoogleMaps = findViewById(R.id.openMaps);
    }

    private void sendMessage(String username) {
        String messageText = messageArea.getText().toString();
        if (!messageText.equals("")) {
            Message m = new Message(messageText, username);

            //Update the Database w/ what is currently in the text fields
            reference.child(String.valueOf(System.currentTimeMillis())).setValue(m);
            messageArea.setText("");

            //Adds the currently sent message to the read message list to avoid badge notification
            SharedPreference.setPrefRead(this, SharedPreference.getRead(this) + 1);
        }
    }

    private void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void switchToMessagesPage() {
        Intent i = new Intent(ChatActivity.this, MessagesActivity.class);
        startActivity(i);
    }

    private void switchToUserPostsPage() {
        Intent intent = new Intent(this, OtherUserPostsActivity.class);
        intent.putExtra("ca.dal.cs.onlinebartertrader.username", chatWith);
        startActivity(intent);
    }

    public void getOtherLocation(String chatWith) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String currUser = "";

                for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()) {

                    currUser = ds.getValue(Post.class).getSellerUsername();


                    if (currUser.equals(chatWith)) {
                        ArrayList<Double> location = ds.getValue(Post.class).getLocation();
                        locationOfChatWith =  location.get(0).toString() + "," + location.get(1);
                        UserLocation.setLocationAccessor(locationOfChatWith);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded") //TODO Add button for this function
    private void openGoogleMaps() {
        if (locationOfChatWith != null) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+locationOfChatWith);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
        }

    }


}