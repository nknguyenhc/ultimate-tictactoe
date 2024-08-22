package board;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a move on an ultimate tictactoe board.
 */
public class Move {
    /** Represents the coordinates of the move, between 0 and 8 inclusive. */
    public final byte row;
    public final byte col;

    public Move(byte row, byte col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public int hashCode() {
        return this.row * 9 + this.col;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Move)) {
            return false;
        }

        Move move = (Move) object;
        return this.row == move.row && this.col == move.col;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", this.row + 1, this.col + 1);
    }

    public String sparString() {
        return String.format("%d %d", this.row, this.col);
    }

    /**
     * Checks if two moves are equivalent in the given board,
     * due to the board being symmetrical.
     */
    public boolean isEquivalentTo(Move move, Board board) {
        List<Utils.Morph> possibleMorphs = this.possibleMorphsWith(move);
        for (Utils.Morph morph: possibleMorphs) {
            if (board.isMorphable(morph)) {
                return true;
            }
        }
        return false;
    }

    private List<Utils.Morph> possibleMorphsWith(Move move) {
        List<Utils.Morph> morphs = new ArrayList<>();
        if (this.isNorthMorphableWith(move)) {
            morphs.add(Utils.Morph.NORTH);
        }
        if (this.isNortheastMorphableWith(move)) {
            morphs.add(Utils.Morph.NORTHEAST);
        }
        if (this.isEastMorphableWith(move)) {
            morphs.add(Utils.Morph.EAST);
        }
        if (this.isSoutheastMorphableWith(move)) {
            morphs.add(Utils.Morph.SOUTHEAST);
        }
        if (this.isClockwiseMorphableWith(move)) {
            morphs.add(Utils.Morph.CLOCKWISE);
        }
        if (this.isAnticlockwiseMorphableWith(move)) {
            morphs.add(Utils.Morph.ANTICLOCKWISE);
        }
        if (this.isSemicircleMorphableWith(move)) {
            morphs.add(Utils.Morph.SEMICIRCLE);
        }
        return morphs;
    }

    private boolean isNorthMorphableWith(Move move) {
        return this.row == move.row && this.col + move.col == 8;
    }

    private boolean isNortheastMorphableWith(Move move) {
        return this.row + move.col == 8 && this.col + move.row == 8;
    }

    private boolean isEastMorphableWith(Move move) {
        return this.row + move.row == 8 && this.col == move.col;
    }

    private boolean isSoutheastMorphableWith(Move move) {
        return this.row == move.col && this.col == move.row;
    }

    private boolean isClockwiseMorphableWith(Move move) {
        return this.row + move.col == 8 && this.col == move.row;
    }

    private boolean isAnticlockwiseMorphableWith(Move move) {
        return this.row == move.col && this.col + move.row == 8;
    }

    private boolean isSemicircleMorphableWith(Move move) {
        return this.row + move.row == 8 && this.col + move.col == 8;
    }
}
