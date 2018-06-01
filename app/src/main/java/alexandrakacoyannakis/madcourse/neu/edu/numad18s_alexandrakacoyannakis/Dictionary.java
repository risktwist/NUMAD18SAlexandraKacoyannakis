package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.net.Uri;
import android.net.rtp.AudioStream;
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

    InputStream inputStream = null;
    BufferedReader reader = null;
    ArrayList<String> words = new ArrayList<>(); //words from the text file
    Ringtone r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        final ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        try {
            Log.i("mediaPlayer", "media player has been created");


            inputStream = getAssets().open("wordlist.txt");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("message: ",e.getMessage());
        }

        //text view
        final TextView resultsView = findViewById(R.id.entries);

        //edit text
        final EditText inputText = findViewById(R.id.enter_word);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("onTextChanged", "entering onTextChanged");
                //check should only occur when the string is 3 characters or more
                if (charSequence.length() >= 3) {
                    //convert to lower case so that the matching can occur in dictionary
                    Log.i("onTextChanged", "text is greater than 3 characters");
                    if (words.contains(charSequence.toString().toLowerCase())) {
                        beep.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                        Log.i("onTextChanged", "match found " + charSequence.toString());
                        resultsView.append(charSequence +"\n");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                Intent intent = new Intent(v.getContext(), Acknowledgments.class);
                Dictionary.this.startActivity(intent);
            }
        });
    }
}
