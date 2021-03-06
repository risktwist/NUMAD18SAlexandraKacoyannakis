package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class GameActivity extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    private GameFragment mGameFragment;
    private ControlFragment controlFragment;
    private int finalScore = 0;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        controlFragment = (ControlFragment) getFragmentManager().findFragmentById(R.id.fragment_game_controls);
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
            controlFragment.restartTimer();
        }
        Log.d("Scroggle", "restore = " + restore);
    }

    public void restartGame() {
        mGameFragment.restartGame();
        controlFragment.resetTimer();

    }

    public void reportWinner(final Tile.Owner winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.declare_winner, winner));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();

        // Reset the board to the initial position
        mGameFragment.initGame();
    }


    //used for when the timer runs out
    public void stopGame() {
      //  mGameFragment.saveWords();
        int score = mGameFragment.getFinalScore();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.timer_finished) + " " + finalScore);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();

        mGameFragment.initGame();
    }

    public void beginPhase2() {
        finalScore = mGameFragment.getFinalScore();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Phase 1 Score: " + finalScore);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGameFragment.beginPhase2();
                        controlFragment.startPhase2Timer();
                    }
                });
        final Dialog dialog = builder.create();
        dialog.show();
    }

    public void updateScore(int score, String word) {
        controlFragment.updateScore(score, word);
    }

    public void turnOffMusic(boolean playMusic) {
        if (playMusic) {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
            backgroundMusic.start();
        } else {
            backgroundMusic.stop();
            backgroundMusic.reset();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("UT3", "state = " + gameData);
        controlFragment.pauseTimer();
        controlFragment.updatePauseButton();
        backgroundMusic.stop();
        backgroundMusic.reset();
        backgroundMusic.release();
        getFragmentManager().beginTransaction().hide(mGameFragment).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String gameData = getPreferences(MODE_PRIVATE)
                .getString(PREF_RESTORE, null);
        if (gameData != null) {
            mGameFragment.putState(gameData);
        }
        controlFragment.restartTimer();
        controlFragment.updateRestartButton();
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        backgroundMusic.start();
        getFragmentManager().beginTransaction().show(mGameFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundMusic.release();
    }
}
