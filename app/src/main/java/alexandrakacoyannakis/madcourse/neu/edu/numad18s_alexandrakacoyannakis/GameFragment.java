package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class GameFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9, R.id.large10, R.id.large11,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private int numBoards = 9;
    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[numBoards];
    private Tile mSmallTiles[][] = new Tile[numBoards][9];
    private Tile.Owner mPlayer = Tile.Owner.X;
    private int mLastLarge = -1;
    private int mLastSmall = -1;
    private ArrayList<String> words = new ArrayList<>();
    private Map<Integer, String> userWords = new HashMap<>(); //will store user words
    private Map<Integer, Map<Integer, String>> boardWords = new HashMap<>();
    private Map<Integer, String> smallBoard = new HashMap<>();
    private Set<Integer> availableSquares = new HashSet<>();
    private ArrayList<String> correctWords = new ArrayList<>();
    private ToneGenerator beep;
    private Vibrator vibrate;
    private int currentScore = 0; //keeps track of the current score
    private MediaPlayer matchSound;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beep = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        vibrate = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        matchSound = MediaPlayer.create(getActivity(), R.raw.word_match);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();
    }

    private void clearAvailable() {
        availableSquares.clear();
    }

    public boolean isAvailable(int dest) {
        return availableSquares.contains(dest);
    }

    private void addAvailableDest(int dest) {
        availableSquares.add(dest);
    }

    public int getFinalScore() {return currentScore;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.large_board, container, false);

        //find 9 letter words
        LoadWordTask loadWords = new LoadWordTask();

        try{
            correctWords = loadWords.execute().get();
        } catch (InterruptedException e) {
            Log.e("error", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("'error", e.getMessage());
        }

        addNineLetterWords();

        initViews(rootView, words);
        updateAllTiles();
        return rootView;
    }

    private void initViews(View rootView, ArrayList<String> wordsForBoard) {
        mEntireBoard.setView(rootView);
        for (int large = 0; large < numBoards; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            //find random 9 letter word for each small board
            String boardWord = randomNineLetterWord(wordsForBoard);
            Log.d("board_word", "Next word is " + boardWord);

            for (int small = 0; small < 9; small++) {
                char letter = randomLetter(boardWord);

                //remove letter so it is not chosen again
                boardWord = boardWord.replaceFirst(Character.toString(letter), "");
                smallBoard.put(small, Character.toString(letter));
                boardWords.put(large, smallBoard);

                ImageButton inner = (ImageButton) outer.findViewById
                        (mSmallIds[small]);
                inner.setImageResource(getResources().getIdentifier(Character.toString(letter), "drawable", "alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis"));
                final int fLarge = large;
                final int fSmall = small;
                final String currentLetter = Character.toString(letter);
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //when tile is selected, beep and vibrate will occur
                        beep.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                        vibrate.vibrate(150);

                        //if tile previously selected, unselect it
                        if (smallTile.getIsSelected()) {
                            unselectTile(smallTile, fLarge, currentLetter);
                            return;
                        }

                        if (isNewLargeBoard(fLarge, mLastLarge)) {
                            String word = "";
                            if (mLastLarge != -1) {
                                word = checkIfWord(mLastLarge);
                                clearUnselectedLetters(word);
                            }
                            makeMove(fLarge, fSmall, currentLetter);
                        }
                        if (isAvailable(fSmall)) {
                            makeMove(fLarge, fSmall, currentLetter);
                        }

                    }
                });
            }
        }
    }

    private void switchTurns() {
        mPlayer = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile
                .Owner.X;
    }

    private void makeMove(int large, int small, String letter) {
        Log.d("make_move", "getting to make move");
        mLastLarge = large;
        mLastSmall = small;
        Tile smallTile = mSmallTiles[large][small];
        smallTile.setOwner(mPlayer);
        smallTile.selectLetterTile();
        appendLetterToWord(mLastLarge, letter);
        setAvailableFromLastMove(small);
    }

    /**
     * restart the game
     */
    public void restartGame() {
        initGame();
        initViews(getView(), words);
        updateAllTiles();
    }

    /**
     * start phase 2: add 2 new boards
     */
    public void beginPhase2() {
        numBoards = 11;
        mLargeTiles = new Tile[numBoards];
        mSmallTiles = new Tile[numBoards][9];
        final View board10 = getView().findViewById(R.id.large10);
        board10.setVisibility(View.VISIBLE);
        final View board11 = getView().findViewById(R.id.large11);
        board11.setVisibility(View.VISIBLE);
        restartGame();
    }

    public void initGame() {
        Log.d("UT3", "init game");
        mEntireBoard = new Tile(this);
        // Create all the tiles
        for (int large = 0; large < numBoards; large++) {
            mLargeTiles[large] = new Tile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new Tile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // If the player moves first, set which spots are available
        setAvailableFromLastMove(-1);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                Tile tile = mSmallTiles[small][dest];
                if (small == 0 && !tile.getIsSelected() && (dest == 1 || dest == 3 || dest == 4)) {
                    addAvailableDest(dest);
                } else if (small == 1 && !tile.getIsSelected() && (dest == 0 || dest == 2 || dest == 3 || dest == 4 || dest == 5)) {
                    addAvailableDest(dest);
                } else if (small == 2 && !tile.getIsSelected() && (dest == 1 || dest == 4 || dest == 5)) {
                    addAvailableDest(dest);
                } else if (small == 3 && !tile.getIsSelected() && (dest == 0 || dest == 1 || dest == 4 || dest == 6 || dest == 7 )) {
                    addAvailableDest(dest);
                } else if (small == 4 && !tile.getIsSelected()) { //from this tile could move anywhere since it's center
                    addAvailableDest(dest);
                } else if (small == 5 && !tile.getIsSelected() && (dest == 1 || dest == 2 || dest == 4 || dest == 7 || dest == 8)) {
                    addAvailableDest(dest);
                } else if (small == 6 && !tile.getIsSelected() && (dest == 3 || dest == 4 || dest == 7)) {
                    addAvailableDest(dest);
                } else if (small == 7 && !tile.getIsSelected() && (dest == 3 || dest == 4 || dest == 5 || dest == 6 || dest == 8)) {
                    addAvailableDest(dest);
                } else if (small == 8 && !tile.getIsSelected() && (dest == 4 || dest == 5 || dest == 7)) {
                    addAvailableDest(dest);
                }
            }
        }
        // If there were none available, make all squares available
         else if (availableSquares.isEmpty()) {
            setAllAvailable();
        }
    }

    private void setAllAvailable() {
        availableSquares.clear();
        for (int small = 0; small < 9; small++) {
            addAvailableDest(small);
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < numBoards; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    /** Create a string containing the state of the game. */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < numBoards; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getOwner().name());
                builder.append(',');
            }
        }
        return builder.toString();
    }

    /** Restore the state of the game from the given string. */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < numBoards; large++) {
            for (int small = 0; small < 9; small++) {
                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
            }
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }

    private String randomNineLetterWord(ArrayList<String> smallBoardWords) {
        return smallBoardWords.get(new Random().nextInt(smallBoardWords.size()));
    }

    private char randomLetter(String word) {
        return word.charAt(new Random().nextInt(word.length()));
    }


    //calculate the score using the scrabble scores.
    //add a bonus 5 points for any words with all 9 letters
    //subtract 2 points for any incorrect words
    public int calculateScore(boolean isCorrect, String word ) {

        if (isCorrect) {
            //bonus points if word is 9 letter (full board)
            if (word.length() == 9) {
                currentScore += 5;
            }

            for (int j = 0; j < word.length(); j++) {
                char letter = word.charAt(j);
                if (isOnePointLetter(letter)) {
                    currentScore += 1;
                } else if (isTwoPointLetter(letter)) {
                    currentScore += 2;
                } else if (isThreePointLetter(letter)) {
                    currentScore += 3;
                } else if (isFourPointLetter(letter)) {
                    currentScore += 4;
                } else if (isFivePointLetter(letter)) {
                    currentScore += 5;
                } else if (isEightPointLetter(letter)) {
                    currentScore += 8;
                } else if (isTenPointLetter(letter)) {
                    currentScore += 10;
                }

            }
        } else {
            //subtract 2 points for each incorrect word
            currentScore -= 2;
        }

        //to not discourage players,
        //set negative score to 0
        if (currentScore < 0 )
        {
            currentScore = 0;
        }
        return currentScore;
    }

    // letter checks to determine scoring below
    private boolean isOnePointLetter(char letter) {
        if (letter == 'e' || letter == 'a' || letter == 'i' || letter == 'o' || letter == 'n'
                || letter == 'r' || letter == 't' || letter == 'l' || letter =='s' || letter == 'u') {
            return true;
        }
        return false;
    }

    private boolean isTwoPointLetter(char letter) {
        if (letter == 'd' || letter == 'g') {
            return true;
        }
        return false;
    }

    private boolean isThreePointLetter(char letter) {
        if (letter == 'b' || letter == 'c' || letter == 'm' || letter == 'p') {
            return true;
        }
        return false;
    }

    private boolean isFourPointLetter(char letter) {
        if (letter == 'f' || letter == 'h' || letter == 'v' || letter == 'w' || letter == 'y') {
            return true;
        }
        return false;
    }

    private boolean isFivePointLetter(char letter) {
        if (letter == 'k') {
            return true;
        }
        return false;
    }

    private boolean isEightPointLetter(char letter) {
        if (letter == 'j' || letter == 'x') {
            return true;
        }
        return false;
    }

    private boolean isTenPointLetter(char letter) {
        if (letter == 'q' || letter == 'z') {
            return true;
        }
        return false;
    }

    /**
     * if user enters next large board, reset
     * @param nextLarge
     * @param currentLarge
     * @return
     */
    private boolean isNewLargeBoard(int nextLarge, int currentLarge) {
        if (nextLarge == currentLarge) {
            return false;
        }
        return true;
    }

    private void clearUnselectedLetters(String word) {
        if (word != null && word.length() > 0) {
            for (int small = 0; small < 9; small++) {
                Tile tile = mSmallTiles[mLastLarge][small];
                if (!tile.getIsSelected()) {
                    tile.clearLetter();
                }
            }
        }
    }

    private void appendLetterToWord(int large, String letter) {

        String currentWord = userWords.get(large);
        if (currentWord == null) {
            userWords.put(large, letter);
        } else {
            userWords.put(large, currentWord + letter);
        }
    }

    private void unselectTile(Tile smallTile, int large, String currentLetter){

        String word = userWords.get(large);
        int last = word.length() - 1;
        char newLastLetter = word.charAt(last);

        //unselect and remove letter only if last tile selected
        if (Character.toString(newLastLetter).equals(currentLetter)) {
            smallTile.unselectTile();
            word = word.substring(0, last);
            userWords.put(large, word);
        }

        //set all tiles to be available if the first character unselected
        if (last == 0) {
            setAllAvailable();
        } else {
            setAvailableFromLastMove(mLastSmall);
        }

    }

    private String checkIfWord(int lastBoard) {
        String word = userWords.get(lastBoard);

        if (word != null) {
            if (correctWords.contains(word)) {
                calculateScore(true, word);
                matchSound.start();
                ((GameActivity)getActivity()).updateScore(currentScore, word);
            } else {
                calculateScore(false, word);
                ((GameActivity)getActivity()).updateScore(currentScore, "");
            }
        }

        return word;
    }

    @Override
    public void onDestroy()  {
        super.onDestroy();
        beep.release();
        vibrate.cancel();
        matchSound.stop();
        matchSound.reset();
        matchSound.release();
    }

    /**
     * add 9 letter words to the possible words
     * that can be used for the board
     */
    private void addNineLetterWords() {
        for (int i=0; i < correctWords.size(); i++) {
            if (correctWords.get(i).length() == 9) {
                words.add(correctWords.get(i));
            }
        }
    }

    /**
     * LoadWordTask is an async task that loads all of the words provided by
     * the text file. These words will then later be used to check against
     * the user input from each board to see if the word is correct.
     */
    private class LoadWordTask extends AsyncTask<Void, Integer, ArrayList<String>> {

        InputStream inputStream = null;
        BufferedReader reader = null;
        ArrayList<String> words = new ArrayList<>(); //words from the text file

        @Override
        protected ArrayList<String> doInBackground(Void...params) {
            try {
                inputStream = getActivity().getAssets().open("wordlist.txt");
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                //only add 9 character words for block
                while ((line = reader.readLine()) != null) {
                    words.add(line);
                }
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                Log.e("message: ",e.getMessage());
            }
            return words;
        }
    }
}
