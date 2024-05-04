package board;

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
}
