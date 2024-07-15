package algo.pv;

import algo.mcts.MctsAlgo;
import board.Board;

import org.junit.Test;

public class PvNodeTest {
    private final int time = 5000;

    private void testBoard(String boardString) throws Exception {
        Board board = Board.fromCompactString(boardString);

        MctsAlgo mctsAlgo = new MctsAlgo();
        mctsAlgo.nextMoveWithTime(board, time);
        double expected = mctsAlgo.evaluate();

        mctsAlgo = new MctsAlgo();
        mctsAlgo.nextMoveWithTime(board, time);
        double expected2 = mctsAlgo.evaluate();

        PvNode node = new PvNode(board);
        double actual = node.evaluate();

        System.out.printf("Expected: %.3f, Expected2: %.3f, Actual: %.3f\n", expected, expected2, actual);
    }

    @Test
    public void testOpening() throws Exception {
        testBoard("8,0 0,0 0,0 16,8 256,16 64,0 128,64 0,1 0,32 0,0");
        testBoard("72,0 0,0 4,32 16,8 256,16 65,0 128,68 0,1 0,32 0,1");
        testBoard("72,1 0,0 4,32 16,8 256,16 65,0 128,68 0,1 0,32 0,0");
    }

    @Test
    public void testMid() throws Exception {
        testBoard("74,1 32,2 4,32 16,8 256,16 65,8 128,68 0,1 0,32 3,0");
        testBoard("74,1 32,2 4,32 144,8 256,16 65,8 128,68 0,1 0,32 7,1");
        testBoard("74,1 36,2 132,48 144,8 256,16 65,8 128,68 0,7 0,32 4,0");
    }

    @Test
    public void testEndgame() throws Exception {
        testBoard("74,257 45,2 132,48 144,42 288,16 65,10 128,68 0,7 8,32 5,0");
        testBoard("74,257 45,2 132,48 144,42 288,16 97,266 128,68 0,7 264,32 8,1");
        testBoard("74,257 45,2 132,48 144,42 288,16 97,266 128,68 0,7 264,34 1,0");
    }
}
