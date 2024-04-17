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

    /**
     * Determines the winner of this board.
     * @return 0 if undetermined, 1 if X, 2 if O.
     */
    public Utils.Side winner() {
        int Xboard = 0;
        int Oboard = 0;
        for (int i = 0; i < 9; i++) {
            if (this.subBoards[i].getWinner() == Utils.Side.X) {
                Xboard += 1 << i;
            } else if (this.subBoards[i].getWinner() == Utils.Side.O) {
                Oboard += 1 << i;
            }
        }

        if ((Xboard | Oboard) == Utils.filled) {
            return Utils.Side.D;
        }
        for (short line: Utils.winningLines) {
            if ((Xboard & line) == line) {
                return Utils.Side.X;
            } else if ((Oboard & line) == line) {
                return Utils.Side.O;
            }
        }
        return Utils.Side.U;
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
}
