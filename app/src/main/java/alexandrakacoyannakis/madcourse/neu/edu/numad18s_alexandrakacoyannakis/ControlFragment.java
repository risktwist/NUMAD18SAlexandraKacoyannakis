package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class ControlFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_control, container, false);
        View main = rootView.findViewById(R.id.button_main);
        View restart = rootView.findViewById(R.id.button_restart);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).restartGame();
            }
        });

        //create timer with default 3 minutes
        final TextView timerView = rootView.findViewById(R.id.timer);
        new CountDownTimer(180000, 1000) {
            public void onTick(long timeToFinished) {
                timerView.setText(""+String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(timeToFinished),
                        TimeUnit.MILLISECONDS.toMinutes(timeToFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(timeToFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(timeToFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(timeToFinished))));
            }

            public void onFinish() {
                timerView.setText("Time's Up!");
                ((GameActivity) getActivity()).stopGame();
            }
        }.start();


        return rootView;
    }
}
