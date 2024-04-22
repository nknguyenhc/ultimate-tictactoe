package board;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SubBoardTest {
    @Test
    public void testWinner() {
        SubBoard board = new SubBoard();
        board = board.move(0, 0, true);
        board = board.move(1, 1, true);
        assertEquals(Utils.Side.U, board.getWinner());

        board = board.move(2, 2, true);
        assertEquals(Utils.Side.X, board.getWinner());

        board = new SubBoard();
        board = board.move(0, 0, true);
        board = board.move(1, 1, true);
        board = board.move(2, 2, false);
        assertEquals(Utils.Side.U, board.getWinner());

        board = board.move(1, 2, false);
        board = board.move(0, 2, false);
        assertEquals(Utils.Side.O, board.getWinner());
    }

    @Test
    public void testStringRepresentation() {
        SubBoard board = new SubBoard();
        board = board.move(0, 1, true);
        board = board.move(2, 0, false);
        assertEquals("- X -\n- - -\nO - -", board.toString());
    }

    @Test
    public void testImmutability() {
        SubBoard board = new SubBoard();
        SubBoard newBoard = board.move(1, 0, true);
        newBoard = newBoard.move(1, 1, true);
        newBoard.move(1, 2, true);
        assertEquals(board, new SubBoard());
        assertEquals(Utils.Side.U, board.getWinner());
    }

    @Test
    public void testDraw() {
        SubBoard board = new SubBoard();
        board = board.move(0, 0, true);
        board = board.move(0, 1, false);
        board = board.move(0, 2, true);
        board = board.move(1, 0, true);
        board = board.move(1, 1, false);
        board = board.move(1, 2, true);
        board = board.move(2, 0, false);
        board = board.move(2, 1, true);
        assertEquals(Utils.Side.U, board.getWinner());
        board = board.move(2, 2, false);
        assertEquals(Utils.Side.D, board.getWinner());
    }

    @Test
    public void testFromString() throws Exception {
        SubBoard board = SubBoard.fromString("X - -", "- O -", "O - X");
        SubBoard expectedBoard = new SubBoard();
        expectedBoard = expectedBoard.move(0, 0, true);
        expectedBoard = expectedBoard.move(1, 1, false);
        expectedBoard = expectedBoard.move(2, 0, false);
        expectedBoard = expectedBoard.move(2, 2, true);
        assertEquals(expectedBoard, board);
    }

    @Test
    public void testFromCompactString() throws Exception {
        SubBoard board = SubBoard.fromCompactString("257,80");
        SubBoard expectedBoard = new SubBoard();
        expectedBoard = expectedBoard.move(0, 0, true);
        expectedBoard = expectedBoard.move(1, 1, false);
        expectedBoard = expectedBoard.move(2, 0, false);
        expectedBoard = expectedBoard.move(2, 2, true);
        assertEquals(expectedBoard, board);
    }
}
