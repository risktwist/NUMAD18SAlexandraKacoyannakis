package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle(R.string.my_name);

        String phoneId = "";

        TextView phoneIdView = findViewById(R.id.phone_id);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            phoneId = telephonyManager.getDeviceId();
        } else {
            phoneId = "Accessing the device id is not permitted.";
        }
        phoneIdView.setText("IMEI: "+ phoneId);
        phoneIdView.setTextSize(18);
    }
}
