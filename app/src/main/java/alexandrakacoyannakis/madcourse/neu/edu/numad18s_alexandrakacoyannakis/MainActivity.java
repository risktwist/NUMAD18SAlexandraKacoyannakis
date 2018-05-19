package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.my_name);

        //about button
        final Button aboutButton = findViewById(R.id.about);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), About.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //error button
        final Button errorButton = findViewById(R.id.generate_error);
        errorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Error.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //version id
        final TextView versionIdView = findViewById(R.id.version_id);
        String versionId = "Version Id: " + BuildConfig.VERSION_CODE;
        versionIdView.setText(versionId);

        //version name
        final TextView versionNameView = findViewById(R.id.version_name);
        String versionName = "Version Name: " + BuildConfig.VERSION_NAME;
        versionNameView.setText(versionName);
    }
}
