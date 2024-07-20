package manager;

import algo.BaseAlgo;
import algo.mcts.parallel.ParallelMctsAlgo;
import board.Board;
import board.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CodinGame {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final BaseAlgo algo = new ParallelMctsAlgo();
    private boolean isFirstTurn = true;
    private Board board = new Board();

    /**
     * Receives user input and outputs the moves.
     * To be called only once.
     */
    public void run() {
        while (true) {
            this.algo.ponder();
            int opponentRow;
            int opponentCol;
            try {
                String[] inputs = in.readLine().split(" ");
                opponentRow = Integer.parseInt(inputs[0]);
                opponentCol = Integer.parseInt(inputs[1]);
                int validActionCount = Integer.parseInt(in.readLine());
                in.skip(validActionCount * 4);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            long startTime = System.currentTimeMillis();
            this.algo.stopPondering();
            int timeTaken = (int) (System.currentTimeMillis() - startTime);

            Move action;
            if (this.isFirstTurn) {
                if (opponentRow != -1) {
                    this.board = this.board.move(new Move((byte) opponentRow, (byte) opponentCol));
                }
                action = this.algo.nextMoveWithTime(this.board, 980 - timeTaken);
                this.isFirstTurn = false;
            } else {
                this.board = this.board.move(new Move((byte) opponentRow, (byte) opponentCol));
                action = this.algo.nextMoveWithTime(this.board, 80 - timeTaken);
            }
            System.out.println(action.sparString());
            this.board = this.board.move(action);
        }
    }
}
