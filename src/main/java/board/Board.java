package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a 9x9 ultimate tictactoe board.
 */
public class Board {
    private final int[] subBoards;
    /** The board index that the next player must go at. 9 if the next player can go anywhere. */
    private final byte subBoardIndex;
    /** Represents the turn at this board. {@code true} if X, {@code false} otherwise. */
    private final boolean turn;
    /** Cached value of the winner */
    public final Utils.Side winner;
    /** Boards won by X. */
    private final short Xmeta;
    /** Boards won by O. */
    private final short Ometa;
    /** Boards draw. */
    private final short Dmeta;

    public Board() {
        this.subBoards = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        this.subBoardIndex = 9;
        this.turn = true;
        this.Xmeta = 0;
        this.Ometa = 0;
        this.Dmeta = 0;
        this.winner = Utils.Side.U;
    }

    private Board(int[] subBoards, byte subBoardIndex, boolean turn,
                  short Xmeta, short Ometa, short Dmeta) {
        this.subBoards = subBoards;
        this.subBoardIndex = subBoardIndex;
        this.turn = turn;
        this.Xmeta = Xmeta;
        this.Ometa = Ometa;
        this.Dmeta = Dmeta;
        this.winner = this.determineWinner();
    }

    /**
     * Returns the board with the given string representation.
     * The string representation must be the same as {@code toString} method,
     * and must additional have two numbers, first representing {@code subBoardIndex},
     * second representing {@code turn}.
     * An example board is as follows:
     *
     * <pre>
     * - - X  - - -  - - -
     * - - -  - - -  - - -
     * - - -  - - -  - O -
     *
     * - - -  O - -  - - -
     * - - -  - X -  - - -
     * - - -  - - -  - - -
     *
     * - - -  - - -  - - -
     * - - -  - - -  - - -
     * - - -  - - -  - - -
     *
     * 7,0
     * </pre>
     *
     * This means that it is X's turn to go at board (3, 2).
     */
    public static Board fromString(String string) throws InvalidBoardStringException {
        String[] strings = string.split("\n\n");
        if (strings.length != 4) {
            throw new InvalidBoardStringException(String.format(
                    "String not enough length after \\n\\n separation: %s;\nExpected length: 4, actual length: %d",
                    string, strings.length));
        }

        int[] subBoards = new int[9];
        for (byte i = 0; i < 3; i++) {
            String[] lines = strings[i].split("\n");
            if (lines.length != 3) {
                throw new InvalidBoardStringException(String.format(
                        "Group does not have the correct number of lines:\n%s;\nExpected length: 3, actual: %d",
                        strings[i], lines.length));
            }
            String[][] subBoardLines = new String[3][];
            for (int j = 0; j < 3; j++) {
                subBoardLines[j] = lines[j].split("  ");
                if (subBoardLines[j].length != 3) {
                    throw new InvalidBoardStringException(String.format(
                            "Long line does not have the correct number of groups: %s; Expected number of groups: %d",
                            lines[j], subBoardLines[j].length));
                }
            }
            for (byte j = 0; j < 3; j++) {
                subBoards[3 * i + j] = subBoardFromString(subBoardLines[0][j],
                        subBoardLines[1][j], subBoardLines[2][j]);
            }
        }
        short Xmeta = 0;
        short Ometa = 0;
        short Dmeta = 0;
        for (int i = 0; i < 9; i++) {
            int subBoard = subBoards[i];
            if (Utils.wins[subBoard & Utils.filled]) {
                Xmeta |= 1 << i;
            } else if (Utils.wins[(subBoard >> 9) & Utils.filled]) {
                Ometa |= 1 << i;
            } else if (((subBoard & Utils.filled) | (subBoard >> 9)) == Utils.filled) {
                Dmeta |= 1 << i;
            }
        }

        String[] boardInfo = strings[3].split(",");
        if (boardInfo.length != 2) {
            throw new InvalidBoardStringException(String.format(
                    "Wrong number of elements in last line: %s; Expected 2, received %d",
                    strings[3], boardInfo.length));
        }

        int boardIndex;
        int turnValue;
        try {
            boardIndex = Integer.parseInt(boardInfo[0]);
            turnValue = Integer.parseInt(boardInfo[1]);
        } catch (NumberFormatException e) {
            throw new InvalidBoardStringException(String.format(
                    "Last line contains an invalid number: %s", strings[3]));
        }

        validateBoardIndexAndTurnValue(boardIndex, turnValue);
        return new Board(subBoards, (byte) boardIndex, turnValue == 0, Xmeta, Ometa, Dmeta);
    }

