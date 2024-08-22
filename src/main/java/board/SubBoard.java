package board;

import java.util.Objects;

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
     * Creates a new board from the human-readable strings.
     * Each string corresponds to a line, for eg: {@code "X O -"}.
     * @throws InvalidBoardStringException If the given board string is invalid.
     */
    public static SubBoard fromString(String line1, String line2, String line3) throws InvalidBoardStringException {
        int Xboard = SubBoard.parseLine(line1, true);
        int Oboard = SubBoard.parseLine(line1, false);

        Xboard += SubBoard.parseLine(line2, true) << 3;
        Oboard += SubBoard.parseLine(line2, false) << 3;

        Xboard += SubBoard.parseLine(line3, true) << 6;
        Oboard += SubBoard.parseLine(line3, false) << 6;

        return new SubBoard((short) Xboard, (short) Oboard);
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
    public static SubBoard fromCompactString(String compactString) throws InvalidBoardStringException {
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

        return new SubBoard(Xboard, Oboard);
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

    @Override
    public int hashCode() {
        return Objects.hash(this.Xboard, this.Oboard);
    }

    /**
     * Determines if this board can be morphed into the given sub-board.
     * @param morph The transformation to consider.
     * @param other The sub-board to check.
     */
    public boolean isSameAs(Utils.Morph morph, SubBoard other) {
        switch (morph) {
            case NORTH:
                return this.isNorthMorphable(other);
            case NORTHEAST:
                return this.isNortheastMorphable(other);
            case EAST:
                return this.isEastMorphable(other);
            case SOUTHEAST:
                return this.isSoutheastMorphable(other);
            case CLOCKWISE:
                return this.isClockwiseMorphable(other);
            case ANTICLOCKWISE:
                return this.isAnticlockwiseMorphable(other);
            case SEMICIRCLE:
                return this.isSemicircleMorphable(other);
            default:
                return false;
        }
    }

    private boolean isNorthMorphable(SubBoard other) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (((this.Xboard >> (3 * row + col)) & 1) != ((other.Xboard >> (3 * row + 2 - col)) & 1)) {
                    return false;
                }
                if (((this.Oboard >> (3 * row + col)) & 1) != ((other.Oboard >> (3 * row + 2 - col)) & 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNortheastMorphable(SubBoard other) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (((this.Xboard >> (3 * row + col)) & 1) != ((other.Xboard >> (3 * (2 - col) + 2 - row)) & 1)) {
                    return false;
                }
                if (((this.Oboard >> (3 * row + col)) & 1) != ((other.Oboard >> (3 * (2 - col) + 2 - row)) & 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isEastMorphable(SubBoard other) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (((this.Xboard >> (3 * row + col)) & 1) != ((other.Xboard >> (3 * (2 - row) + col)) & 1)) {
                    return false;
                }
                if (((this.Oboard >> (3 * row + col)) & 1) != ((other.Oboard >> (3 * (2 - row) + col)) & 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSoutheastMorphable(SubBoard other) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (((this.Xboard >> (3 * row + col)) & 1) != ((other.Xboard >> (3 * col + row)) & 1)) {
                    return false;
                }
                if (((this.Oboard >> (3 * row + col)) & 1) != ((other.Oboard >> (3 * col + row)) & 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isClockwiseMorphable(SubBoard other) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (((this.Xboard >> (3 * row + col)) & 1) != ((other.Xboard >> (3 * col + (2 - row))) & 1)) {
                    return false;
                }
                if (((this.Oboard >> (3 * row + col)) & 1) != ((other.Oboard >> (3 * col + (2 - row))) & 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAnticlockwiseMorphable(SubBoard other) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (((this.Xboard >> (3 * row + col)) & 1) != ((other.Xboard >> (3 * (2 - col) + row)) & 1)) {
                    return false;
                }
                if (((this.Oboard >> (3 * row + col)) & 1) != ((other.Oboard >> (3 * (2 - col) + row)) & 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSemicircleMorphable(SubBoard other) {
        for (int i = 0; i < 9; i++) {
            if (((this.Xboard >> i) & 1) != ((other.Xboard >> (8 - i)) & 1)) {
                return false;
            }
            if (((this.Oboard >> i) & 1) != ((other.Oboard >> (8 - i)) & 1)) {
                return false;
            }
        }
        return true;
    }
}
