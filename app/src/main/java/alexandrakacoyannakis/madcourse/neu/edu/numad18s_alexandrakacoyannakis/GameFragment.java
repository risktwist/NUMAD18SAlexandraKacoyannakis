package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class GameFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private Tile.Owner mPlayer = Tile.Owner.X;
    private Tile currentTile = new Tile(this);
    private Set<Tile> mAvailable = new HashSet<Tile>();
    private int mLastLarge;
    private int mLastSmall;
    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<String> userWords = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();
    }

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(Tile tile) {
        mAvailable.add(tile);
    }

    public boolean isAvailable(Tile tile, int smallTile) {

        Log.d("subtiles", smallTile+"'");
        return mAvailable.contains(tile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.large_board, container, false);

        //find 9 letter words
        LoadWordTask loadWords = new LoadWordTask();

        try{
            words = loadWords.execute().get();
        } catch (InterruptedException e) {
            Log.e("error", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("'error", e.getMessage());
        }
        Log.v("word_result", "word size from results is " + words.size());

        initViews(rootView, words);
        updateAllTiles();
        return rootView;
    }

    private void initViews(View rootView, ArrayList<String> wordsForBoard) {
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);

            //find random 9 letter word for each small board
            String boardWord = randomNineLetterWord(wordsForBoard);
            Log.d("board_word", "Next word is " + boardWord);

            for (int small = 0; small < 9; small++) {
                char letter = randomLetter(boardWord);

                //remove letter so it is not chosen again
                boardWord = boardWord.replaceFirst(Character.toString(letter), "");
                Log.d("random_letter", "Random letter is " + letter);

                ImageButton inner = (ImageButton) outer.findViewById
                        (mSmallIds[small]);
                inner.setImageResource(getResources().getIdentifier(Character.toString(letter), "drawable", "alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis"));
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAvailable(smallTile, fSmall)) {
                            makeMove(fLarge, fSmall);
                        }
                           // switchTurns();
                    }
                });
            }
        }
    }

    private void switchTurns() {
        mPlayer = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile
                .Owner.X;
    }

    private void makeMove(int large, int small) {
        Log.d("make_move", "getting to make move");
        mLastLarge = large;
        mLastSmall = small;
        Tile smallTile = mSmallTiles[large][small];
        Tile largeTile = mLargeTiles[large];
        smallTile.setOwner(mPlayer);
        smallTile.selectLetterTile();
        currentTile = smallTile;

        setAvailableFromLastMove(small);
        /*Tile.Owner oldWinner = largeTile.getOwner();
        Tile.Owner winner = largeTile.findWinner();
        if (winner != oldWinner) {
            largeTile.setOwner(winner);
        }
        winner = mEntireBoard.findWinner();
        mEntireBoard.setOwner(winner);
        updateAllTiles();
        if (winner != Tile.Owner.NEITHER) {
            ((GameActivity)getActivity()).reportWinner(winner);
        }*/
    }

    public void restartGame() {
        initGame();
        initViews(getView(), words);
        updateAllTiles();

    }

    public void initGame() {
        Log.d("UT3", "init game");
        mEntireBoard = new Tile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new Tile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new Tile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // If the player moves first, set which spots are available
        Log.d("what_is_initial", Integer.toString(mLastSmall));
        setAvailableFromLastMove(-1);
    }

    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                Tile tile = mSmallTiles[small][dest];
                Log.d("smallTile_selected" , currentTile.getIsSelected()+"");
                if (mLastSmall == 0 && !currentTile.getIsSelected() && (dest == 1 || dest == 3 || dest == 4)) {
                    addAvailable(tile);
                } else if (mLastSmall == 1 && !currentTile.getIsSelected() && (dest == 0 || dest == 2 || dest == 3 || dest == 4 || dest == 5)) {
                    addAvailable(tile);
                } else if (mLastSmall == 2 && !currentTile.getIsSelected() && (dest == 1 || dest == 4 || dest == 5)) {
                    addAvailable(tile);
                } else if (mLastSmall == 3 && !currentTile.getIsSelected() && (dest == 0 || dest == 1 || dest == 4 || dest == 6 || dest == 7 )) {
                    addAvailable(tile);
                } else if (mLastSmall == 4 && !currentTile.getIsSelected()) { //from this tile could move anywhere since it's center
                    addAvailable(tile);
                } else if (mLastSmall == 5 && !currentTile.getIsSelected() && (dest == 1 || dest == 2 || dest == 4 || dest == 7 || dest == 8)) {
                    addAvailable(tile);
                } else if (mLastSmall == 6 && !currentTile.getIsSelected() && (dest == 3 || dest == 4 || dest == 7)) {
                    addAvailable(tile);
                } else if (mLastSmall == 7 && !currentTile.getIsSelected() && (dest == 3 || dest == 4 || dest == 5 || dest == 6 || dest == 8)) {
                    addAvailable(tile);
                } else if (mLastSmall == 8 && !currentTile.getIsSelected() && (dest == 4 || dest == 5 || dest == 7)) {
                    addAvailable(tile);
                }
            }
        }
        // If there were none available, make all squares available
         else if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

    private void setAllAvailable() {
        Log.d("setallavailable", "getting to set all available");
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile tile = mSmallTiles[large][small];
                if (tile.getOwner() == Tile.Owner.NEITHER)
                    addAvailable(tile);
            }
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
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
        for (int large = 0; large < 9; large++) {
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
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
            }
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }

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
                    if (line.length() == 9) {
                        words.add(line);
                    }
                }
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                Log.e("message: ",e.getMessage());
            }
            return words;
        }
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
    public int calculateScore() {
        int score = 0;
        for (int i=0; i  < userWords.size(); i++) {
            String word = userWords.get(i);
            if (word.length() == 9) {
                score += 5;
            }

            for (int j = 0; j < word.length(); j++) {
                char letter = word.charAt(j);
                if (isOnePointLetter(letter)){
                    score += 1;
                } else if (isTwoPointLetter(letter)) {
                    score += 2;
                } else if (isThreePointLetter(letter)) {
                    score += 3;
                } else if (isFourPointLetter(letter)) {
                    score +=4;
                } else if (isFivePointLetter(letter)) {
                    score += 5;
                } else if (isEightPointLetter(letter)) {
                    score += 8;
                } else if (isTenPointLetter(letter)) {
                    score += 10;
                }
            }
        }
        return score;
    }

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
}
