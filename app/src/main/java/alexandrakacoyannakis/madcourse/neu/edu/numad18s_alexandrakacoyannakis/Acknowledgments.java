package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Acknowledgments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgments);

        final TextView view = findViewById(R.id.assign1ack);
        String bullet = getString(R.string.bullet);
        String assignment1Acknowledgments = Html.fromHtml(getString(R.string.assign1ack)).toString();
        view.setText(bullet + assignment1Acknowledgments);

        //separate text view needed for the hyperlinks to source
        final TextView viewSource1 = findViewById(R.id.assign1_source);
        viewSource1.setText(
                Html.fromHtml(
                        "<a href=\"https://firebase.google.com/docs/test-lab/android/android-studio\">https://firebase.google.com/docs/test-lab/android/android-studio</a> "));
        viewSource1.setMovementMethod(LinkMovementMethod.getInstance());

        final TextView view2 = findViewById(R.id.assign2ack);
        view2.setText(bullet + getString(R.string.assign2ack));

        //separate text view needed for the hyperlinks to source
        final TextView viewSource2 = findViewById(R.id.assign2_source);
        viewSource2.setText(
                Html.fromHtml("<a href=\"http://wordlist.sourceforge.net/\"> http://wordlist.sourceforge.net/</a>"));
        viewSource2.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
