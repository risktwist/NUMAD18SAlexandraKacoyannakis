package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Error extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ConstraintLayout constraintLayout = findViewById(R.id.error);

        String testLabSetting =
                Settings.System.getString(getApplicationContext().getContentResolver(), "firebase.test.lab");
        if (testLabSetting != null && "true".equals(testLabSetting)) {
            throw new RuntimeException("program has crashed");
        } else {
            TextView view = new TextView(this);
            view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
            view.setText("Please use this feature in a test mode.");
            view.setTextColor(getResources().getColor(R.color.error));
            view.setTextSize(30);
            view.setGravity(Gravity.CENTER);
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            constraintLayout.addView(view);
        }

    }
}
