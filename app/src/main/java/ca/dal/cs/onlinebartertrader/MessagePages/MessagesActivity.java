package ca.dal.cs.onlinebartertrader.MessagePages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import android.content.Context;

import ca.dal.cs.onlinebartertrader.HomePages.NavigationHandler;
import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;

public class MessagesActivity extends AppCompatActivity {
    ListView usersList;
    TextView noUsersText;
    ArrayList<String> chatterList = new ArrayList<String>();
    private DatabaseReference UserRef;
    private DatabaseReference MessageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        //bottom menu
        BottomNavigationView bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.setSelectedItemId(R.id.menumessages);
        new NavigationHandler(bottomMenu);

        initializeDatabase();

        usersList = findViewById(R.id.usersList);
        noUsersText = findViewById(R.id.noUsersText);
        noUsersText.setVisibility(View.VISIBLE);
        usersList.setVisibility(View.GONE);

        updateChatterList(SharedPreference.getUsername(this), this);

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = SharedPreference.getUsername(MessagesActivity.this);
                String chatWith = chatterList.get(position);

                Intent i = new Intent(MessagesActivity.this, ChatActivity.class);
                i.putExtra("username", username);
                i.putExtra("chatWith", chatWith);
                startActivity(i);
            }
        });
    }

    private void initializeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        UserRef = database.getReference("Users");
        MessageRef = database.getReference("Messages");
    }

    /**
     * Searches the database for chats belonging to the current user. For each chat found, adds the
     * other user's username to a list to be displayed on the messages page.
     * @param username: Username of the current user
     * @param context: The current context
     */
    private void updateChatterList(String username, Context context) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String userRef = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userRef = ds.getKey();

                    String firstUser = userRef.substring(0, userRef.indexOf("_"));
                    String secondUser = userRef.substring(userRef.indexOf("_") + 1);

                    if(firstUser.equals(username)) {
                        chatterList.add(secondUser);

                    }
                    else if (secondUser.equals(username)){
                        chatterList.add(firstUser);
                    }

                    if(!chatterList.isEmpty()){
                        noUsersText.setVisibility(View.GONE);
                        usersList.setVisibility(View.VISIBLE);
                        usersList.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, chatterList));
                    }
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

    /**
     * Searches the database for chats belonging to the current user. For each chat node found, adds the
     * the number of that node's children to count; which is then differentiated against the user's
     * read messages to display the appropriate badge status
     * @param menu: BottomNavigationView where badge updates will happen over the messages Icon
     * @param context: Various SharedPreferences items are accessed through context
     */

    public static void checkForUpdate(Context context, BottomNavigationView menu) {
        final int[] count = {0};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String userRef = null;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userRef = ds.getKey();

                    String firstUser = userRef.substring(0, userRef.indexOf("_"));
                    String secondUser = userRef.substring(userRef.indexOf("_") + 1);

                    if(firstUser.equals(SharedPreference.getUsername(context)) || secondUser.equals(SharedPreference.getUsername(context))) {
                        count[0]+= ds.getChildrenCount();
                    }
                }

                if (count[0] == SharedPreference.getRead(context)){
                    menu.getOrCreateBadge(R.id.menumessages).setVisible(false);
                }
                else if (count[0] - SharedPreference.getRead(context) > 0) {
                    menu.getOrCreateBadge(R.id.menumessages).setVisible(true);
                    SharedPreference.setPrefCurrent(context,count[0] - SharedPreference.getRead(context));
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
}