package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class ControlFragment extends Fragment {

    private CountDownTimer timer;
    private long timeRemaining; //handle timer when paused

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

        //restart button
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).restartGame();
            }
        });

        //pause button
        View pause = rootView.findViewById((R.id.button_pause));
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if (button.getText().toString().equals("Pause")) {
                    ((GameActivity) getActivity()).onPause();
                } else {
                    ((GameActivity) getActivity()).onRestart();
                }

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

        //turn off music
        final Button turnOffMusic = rootView.findViewById(R.id.turn_off_music);
        turnOffMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).turnOffMusic();
            }
        });

        //score
        final TextView scoreView = rootView.findViewById(R.id.score);
        scoreView.setText(getString(R.string.score) + " 0");

        //create timer with phase 1 of 1.5 minutes
        final TextView timerView = rootView.findViewById(R.id.timer);
        timer = new CountDownTimer(90000, 1000) {
            public void onTick(long timeToFinished) {
                timeRemaining = timeToFinished;
                if (timeToFinished < 10000) {
                    timerView.setTextColor(getResources().getColor(R.color.red_color));
                } else {
                    timerView.setTextColor(getResources().getColor(R.color.white_color));
                }
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

    /**
     * When game is restarted, need to restart the timer
     */
    public void resetTimer() {

        timer.cancel();
        initializeTimer();
    }

    /**
     * start the timer by recreating it for phase 2
     */
    public void startPhase2Timer() {
        timer.cancel();

        //update phase text to phase 2
        final TextView phaseView = getView().findViewById(R.id.phase);
        phaseView.setText(R.string.phase_2);
        initializeTimer();

    }

    /**
     * create brand new timer
     */
    private void initializeTimer() {

        final TextView timerView = getView().findViewById(R.id.timer);

        timer = new CountDownTimer(90000, 1000) {
            public void onTick(long timeToFinished) {
                timeRemaining = timeToFinished;
                if (timeToFinished < 10000) {
                    timerView.setTextColor(getResources().getColor(R.color.red_color));
                } else {
                    timerView.setTextColor(getResources().getColor(R.color.white_color));
                }
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

    /**
     * pause timer when pause button is pressed
     * in GameActivity
     */
    public void pauseTimer() {
        timer.cancel();
    }

    /**
     * restart timer when game is reloaded
     */
    public void restartTimer() {
        final TextView timerView = getView().findViewById(R.id.timer);

        timer = new CountDownTimer(timeRemaining, 1000) {
            public void onTick(long timeToFinished) {
                timeRemaining = timeToFinished;

                //change text to red when timer is less than seconds from finishing
                if (timeToFinished < 10000) {
                    timerView.setTextColor(getResources().getColor(R.color.red_color));
                } else {
                    timerView.setTextColor(getResources().getColor(R.color.white_color));
                }

                //update text with time remaining
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

    public void updatePauseButton() {
        final Button pause = getView().findViewById(R.id.button_pause);
        pause.setText(R.string.continue_label);
    }

    public void updateRestartButton() {
        final Button restart = getView().findViewById(R.id.button_pause);
        restart.setText(R.string.pause_label);
    }

    public void updateScore(int score, String word) {
        final TextView scoreView = getView().findViewById(R.id.score);
        scoreView.setText(getString(R.string.score) + " " + score);

        final TextView wordsView = getView().findViewById(R.id.user_correct_words);
        String text = wordsView.getText().toString();
        wordsView.setText(text + " " + word);
    }
}
