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
        List<Integer> allMoves = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            allMoves.add(i);
        }

        Board board = new Board();
        assertListHasSameElements(allMoves, board.actions());

        board = board.move(40);
        assertListHasSameElements(List.of(36, 37, 38, 39, 41, 42, 43, 44), board.actions());

        board = board.move(37);
        assertListHasSameElements(List.of(9, 10, 11, 12, 13, 14, 15, 16, 17), board.actions());

        board = board.move(10);
        assertListHasSameElements(List.of(9, 11, 12, 13, 14, 15, 16, 17), board.actions());

        board = board.move(13);
        assertListHasSameElements(List.of(36, 38, 39, 41, 42, 43, 44), board.actions());

        board = board.move(36);
        assertListHasSameElements(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), board.actions());

        board = board.move(4);
        assertListHasSameElements(List.of(38, 39, 41, 42, 43, 44), board.actions());

        board = board.move(44);
        assertListHasSameElements(List.of(72, 73, 74, 75, 76, 77, 78, 79, 80), board.actions());

        board = board.move(76);
        HashSet<Integer> expectedMoves = new HashSet<>(allMoves);
        expectedMoves.remove(4);
        expectedMoves.remove(76);
        expectedMoves.remove(10);
        expectedMoves.remove(13);
        for (int i = 36; i <= 44; i++) {
            expectedMoves.remove(i);
        }
        assertListHasSameElements(new ArrayList<>(expectedMoves), board.actions());
    }

    @Test
    public void testImmutability() {
        Board board = new Board();
        board.move(40);
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
