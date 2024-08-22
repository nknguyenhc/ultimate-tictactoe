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
        List<Move> allMoves = new ArrayList<>();
        for (byte i = 0; i < 9; i++) {
            for (byte j = 0; j < 9; j++) {
                allMoves.add(new Move(i, j));
            }
        }

        Board board = new Board();
        assertListHasSameElements(allMoves, board.actions());

        board = board.move(new Move((byte) 4, (byte) 4));
        assertListHasSameElements(List.of(
                new Move((byte) 3, (byte) 3),
                new Move((byte) 3, (byte) 4),
                new Move((byte) 3, (byte) 5),
                new Move((byte) 4, (byte) 3),
                new Move((byte) 4, (byte) 5),
                new Move((byte) 5, (byte) 3),
                new Move((byte) 5, (byte) 4),
                new Move((byte) 5, (byte) 5)
        ), board.actions());

        board = board.move(new Move((byte) 3, (byte) 4));
        assertListHasSameElements(List.of(
                new Move((byte) 0, (byte) 3),
                new Move((byte) 0, (byte) 4),
                new Move((byte) 0, (byte) 5),
                new Move((byte) 1, (byte) 3),
                new Move((byte) 1, (byte) 4),
                new Move((byte) 1, (byte) 5),
                new Move((byte) 2, (byte) 3),
                new Move((byte) 2, (byte) 4),
                new Move((byte) 2, (byte) 5)
        ), board.actions());

        board = board.move(new Move((byte) 0, (byte) 4));
        assertListHasSameElements(List.of(
                new Move((byte) 0, (byte) 3),
                new Move((byte) 0, (byte) 5),
                new Move((byte) 1, (byte) 3),
                new Move((byte) 1, (byte) 4),
                new Move((byte) 1, (byte) 5),
                new Move((byte) 2, (byte) 3),
                new Move((byte) 2, (byte) 4),
                new Move((byte) 2, (byte) 5)
        ), board.actions());

        board = board.move(new Move((byte) 1, (byte) 4));
        assertListHasSameElements(List.of(
                new Move((byte) 3, (byte) 3),
                new Move((byte) 3, (byte) 5),
                new Move((byte) 4, (byte) 3),
                new Move((byte) 4, (byte) 5),
                new Move((byte) 5, (byte) 3),
                new Move((byte) 5, (byte) 4),
                new Move((byte) 5, (byte) 5)
        ), board.actions());

        board = board.move(new Move((byte) 3, (byte) 3));
        assertListHasSameElements(List.of(
                new Move((byte) 0, (byte) 0),
                new Move((byte) 0, (byte) 1),
                new Move((byte) 0, (byte) 2),
                new Move((byte) 1, (byte) 0),
                new Move((byte) 1, (byte) 1),
                new Move((byte) 1, (byte) 2),
                new Move((byte) 2, (byte) 0),
                new Move((byte) 2, (byte) 1),
                new Move((byte) 2, (byte) 2)
        ), board.actions());

        board = board.move(new Move((byte) 1, (byte) 1));
        assertListHasSameElements(List.of(
                new Move((byte) 3, (byte) 5),
                new Move((byte) 4, (byte) 3),
                new Move((byte) 4, (byte) 5),
                new Move((byte) 5, (byte) 3),
                new Move((byte) 5, (byte) 4),
                new Move((byte) 5, (byte) 5)
        ), board.actions());

        board = board.move(new Move((byte) 5, (byte) 5));
        assertListHasSameElements(List.of(
                new Move((byte) 6, (byte) 6),
                new Move((byte) 6, (byte) 7),
                new Move((byte) 6, (byte) 8),
                new Move((byte) 7, (byte) 6),
                new Move((byte) 7, (byte) 7),
                new Move((byte) 7, (byte) 8),
                new Move((byte) 8, (byte) 6),
                new Move((byte) 8, (byte) 7),
                new Move((byte) 8, (byte) 8)
        ), board.actions());

        board = board.move(new Move((byte) 7, (byte) 7));
        List<Move> expectedMoves = new ArrayList<>(allMoves);
        expectedMoves.remove(new Move((byte) 1, (byte) 1));
        expectedMoves.remove(new Move((byte) 7, (byte) 7));
        expectedMoves.remove(new Move((byte) 0, (byte) 4));
        expectedMoves.remove(new Move((byte) 1, (byte) 4));
        for (byte i = 3; i <= 5; i++) {
            for (byte j = 3; j <= 5; j++) {
                expectedMoves.remove(new Move(i, j));
            }
        }
        assertListHasSameElements(expectedMoves, board.actions());
    }

    @Test
    public void testImmutability() {
        Board board = new Board();
        board.move(new Move((byte) 4, (byte) 4));
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

    @Test
    public void testNorthMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  - O -  X - -\n" +
                "O X -  - X -  - X O\n" +
                "O - -  X - X  - - O\n" +
                "\n" +
                "- - -  - X -  - - -\n" +
                "O O O  X O X  O O O\n" +
                "X - -  - - -  - - X\n" +
                "\n" +
                "X X O  - - -  O X X\n" +
                "X O O  - O -  O O X\n" +
                "- - X  - - -  X - -\n" +
                "\n" +
                "4,0");
        assertTrue(morphable.isMorphable(Utils.Morph.NORTH));
    }

    @Test
    public void testNortheastMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  - O O  X - -\n" +
                "O X X  - O X  O O -\n" +
                "O - -  - - O  - O X\n" +
                "\n" +
                "X - -  O O X  O X O\n" +
                "O - X  - - O  - O O\n" +
                "- - -  - - O  - - -\n" +
                "\n" +
                "O - -  - X -  - X X\n" +
                "X X -  - - -  - X -\n" +
                "X X O  - O X  O O -\n" +
                "\n" +
                "8,0");
        assertTrue(morphable.isMorphable(Utils.Morph.NORTHEAST));
    }

    @Test
    public void testEastMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  O O -  - - X\n" +
                "- X O  X - -  O O O\n" +
                "X O -  O - -  - - -\n" +
                "\n" +
                "- - O  O - -  X O X\n" +
                "X O X  O O -  X - -\n" +
                "- - O  O - -  X O X\n" +
                "\n" +
                "X O -  O - -  - - -\n" +
                "- X O  X - -  O O O\n" +
                "- - X  O O -  - - X\n" +
                "\n" +
                "1,0");
        assertTrue(morphable.isMorphable(Utils.Morph.EAST));
    }

    @Test
    public void testSoutheastMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  O O -  - - X\n" +
                "- - O  O X -  - - X\n" +
                "X O O  X - -  O - -\n" +
                "\n" +
                "O O X  X - -  O X O\n" +
                "O X -  - O X  - - -\n" +
                "- - -  - X -  - X -\n" +
                "\n" +
                "- - O  O - -  X O O\n" +
                "- - -  X - X  O - -\n" +
                "X X -  O - -  O - -\n" +
                "\n" +
                "4,0");
        assertTrue(morphable.isMorphable(Utils.Morph.SOUTHEAST));
    }

    @Test
    public void testClockwiseMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  X O -  X O -\n" +
                "O - -  - X -  O - -\n" +
                "X O O  - X -  O - X\n" +
                "\n" +
                "- - -  O X O  - - X\n" +
                "O X X  X - X  X X O\n" +
                "X - -  O X O  - - -\n" +
                "\n" +
                "X - O  - X -  O O X\n" +
                "- - O  - X -  - - O\n" +
                "- O X  - O X  X - -\n" +
                "\n" +
                "4,0");
        assertTrue(morphable.isMorphable(Utils.Morph.CLOCKWISE));
    }

    @Test
    public void testAnticlockwiseMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  X O -  X O -\n" +
                "O - -  - X -  O - -\n" +
                "X O O  - X -  O - X\n" +
                "\n" +
                "- - -  O X O  - - X\n" +
                "O X X  X - X  X X O\n" +
                "X - -  O X O  - - -\n" +
                "\n" +
                "X - O  - X -  O O X\n" +
                "- - O  - X -  - - O\n" +
                "- O X  - O X  X - -\n" +
                "\n" +
                "4,0");
        assertTrue(morphable.isMorphable(Utils.Morph.ANTICLOCKWISE));
    }

    @Test
    public void testSemicircleMorphable() throws Exception {
        Board morphable = Board.fromString(
                "- - X  X O -  X - O\n" +
                "- X -  O - -  X - -\n" +
                "- - O  O - -  - - -\n" +
                "\n" +
                "- - O  X X O  X - -\n" +
                "O - -  - - -  - - O\n" +
                "- - X  O X X  O - -\n" +
                "\n" +
                "- - -  - - O  O - -\n" +
                "- - X  - - O  - X -\n" +
                "O - X  - O X  X - -\n" +
                "\n" +
                "6,0");
        assertTrue(morphable.isMorphable(Utils.Morph.SEMICIRCLE));
    }
}
