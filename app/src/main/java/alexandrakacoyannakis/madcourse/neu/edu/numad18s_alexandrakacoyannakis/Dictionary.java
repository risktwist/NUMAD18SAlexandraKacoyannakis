package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Dictionary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        //clear button
        final Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText inputText = findViewById(R.id.enter_word);
                TextView resultsView = findViewById(R.id.entries);
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
