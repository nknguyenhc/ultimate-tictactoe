package board;

import static org.junit.Assert.assertEquals;

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
}
