package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                Settings.System.getString(this.getContentResolver(), "firebase.test.lab");
        if (testLabSetting != null && "true".equals(testLabSetting)) {
            throw new RuntimeException("program has crashed");
        } else {
            TextView view = new TextView(this);
            view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT));
            view.setText("Please use this feature in a test mode.");
            constraintLayout.addView(view);
        }

    }
}
