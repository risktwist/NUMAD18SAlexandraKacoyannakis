package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;


public class ControlFragment extends Fragment {

    CountDownTimer timer;
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
        View pause = rootView.findViewById((R.id.button_pause));
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).onPause();
            }

        });

        //initialize phase
        final TextView phaseView = rootView.findViewById(R.id.phase);
        phaseView.setText(R.string.phase_1);

        //acknowledgements button
        final Button acknowledgement = rootView.findViewById(R.id.game_acknowlegements);
        acknowledgement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GameAcknowledgements.class);
                getActivity().startActivity(intent);
            }
        });

        //score
        final TextView scoreView = rootView.findViewById(R.id.score);
        scoreView.setText(getString(R.string.score) + " 0");

        //create timer with phase 1 of 1.5 minutes
        final TextView timerView = rootView.findViewById(R.id.timer);
        timer = new CountDownTimer(90000, 1000) {
            public void onTick(long timeToFinished) {
                timerView.setText(""+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(timeToFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(timeToFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(timeToFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(timeToFinished))));
            }

            public void onFinish() {
                ((GameActivity) getActivity()).beginPhase2();
            }
        }.start();

        return rootView;
    }

    public void resetTimer() {

        timer.cancel();
        initalizeTimer();
    }

    public void startPhase2Timer() {
        timer.cancel();

        //update phase text to phase 2
        final TextView phaseView = getView().findViewById(R.id.phase);
        phaseView.setText(R.string.phase_2);
        initalizeTimer();

    }

    private void initalizeTimer() {

        final TextView timerView = getView().findViewById(R.id.timer);

        timer = new CountDownTimer(90000, 1000) {
            public void onTick(long timeToFinished) {
                timerView.setText(""+String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(timeToFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(timeToFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(timeToFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(timeToFinished))));
            }

            public void onFinish() {
                ((GameActivity) getActivity()).stopGame();
            }
        }.start();
    }
}
