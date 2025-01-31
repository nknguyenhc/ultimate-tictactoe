package board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class BoardTest {
    private static <T> void assertListHasSameElements(List<T> expected, List<T> actual) {
        HashSet<T> expectedSet = new HashSet<>(expected);
        HashSet<T> actualSet = new HashSet<>(actual);
        assertEquals(expectedSet, actualSet);
    }
    @Test
    public void test() {
        List<Byte> allMoves = new ArrayList<>();
        for (byte i = 0; i < 81; i++) {
            allMoves.add(i);
        }

        Board board = new Board();
        assertListHasSameElements(allMoves, board.actions());

        board = board.move((byte) 40);
        assertListHasSameElements(List.of(
                (byte) 36, (byte) 37, (byte) 38,
                (byte) 39, (byte) 41, (byte) 42,
                (byte) 43, (byte) 44), board.actions());

        board = board.move((byte) 37);
        assertListHasSameElements(List.of(
                (byte) 9, (byte) 10, (byte) 11,
                (byte) 12, (byte) 13, (byte) 14,
                (byte) 15, (byte) 16, (byte) 17), board.actions());

        board = board.move((byte) 10);
        assertListHasSameElements(List.of(
                (byte) 9, (byte) 11, (byte) 12,
                (byte) 13, (byte) 14, (byte) 15,
                (byte) 16, (byte) 17), board.actions());

        board = board.move((byte) 13);
        assertListHasSameElements(List.of(
                (byte) 36, (byte) 38, (byte) 39,
                (byte) 41, (byte) 42, (byte) 43,
                (byte) 44), board.actions());

        board = board.move((byte) 36);
        assertListHasSameElements(List.of(
                (byte) 0, (byte) 1, (byte) 2,
                (byte) 3, (byte) 4, (byte) 5,
                (byte) 6, (byte) 7, (byte) 8), board.actions());

        board = board.move((byte) 4);
        assertListHasSameElements(List.of(
                (byte) 38, (byte) 39, (byte) 41,
                (byte) 42, (byte) 43, (byte) 44), board.actions());

        board = board.move((byte) 44);
        assertListHasSameElements(List.of(
                (byte) 72, (byte) 73, (byte) 74,
                (byte) 75, (byte) 76, (byte) 77,
                (byte) 78, (byte) 79, (byte) 80), board.actions());

        board = board.move((byte) 76);
        HashSet<Byte> expectedMoves = new HashSet<>(allMoves);
        expectedMoves.remove((byte) 4);
        expectedMoves.remove((byte) 76);
        expectedMoves.remove((byte) 10);
        expectedMoves.remove((byte) 13);
        for (int i = 36; i <= 44; i++) {
            expectedMoves.remove((byte) i);
        }
        assertListHasSameElements(new ArrayList<>(expectedMoves), board.actions());
    }

    @Test
    public void testImmutability() {
        Board board = new Board();
        board.move((byte) 40);
        assertEquals(new Board(), board);
    }

    @Test
    public void testFromString() throws Exception {
        Board board = Board.fromString(
                "- - X  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - O -\n" +
                "\n" +
                "- - -  O - -  - - -\n" +
                "- - -  - X -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "\n" +
                "7,0");
        assertEquals(
                "- - X  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - O -\n" +
                "\n" +
                "- - -  O - -  - - -\n" +
                "- - -  - X -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - - -\n",
                board.toString());
        assertEquals(7, board.getBoardIndexToMove());
        assertTrue(board.getTurn());
    }

    @Test
    public void testFromCompactString() throws Exception {
        Board board = Board.fromCompactString("4,0 0,0 0,128 0,0 16,1 0,0 0,0 0,0 0,0 7,0");
        assertEquals(
                "- - X  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - O -\n" +
                "\n" +
                "- - -  O - -  - - -\n" +
                "- - -  - X -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - - -\n" +
                "- - -  - - -  - - -\n",
                board.toString());
        assertEquals(7, board.getBoardIndexToMove());
        assertTrue(board.getTurn());
    }

    @Test
    public void testToCompactString() throws Exception {
        String string = "4,0 0,0 0,128 0,0 16,1 0,0 0,0 0,0 0,0 7,0";
        Board board = Board.fromCompactString(string);
        assertEquals(string, board.toCompactString());
    }
}
