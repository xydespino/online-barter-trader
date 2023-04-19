package ca.dal.cs.onlinebartertrader.PostPages;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ca.dal.cs.onlinebartertrader.R;

/**
 * contains logic for viewing another user's posts
 */
public class UserPostActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_page);
    }

    @Override
    public void onClick(View v) {
        /*
        // Unused
        */
    }
}
