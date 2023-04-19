package ca.dal.cs.onlinebartertrader.ProfilePages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.dal.cs.onlinebartertrader.R;
import ca.dal.cs.onlinebartertrader.SharedPreference;

/**
 * Contains logic for maintaining user's preferences
 */
public class PreferenceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String errorMessage = "";
    int distance;
    String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        distance = SharedPreference.getDistance(this);
        EditText distanceField = findViewById(R.id.distanceText);
        distanceField.setText("" + distance);

        Spinner spinner = findViewById(R.id.catListPreference);
        spinner.setOnItemSelectedListener(this);

        Button save = findViewById(R.id.savePref);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });


    }

    //setters
    protected void setStatusMessage(String message) {
        TextView statusLabel = findViewById(R.id.statusLabel3);
        statusLabel.setText(message);
    }

    protected void savePreferences() {
        if (checkDistance()) {
            SharedPreference.setCategory(this, category);
            SharedPreference.setDistance(this, distance);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String item = parent.getItemAtPosition(pos).toString();
        if (item.equals("Select Category")) {
            Toast.makeText(PreferenceActivity.this, "No Category Selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PreferenceActivity.this, item + " Selected", Toast.LENGTH_SHORT).show();
        }
        category = item;
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Verifies that the distance chosen does not exceed the maximum distance.
     *
     * @return Boolean indicating whether the distance selected was valid
     */
    public boolean checkDistance() {
        EditText distanceField = findViewById(R.id.distanceText);
        if (distanceField.getText().toString().isEmpty()) {
            errorMessage = "Must have distance set!";
            setStatusMessage(errorMessage);
            return false;
        } else {
            int dist = Integer.parseInt(distanceField.getText().toString());
            if (dist > 1000) {
                errorMessage = "Distance Greater then 1000 Km!";
                setStatusMessage(errorMessage);
                return false;
            } else {
                distance = dist;
                return true;
            }
        }
    }

}
