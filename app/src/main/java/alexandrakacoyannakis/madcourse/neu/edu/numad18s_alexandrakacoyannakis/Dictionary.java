package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Dictionary extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    InputStream inputStream = null;
    BufferedReader reader = null;
    ArrayList<String> matches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        mMediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI );
        try {
            inputStream = getAssets().open("wordlist.txt");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                matches.add(line);
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("message: ",e.getMessage());
        }


        //edit text
        final EditText inputText = findViewById(R.id.enter_word);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //check should only occur when the string is 3 characters or more
                if (charSequence.length() >= 3) {
                    mMediaPlayer.start();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //text view
        final TextView resultsView = findViewById(R.id.entries);

        //clear button
        final Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputText.setText("");
                resultsView.setText("");
            }
        });

        //acknowledgements button
        final Button acknowledgementsButton = findViewById(R.id.acknowledgements_button);
        acknowledgementsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }
}