    /**
     * Creates a new sub-board from the human-readable strings.
     * Each string corresponds to a line, for eg: {@code "X O -"}.
     * @throws InvalidBoardStringException If the given board string is invalid.
     */
    static int subBoardFromString(String line1, String line2, String line3) throws InvalidBoardStringException {
        int Xboard = parseLine(line1, true);
        int Oboard = parseLine(line1, false);

        Xboard += parseLine(line2, true) << 3;
        Oboard += parseLine(line2, false) << 3;

        Xboard += parseLine(line3, true) << 6;
        Oboard += parseLine(line3, false) << 6;

        return Xboard + (Oboard << 9);
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
     * Gets the board from the compact string.
     * The compact string is of the format {@code x1,y1 x2,y2 ... x9,y9 boardIndex,turnValue},
     * where each {@code xi,yi} represents the corresponding sub-board.
     */
    public static Board fromCompactString(String string) throws InvalidBoardStringException {
        String[] strings = string.split(" ");
        if (strings.length != 10) {
            throw new InvalidBoardStringException(String.format(
                    "String does not the correct number of items: %s\nExpected 10, got: %d",
                    string, strings.length));
        }

        int[] subBoards = new int[9];
        for (byte i = 0; i < 9; i++) {
            subBoards[i] = subBoardFromCompactString(strings[i]);
        }
        short Xmeta = 0;
        short Ometa = 0;
        short Dmeta = 0;
        for (int i = 0; i < 9; i++) {
            int subBoard = subBoards[i];
            if (Utils.wins[subBoard & Utils.filled]) {
                Xmeta |= 1 << i;
            } else if (Utils.wins[(subBoard >> 9) & Utils.filled]) {
                Ometa |= 1 << i;
            } else if (((subBoard & Utils.filled) | (subBoard >> 9)) == Utils.filled) {
                Dmeta |= 1 << i;
            }
        }

        String[] elems = strings[9].split(",");
        if (elems.length != 2) {
            throw new InvalidBoardStringException(String.format(
                    "Invalid last element: %s; Expected 2, got: %d", strings[9], elems.length));
        }

        int boardIndex;
        int turnValue;
        try {
            boardIndex = Integer.parseInt(elems[0]);
            turnValue = Integer.parseInt(elems[1]);
        } catch (NumberFormatException e) {
            throw new InvalidBoardStringException(String.format(
                    "Unable to parse numbers: %s", strings[9]));
        }

        validateBoardIndexAndTurnValue(boardIndex, turnValue);
        return new Board(subBoards, (byte) boardIndex, turnValue == 0, Xmeta, Ometa, Dmeta);
    }

    /**
     * Returns a sub-board that corresponds to the compact string representation.
     * The string is of format {@code x,o},
     * where {@code x} is the decimal number for {@code Xboard},
     * and {@code o} is the decimal number for {@code Oboard}.
     * @throws InvalidBoardStringException If the string given is invalid.
     */
    static int subBoardFromCompactString(String compactString) throws InvalidBoardStringException {
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

        return Xboard + (Oboard << 9);
    }

    private static void validateBoardIndexAndTurnValue(int boardIndex, int turnValue)
            throws InvalidBoardStringException {
        if (boardIndex < 0 || boardIndex > 9) {
            throw new InvalidBoardStringException(String.format(
                    "Invalid board index: %d", boardIndex));
        }
        if (turnValue != 0 && turnValue != 1) {
            throw new InvalidBoardStringException(String.format(
                    "Invalid turn index: %d", turnValue));
        }
    }

    /**
     * Obtains the list of move available for this board.
     */
    public List<Byte> actions() {
        List<Byte> actions = new ArrayList<>();
        if (this.subBoardIndex == 9) {
            short occupiedBoards = (short) (this.Xmeta | this.Ometa | this.Dmeta);
            for (int boardIndex = 0; boardIndex < 9; boardIndex++) {
                if (((occupiedBoards >> boardIndex) & 1) == 1) {
                    continue;
                }
                short subBoardActions = (short) (Utils.filled ^ (
                        (this.subBoards[boardIndex] & Utils.filled) | (this.subBoards[boardIndex] >> 9)));
                for (int i = 0; i < 9; i++) {
                    if (((subBoardActions >> i) & 1) == 1) {
                        actions.add((byte) (boardIndex * 9 + i));
                    }
                }
            }
        } else {
            short subBoardActions = (short) (Utils.filled ^ (
                    (this.subBoards[this.subBoardIndex] & Utils.filled) | (this.subBoards[this.subBoardIndex] >> 9)));
            for (int i = 0; i < 9; i++) {
                if (((subBoardActions >> i) & 1) == 1) {
                    actions.add((byte) (this.subBoardIndex * 9 + i));
                }
            }
        }
        return actions;
    }

    /**
     * Makes a move, returning a new board.
     * @param action The move to apply.
     */
    public Board move(byte action) {
        int boardIndex = this.subBoardIndex == 9 ? action / 9 : this.subBoardIndex;
        int[] newSubBoards = this.subBoards.clone();
        if (this.turn) {
            newSubBoards[boardIndex] = newSubBoards[boardIndex] | (1 << (action % 9));
        } else {
            newSubBoards[boardIndex] = newSubBoards[boardIndex] | (1 << (action % 9 + 9));
        }
        byte nextSubBoardIndex = (byte) (action % 9);
        short newXmeta = this.Xmeta;
        short newOmeta = this.Ometa;
        short newDmeta = this.Dmeta;
        int subBoardToConsider = newSubBoards[boardIndex];
        if (Utils.wins[subBoardToConsider & Utils.filled]) {
            newXmeta |= 1 << boardIndex;
        } else if (Utils.wins[(subBoardToConsider >> 9) & Utils.filled]) {
            newOmeta |= 1 << boardIndex;
        } else if (((subBoardToConsider & Utils.filled) | (subBoardToConsider >> 9)) == Utils.filled) {
            newDmeta |= 1 << boardIndex;
        }
        if ((((newXmeta | newOmeta | newDmeta) >> nextSubBoardIndex) & 1) == 1) {
            nextSubBoardIndex = 9;
        }
        return new Board(newSubBoards, nextSubBoardIndex, !this.turn, newXmeta, newOmeta, newDmeta);
    }

    private Utils.Side determineWinner() {
        if (Utils.wins[this.Xmeta]) {
            return Utils.Side.X;
        } else if (Utils.wins[this.Ometa]) {
            return Utils.Side.O;
        }
        if ((this.Xmeta | this.Ometa | this.Dmeta) == Utils.filled) {
            return Utils.Side.D;
        }
        return Utils.Side.U;
    }

    /**
     * Returns the turn at this board.
     */
    public boolean getTurn() {
        return this.turn;
    }

    /**
     * Get the board index that the current player has to move at.
     */
    public byte getBoardIndexToMove() {
        return this.subBoardIndex;
    }

    /**
     * Determines if another object is equal to this board.
     * Two boards are equal if they have the same board, at the same turn,
     * and the sub-board to make a move at is the same.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Board)) {
            return false;
        }

        Board board = (Board) object;
        return this.subBoardIndex == board.subBoardIndex && this.turn == board.turn
                && Arrays.equals(this.subBoards, board.subBoards);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                stringBuilder.append(String.format("%s  %s  %s\n",
                        this.getRow(this.subBoards[3 * i], j),
                        this.getRow(this.subBoards[3 * i + 1], j),
                        this.getRow(this.subBoards[3 * i + 2], j)));
            }
            if (i != 2) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the char representing the occupier of the cell
     * @param positionMask The mask representing the position in binary,
     *                     must be 1 at the position and 0 everywhere else,
     * @return {@code 'X'}, {@code 'O'} or {@code '-'}
     */
    private char getCharAtPosition(int subBoard, short positionMask) {
        if ((subBoard & positionMask) > 0) {
            return 'X';
        } else if (((subBoard >> 9) & positionMask) > 0) {
            return 'O';
        } else {
            return '-';
        }
    }

    /**
     * Returns the string representation of the row.
     * @param rowIndex The index of the row, between 0 and 2 inclusive.
     */
    public String getRow(int subBoard, int rowIndex) {
        return String.format("%c %c %c",
                this.getCharAtPosition(subBoard, (short) (1 << (3 * rowIndex))),
                this.getCharAtPosition(subBoard, (short) (1 << (3 * rowIndex + 1))),
                this.getCharAtPosition(subBoard, (short) (1 << (3 * rowIndex + 2))));
    }

    /**
     * Returns the compact string representation, for ease of trace printing.
     */
    public String toCompactString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            stringBuilder.append(String.format("%d,%d", this.subBoards[i] & Utils.filled, this.subBoards[i] >> 9));
            stringBuilder.append(" ");
        }
        stringBuilder.append(String.format("%d,%d", this.getBoardIndexToMove(), this.getTurn() ? 0 : 1));
        return stringBuilder.toString();
    }

    /**
     * Gives a heuristic score for this board,
     * to be used in PV algo.
     */
    public double evaluate() {
        switch (this.winner) {
            case X:
                return 1;
            case O:
                return -1;
            case D:
                return 0;
        }

        double total = 0;
        double[] evaluations = new double[9];
        for (int i = 0; i < 9; i++) {
            if (((this.Xmeta >> i) & 1) == 1) {
                evaluations[i] = 1;
            } else if (((this.Ometa >> i) & 1) == 1) {
                evaluations[i] = -1;
            } else if (((this.Dmeta >> i) & 1) == 1) {
                evaluations[i] = 0;
            } else {
                evaluations[i] = this.evaluateSubBoard(this.subBoards[i]);
            }
        }

        for (int i = 0; i < 3; i++) {
            // horizontal
            total += this.evaluateSubBoards(
                    evaluations[3 * i],
                    evaluations[3 * i + 1],
                    evaluations[3 * i + 2]);
            // vertical
            total += this.evaluateSubBoards(
                    evaluations[i],
                    evaluations[i + 3],
                    evaluations[i + 6]);
        }

        // diagonals
        total += this.evaluateSubBoards(evaluations[0], evaluations[4], evaluations[8]);
        total += this.evaluateSubBoards(evaluations[2], evaluations[4], evaluations[6]);

        return total / 8;
    }

    /**
     * Evaluates this sub-board, returning a heuristic value.
     * Assuming this board is not yet won.
     * To be used in PV algo.
     */
    public double evaluateSubBoard(int subBoard) {
        boolean isXNearWin = false;
        short Xboard = (short) (subBoard & Utils.filled);
        short Oboard = (short) ((subBoard >> 9) & Utils.filled);
        for (int i = 0; i < 24; i++) {
            if ((Xboard & Utils.nearWinningLines[i]) == Utils.nearWinningLines[i]
                    && (Oboard & Utils.blockingWinningLines[i]) != Utils.blockingWinningLines[i]) {
                isXNearWin = true;
                break;
            }
        }
        boolean isONearWin = false;
        for (int i = 0; i < 24; i++) {
            if ((Oboard & Utils.nearWinningLines[i]) == Utils.nearWinningLines[i]
                    && (Xboard & Utils.blockingWinningLines[i]) != Utils.blockingWinningLines[i]) {
                isONearWin = true;
                break;
            }
        }
        if (isXNearWin) {
            return isONearWin ? 0 : 0.1;
        }
        if (isONearWin) {
            return -0.1;
        }

        int XScore = 0;
        int OScore = 0;
        for (short line: Utils.winningLines) {
            if ((line & Xboard) == 0) {
                OScore++;
            }
            if ((line & Oboard) == 0) {
                XScore++;
            }
        }
        return (XScore - OScore) / 8.0 * 0.01;
    }

    private double evaluateSubBoards(double ...values) {
        double total = 0;
        double lowerBound = -1;
        double upperBound = 1;
        for (double value: values) {
            if (value > 0) {
                lowerBound = Math.max(lowerBound, value - 1);
            } else {
                upperBound = Math.min(upperBound, value + 1);
            }
            total += value;
        }
        double distToMin = (total / values.length + 1) / 2;
        return lowerBound + (upperBound - lowerBound) * distToMin;
    }
}
