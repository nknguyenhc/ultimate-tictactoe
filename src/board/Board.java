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
}
