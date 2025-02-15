package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a 9x9 ultimate tictactoe board.
 */
public class Board {
    private final SubBoard[] subBoards;
    /** The board index that the next player must go at. 9 if the next player can go anywhere. */
    private final byte subBoardIndex;
    /** Represents the turn at this board. {@code true} if X, {@code false} otherwise. */
    private final boolean turn;
    /** Cached value of the winner */
    private final Utils.Side winner;
    /** Boards won by X. */
    private final short Xmeta;
    /** Boards won by O. */
    private final short Ometa;
    /** Boards draw. */
    private final short Dmeta;

    public Board() {
        this.subBoards = new SubBoard[] {
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
                new SubBoard(),
        };
        this.subBoardIndex = 9;
        this.turn = true;
        this.Xmeta = 0;
        this.Ometa = 0;
        this.Dmeta = 0;
        this.winner = Utils.Side.U;
    }

    private Board(SubBoard[] subBoards, byte subBoardIndex, boolean turn,
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

        SubBoard[] subBoards = new SubBoard[9];
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
                subBoards[3 * i + j] = SubBoard.fromString(subBoardLines[0][j],
                        subBoardLines[1][j], subBoardLines[2][j]);
            }
        }
        short Xmeta = 0;
        short Ometa = 0;
        short Dmeta = 0;
        for (int i = 0; i < 9; i++) {
            switch (subBoards[i].getWinner()) {
                case X:
                    Xmeta |= 1 << i;
                    break;
                case O:
                    Ometa |= 1 << i;
                    break;
                case D:
                    Dmeta |= 1 << i;
                    break;
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

        SubBoard[] subBoards = new SubBoard[9];
        for (byte i = 0; i < 9; i++) {
            subBoards[i] = SubBoard.fromCompactString(strings[i]);
        }
        short Xmeta = 0;
        short Ometa = 0;
        short Dmeta = 0;
        for (int i = 0; i < 9; i++) {
            switch (subBoards[i].getWinner()) {
                case X:
                    Xmeta |= 1 << i;
                    break;
                case O:
                    Ometa |= 1 << i;
                    break;
                case D:
                    Dmeta |= 1 << i;
                    break;
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
                short subBoardActions = this.subBoards[boardIndex].getActions();
                for (int i = 0; i < 9; i++) {
                    if (((subBoardActions >> i) & 1) == 1) {
                        actions.add((byte) (boardIndex * 9 + i));
                    }
                }
            }
        } else {
            short subBoardActions = this.subBoards[this.subBoardIndex].getActions();
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
        SubBoard[] newSubBoards = this.subBoards.clone();
        newSubBoards[boardIndex] = newSubBoards[boardIndex].move((byte) (action % 9), this.turn);
        byte nextSubBoardIndex = (byte) (action % 9);
        short newXmeta = this.Xmeta;
        short newOmeta = this.Ometa;
        short newDmeta = this.Dmeta;
        switch (newSubBoards[boardIndex].getWinner()) {
            case D:
                newDmeta |= (1 << boardIndex);
                break;
            case X:
                newXmeta |= (1 << boardIndex);
                break;
            case O:
                newOmeta |= (1 << boardIndex);
                break;
        }
        if (newSubBoards[nextSubBoardIndex].getWinner() != Utils.Side.U) {
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
     * Determines the winner of this board.
     * Caches the value.
     */
    public Utils.Side winner() {
        return this.winner;
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
                        this.subBoards[3 * i].getRow(j),
                        this.subBoards[3 * i + 1].getRow(j),
                        this.subBoards[3 * i + 2].getRow(j)));
            }
            if (i != 2) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the compact string representation, for ease of trace printing.
     */
    public String toCompactString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            stringBuilder.append(this.subBoards[i].toCompactString());
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
                evaluations[i] = this.subBoards[i].evaluate();
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
