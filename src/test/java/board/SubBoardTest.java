package board;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;

public class SubBoardTest {
    @Test
    public void testWinner() {
        SubBoard board = new SubBoard((byte) 0);
        board = board.move((byte) 0, true);
        board = board.move((byte) 4, true);
        assertEquals(Utils.Side.U, board.getWinner());
        assertEquals(new HashSet<>(List.of(
                (byte) 1, (byte) 2, (byte) 3,
                (byte) 5, (byte) 6, (byte) 7,
                (byte) 8
        )), new HashSet<>(board.getActions()));

        board = board.move((byte) 8, true);
        assertEquals(Utils.Side.X, board.getWinner());
        assertEquals(List.of(), board.getActions());

        board = new SubBoard((byte) 0);
        board = board.move((byte) 0, true);
        board = board.move((byte) 4, true);
        board = board.move((byte) 8, false);
        assertEquals(Utils.Side.U, board.getWinner());
        assertEquals(new HashSet<>(List.of(
                (byte) 1, (byte) 2, (byte) 3,
                (byte) 5, (byte) 6, (byte) 7
        )), new HashSet<>(board.getActions()));

        board = board.move((byte) 5, false);
        board = board.move((byte) 2, false);
        assertEquals(Utils.Side.O, board.getWinner());
        assertEquals(List.of(), board.getActions());
    }

    @Test
    public void testStringRepresentation() {
        SubBoard board = new SubBoard((byte) 0);
        board = board.move((byte) 1, true);
        board = board.move((byte) 6, false);
        assertEquals("- X -\n- - -\nO - -", board.toString());
    }

    @Test
    public void testImmutability() {
        SubBoard board = new SubBoard((byte) 4);
        SubBoard newBoard = board.move((byte) 3, true);
        newBoard = newBoard.move((byte) 4, true);
        newBoard.move((byte) 5, true);
        assertEquals(board, new SubBoard((byte) 4));
        assertEquals(Utils.Side.U, board.getWinner());
    }

    @Test
    public void testDraw() {
        SubBoard board = new SubBoard((byte) 0);
        board = board.move((byte) 0, true);
        board = board.move((byte) 1, false);
        board = board.move((byte) 2, true);
        board = board.move((byte) 3, true);
        board = board.move((byte) 4, false);
        board = board.move((byte) 5, true);
        board = board.move((byte) 6, false);
        board = board.move((byte) 7, true);
        assertEquals(Utils.Side.U, board.getWinner());
        assertEquals(List.of((byte) 8), board.getActions());
        board = board.move((byte) 8, false);
        assertEquals(Utils.Side.D, board.getWinner());
        assertEquals(List.of(), board.getActions());
    }

    @Test
    public void testFromString() throws Exception {
        SubBoard board = SubBoard.fromString("X - -", "- O -", "O - X", (byte) 3);
        SubBoard expectedBoard = new SubBoard((byte) 3);
        expectedBoard = expectedBoard.move((byte) 0, true);
        expectedBoard = expectedBoard.move((byte) 4, false);
        expectedBoard = expectedBoard.move((byte) 6, false);
        expectedBoard = expectedBoard.move((byte) 8, true);
        assertEquals(expectedBoard, board);
    }

    @Test
    public void testFromCompactString() throws Exception {
        SubBoard board = SubBoard.fromCompactString("257,80", (byte) 1);
        SubBoard expectedBoard = new SubBoard((byte) 1);
        expectedBoard = expectedBoard.move((byte) 0, true);
        expectedBoard = expectedBoard.move((byte) 4, false);
        expectedBoard = expectedBoard.move((byte) 6, false);
        expectedBoard = expectedBoard.move((byte) 8, true);
        assertEquals(expectedBoard, board);
    }

    @Test
    public void testToCompactString() throws Exception {
        String string = "257,80";
        SubBoard subBoard = SubBoard.fromCompactString(string, (byte) 0);
        assertEquals(string, subBoard.toCompactString());
    }
}
