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
    private Utils.Side winner = null;

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
    }

    private Board(SubBoard[] subBoards, byte subBoardIndex, boolean turn) {
        this.subBoards = subBoards;
        this.subBoardIndex = subBoardIndex;
        this.turn = turn;
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
        for (int i = 0; i < 3; i++) {
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
            for (int j = 0; j < 3; j++) {
                subBoards[3 * i + j] = SubBoard.fromString(subBoardLines[0][j],
                        subBoardLines[1][j], subBoardLines[2][j]);
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
        return new Board(subBoards, (byte) boardIndex, turnValue == 0);
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
        for (int i = 0; i < 9; i++) {
            subBoards[i] = SubBoard.fromCompactString(strings[i]);
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
        return new Board(subBoards, (byte) boardIndex, turnValue == 0);
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
    public List<Move> actions() {
        byte rowFloor;
        byte rowCeil;
        byte colFloor;
        byte colCeil;
        if (this.subBoardIndex == 9) {
            rowFloor = 0;
            rowCeil = 9;
            colFloor = 0;
            colCeil = 9;
        } else {
            rowFloor = (byte) (3 * (this.subBoardIndex / 3));
            rowCeil = (byte) (rowFloor + 3);
            colFloor = (byte) (3 * (this.subBoardIndex % 3));
            colCeil = (byte) (colFloor + 3);
        }

        ArrayList<Move> actions = new ArrayList<>();
        for (byte row = rowFloor; row < rowCeil; row++) {
            for (byte col = colFloor; col < colCeil; col++) {
                int boardIndex = 3 * (row / 3) + col / 3;
                if (this.subBoards[boardIndex].getWinner() != Utils.Side.U) {
                    continue;
                }

                int testRow = row % 3;
                int testCol = col % 3;
                if (this.subBoards[boardIndex].getOccupation(testRow, testCol) == Utils.Side.U) {
                    actions.add(new Move(row, col));
                }
            }
        }
        return actions;
    }

    /**
     * Makes a move, returning a new board.
     * @param action The move to apply.
     */
    public Board move(Move action) {
        int boardIndex;
        if (this.subBoardIndex == 9) {
            boardIndex = 3 * (action.row / 3) + action.col / 3;
        } else {
            boardIndex = this.subBoardIndex;
        }
        SubBoard[] newSubBoards = this.subBoards.clone();
        newSubBoards[boardIndex] = newSubBoards[boardIndex].move((action.row % 3), (action.col % 3), this.turn);
        byte nextSubBoardIndex = (byte) (3 * (action.row % 3) + (action.col % 3));
        if (newSubBoards[nextSubBoardIndex].getWinner() != Utils.Side.U) {
            nextSubBoardIndex = 9;
        }
        return new Board(newSubBoards, nextSubBoardIndex, !this.turn);
    }

    private Utils.Side determineWinner() {
        int Xboard = 0;
        int Oboard = 0;
        int Dboard = 0;
        for (int i = 0; i < 9; i++) {
            switch (this.subBoards[i].getWinner()) {
                case X:
                    Xboard += 1 << i;
                    break;
                case O:
                    Oboard += 1 << i;
                    break;
                case D:
                    Dboard += 1 << i;
                    break;
            }
        }

        for (short line: Utils.winningLines) {
            if ((Xboard & line) == line) {
                return Utils.Side.X;
            } else if ((Oboard & line) == line) {
                return Utils.Side.O;
            }
        }
        if ((Xboard | Oboard | Dboard) == Utils.filled) {
            return Utils.Side.D;
        }
        return Utils.Side.U;
    }

    /**
     * Determines the winner of this board.
     * Caches the value.
     * @return 0 if undetermined, 1 if X, 2 if O.
     */
    public Utils.Side winner() {
        if (this.winner == null) {
            this.winner = this.determineWinner();
        }
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
}
