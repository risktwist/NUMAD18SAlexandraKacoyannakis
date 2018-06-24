package alexandrakacoyannakis.madcourse.neu.edu.numad18s_alexandrakacoyannakis;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class Tile {

    public enum Owner {
        X, O /* letter O */, NEITHER, BOTH
    }

    private final GameFragment mGame;
    private Owner mOwner = Owner.NEITHER;
    private View mView;
    private Tile mSubTiles[];
    private boolean isSelected = false;

    public Tile(GameFragment game) {
        this.mGame = game;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public Owner getOwner() {
        return mOwner;
    }

    public void setOwner(Owner owner) {
        this.mOwner = owner;
    }

    public Tile[] getSubTiles() {
        return mSubTiles;
    }

    public void setSubTiles(Tile[] subTiles) {
        this.mSubTiles = subTiles;
    }

    public boolean getIsSelected() {return isSelected;}

    public void setIsSelected(boolean isSelected) {this.isSelected = isSelected;}

    public void updateDrawableState() {
        if (mView == null) return;
        if (mView instanceof ImageButton) {
            mView.setBackgroundResource(R.color.available_color);
        }
    }

    public void selectLetterTile() {
        if (mView instanceof ImageButton) {
            mView.setBackgroundResource(R.color.green);
            isSelected = true;
        }
    }

    public void unselectTile() {
        if (mView instanceof ImageButton) {
            mView.setBackgroundResource(R.color.available_color);
            isSelected = false;
        }
    }

    private void countCaptures(int totalX[], int totalO[]) {
        int capturedX, capturedO;
        // Check the horizontal
        for (int row = 0; row < 3; row++) {
            capturedX = capturedO = 0;
            for (int col = 0; col < 3; col++) {
                Owner owner = mSubTiles[3 * row + col].getOwner();
                if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
                if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
            }
            totalX[capturedX]++;
            totalO[capturedO]++;
        }

        // Check the vertical
        for (int col = 0; col < 3; col++) {
            capturedX = capturedO = 0;
            for (int row = 0; row < 3; row++) {
                Owner owner = mSubTiles[3 * row + col].getOwner();
                if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
                if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
            }
            totalX[capturedX]++;
            totalO[capturedO]++;
        }

        // Check the diagonals
        capturedX = capturedO = 0;
        for (int diag = 0; diag < 3; diag++) {
            Owner owner = mSubTiles[3 * diag + diag].getOwner();
            if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
            if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
        }
        totalX[capturedX]++;
        totalO[capturedO]++;
        capturedX = capturedO = 0;
        for (int diag = 0; diag < 3; diag++) {
            Owner owner = mSubTiles[3 * diag + (2 - diag)].getOwner();
            if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
            if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
        }
        totalX[capturedX]++;
        totalO[capturedO]++;
    }

    public Owner findWinner() {
        // If owner already calculated, return it
        if (getOwner() != Owner.NEITHER)
            return getOwner();

        int totalX[] = new int[4];
        int totalO[] = new int[4];
        countCaptures(totalX, totalO);
        if (totalX[3] > 0) return Owner.X;
        if (totalO[3] > 0) return Owner.O;

        // Check for a draw
        int total = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Owner owner = mSubTiles[3 * row + col].getOwner();
                if (owner != Owner.NEITHER) total++;
            }
            if (total == 9) return Owner.BOTH;
        }

        // Neither player has won this tile
        return Owner.NEITHER;
    }

    public void clearLetter() {
        if (mView instanceof ImageButton) {
            ((ImageButton) mView).setImageResource(R.color.available_color);
        }
    }
}
