package board;

/**
 * Represents a 3x3 sub-board within an ultimate tictactoe board.
 */
public class SubBoard {
    /** Represents the occupation across the boards, using the first 9 bits only */
    private final short Xboard;
    private final short Oboard;
    /** Represents the winner, 0 for draw/not determined, 1 for X, 2 for O */
    private Utils.Side winner = Utils.Side.U;
    /** Represents the lines along which a player can win */

    public SubBoard() {
        this.Xboard = 0;
        this.Oboard = 0;
    }

    private SubBoard(short Xboard, short Oboard) {
        this.Xboard = Xboard;
        this.Oboard = Oboard;
        this.determineWinner();
    }

    /**
     * Makes a move on the board, assuming that the board is valid.
     * Does not mutate the current board.
     * @param row The row index to move, between 0 and 2 inclusive.
     * @param col The column to move, between 0 and 2 inclusive.
     * @param side The side to move, {@code true} for X, {@code false} for O.
     * @return The new board.
     */
    public SubBoard move(int row, int col, boolean side) {
        short newXboard = this.Xboard;
        short newOboard = this.Oboard;
        if (side) {
            newXboard |= 1 << (3 * row + col);
        } else {
            newOboard |= 1 << (3 * row + col);
        }
        return new SubBoard(newXboard, newOboard);
    }

    /**
     * Determines the winner for this board.
     * The result is saved, and returned when needed.
     * To be called after making each move, or upon a new board.
     */
    private void determineWinner() {
        if (this.isWinner(this.Xboard)) {
            this.winner = Utils.Side.X;
        } else if (this.isWinner(this.Oboard)) {
            this.winner = Utils.Side.O;
        } else if ((this.Xboard | this.Oboard) == Utils.filled) {
            this.winner = Utils.Side.D;
        } else {
            this.winner = Utils.Side.U;
        }
    }

    /**
     * Determines if the player with the occupation board is the winner.
     * @param board Either {@code this.Xboard} or {@code this.Oboard}.
     */
    private boolean isWinner(short board) {
        for (short line: Utils.winningLines) {
            if ((board & line) == line) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the current winner of the board.
     */
    public Utils.Side getWinner() {
        return this.winner;
    }

    /**
     * Returns the occupation of this board.
     * @param row The row index.
     * @param col The column index.
     */
    public Utils.Side getOccupation(int row, int col) {
        if ((this.Xboard & (1 << (3 * row + col))) > 0) {
            return Utils.Side.X;
        } else if ((this.Oboard & (1 << (3 * row + col))) > 0) {
            return Utils.Side.O;
        } else {
            return Utils.Side.U;
        }
    }

    /**
     * Returns the char representing the occupier of the cell
     * @param positionMask The mask representing the position in binary,
     *                     must be 1 at the position and 0 everywhere else,
     * @return {@code 'X'}, {@code 'O'} or {@code '-'}
     */
    private char getCharAtPosition(short positionMask) {
        if ((this.Xboard & positionMask) > 0) {
            return 'X';
        } else if ((this.Oboard & positionMask) > 0) {
            return 'O';
        } else {
            return '-';
        }
    }

    /**
     * Returns the string representation of the row.
     * @param rowIndex The index of the row, between 0 and 2 inclusive.
     */
    public String getRow(int rowIndex) {
        return String.format("%c %c %c",
                this.getCharAtPosition((short) (1 << (3 * rowIndex))),
                this.getCharAtPosition((short) (1 << (3 * rowIndex + 1))),
                this.getCharAtPosition((short) (1 << (3 * rowIndex + 2))));
    }

    /**
     * Returns the string representation of a board.
     */
    @Override
    public String toString() {
        return String.format("%s\n%s\n%s", this.getRow((byte) 0), this.getRow((byte) 1), this.getRow((byte) 2));
    }

    /**
     * Determines if two boards are equal.
     * They are equal of the occupation of X and O are the same.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof SubBoard)) {
            return false;
        }

        SubBoard board = (SubBoard) object;
        return this.Xboard == board.Xboard && this.Oboard == board.Oboard;
    }
}
