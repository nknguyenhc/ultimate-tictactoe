package board;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3x3 sub-board within an ultimate tictactoe board.
 */
public class SubBoard {
    /** Represents the occupation across the boards, using the first 9 bits only */
    private final short Xboard;
    private final short Oboard;
    /** Row index in the bigger board that this sub-board is at. */
    private final byte row;
    /** Col index in the bigger board that this sub-board is at. */
    private final byte col;
    /** Represents the winner, 0 for draw/not determined, 1 for X, 2 for O */
    private final Utils.Side winner;
    /** Actions */
    private final List<Move> actions;

    public SubBoard(byte row, byte col) {
        this.Xboard = 0;
        this.Oboard = 0;
        this.row = row;
        this.col = col;
        this.winner = Utils.Side.U;
        this.actions = this.determineActions();
    }

    private SubBoard(short Xboard, short Oboard, byte row, byte col) {
        this.Xboard = Xboard;
        this.Oboard = Oboard;
        this.row = row;
        this.col = col;
        this.winner = this.determineWinner();
        this.actions = this.determineActions();
    }

    /**
     * Creates a new board from the human-readable strings.
     * Each string corresponds to a line, for eg: {@code "X O -"}.
     * @throws InvalidBoardStringException If the given board string is invalid.
     */
    public static SubBoard fromString(String line1, String line2, String line3,
                                      byte row, byte col) throws InvalidBoardStringException {
        int Xboard = SubBoard.parseLine(line1, true);
        int Oboard = SubBoard.parseLine(line1, false);

        Xboard += SubBoard.parseLine(line2, true) << 3;
        Oboard += SubBoard.parseLine(line2, false) << 3;

        Xboard += SubBoard.parseLine(line3, true) << 6;
        Oboard += SubBoard.parseLine(line3, false) << 6;

        return new SubBoard((short) Xboard, (short) Oboard, row, col);
    }

    /**
     * Returns the new value of the board given the string of the line, eg: {@code "X O -"}.
     */
    private static int parseLine(String line, boolean isX) throws InvalidBoardStringException {
        String[] cells = line.split(" ");
        if (cells.length != 3) {
            throw new InvalidBoardStringException(String.format("Line length not of correct length: %s", line));
        }
        int currentBoard = parseCell(cells[0], isX);
        currentBoard += parseCell(cells[1], isX) << 1;
        currentBoard += parseCell(cells[2], isX) << 2;
        return currentBoard;
    }

    /**
     * Parses the cell, of form {@code "X"}, {@code "O"}, or {@code "-"}.
     */
    private static int parseCell(String cell, boolean isX) throws InvalidBoardStringException {
        switch (cell) {
            case "X":
                return isX ? 1 : 0;
            case "O":
                return isX ? 0 : 1;
            case "-":
                return 0;
            default:
                throw new InvalidBoardStringException(String.format("Invalid character: %s", cell));
        }
    }

    /**
     * Returns a board that corresponds to the compact string representation.
     * The string is of format {@code x,o},
     * where {@code x} is the decimal number for {@code Xboard},
     * and {@code o} is the decimal number for {@code Oboard}.
     * @throws InvalidBoardStringException If the string given is invalid.
     */
    public static SubBoard fromCompactString(String compactString,
                                             byte row, byte col) throws InvalidBoardStringException {
        String[] numbers = compactString.split(",");
        if (numbers.length != 2) {
            throw new InvalidBoardStringException(String.format("Invalid board compact string: %s", compactString));
        }

        short Xboard;
        short Oboard;
        try {
            Xboard = Short.parseShort(numbers[0]);
            Oboard = Short.parseShort(numbers[1]);
        } catch (NumberFormatException e) {
            throw new InvalidBoardStringException(String.format(
                    "Compact string contains an invalid number: %s", compactString));
        }

        return new SubBoard(Xboard, Oboard, row, col);
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
        return new SubBoard(newXboard, newOboard, this.row, this.col);
    }

    /**
     * Determines the winner for this board.
     * The result is saved, and returned when needed.
     * To be called after making each move, or upon a new board.
     */
    private Utils.Side determineWinner() {
        if (this.isWinner(this.Xboard)) {
            return Utils.Side.X;
        } else if (this.isWinner(this.Oboard)) {
            return Utils.Side.O;
        } else if ((this.Xboard | this.Oboard) == Utils.filled) {
            return Utils.Side.D;
        } else {
            return Utils.Side.U;
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
     * Pre-calculates the actions available on this board.
     * To be used only in constructor.
     */
    private List<Move> determineActions() {
        if (this.winner != Utils.Side.U) {
            return List.of();
        }
        int occupationBoard = this.Xboard | this.Oboard;
        List<Move> actions = new ArrayList<>();
        for (byte row = 0; row < 3; row++) {
            for (byte col = 0; col < 3; col++) {
                if (((occupationBoard >> (3 * row + col)) & 1) == 0) {
                    actions.add(new Move(
                            (byte) (this.row * 3 + row),
                            (byte) (this.col * 3 + col)));
                }
            }
        }
        return actions;
    }

    public List<Move> getActions() {
        return this.actions;
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
     * Returns the compact string representation, for ease of copy-pasting.
     */
    public String toCompactString() {
        return String.format("%d,%d", this.Xboard, this.Oboard);
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
