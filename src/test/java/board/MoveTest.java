package board;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MoveTest {
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
        Move move = new Move((byte) 6, (byte) 3);
        assertTrue(move.isEquivalentTo(new Move((byte) 6, (byte) 5), morphable));
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
        Move move = new Move((byte) 4, (byte) 3);
        assertTrue(move.isEquivalentTo(new Move((byte) 5, (byte) 4), morphable));
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
        Move move = new Move((byte) 7, (byte) 5);
        assertTrue(move.isEquivalentTo(new Move((byte) 1, (byte) 5), morphable));
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
        Move move = new Move((byte) 5, (byte) 3);
        assertTrue(move.isEquivalentTo(new Move((byte) 3, (byte) 5), morphable));
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
        Move move = new Move((byte) 0, (byte) 5);
        assertTrue(move.isEquivalentTo(new Move((byte) 5, (byte) 8), morphable));
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
        Move move = new Move((byte) 0, (byte) 5);
        assertTrue(move.isEquivalentTo(new Move((byte) 3, (byte) 0), morphable));
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
        Move move = new Move((byte) 1, (byte) 5);
        assertTrue(move.isEquivalentTo(new Move((byte) 7, (byte) 3), morphable));
    }
}
