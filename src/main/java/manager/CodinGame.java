package manager;

import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import board.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CodinGame {
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private final BaseAlgo algo = new MctsAlgo(true);
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

            int action;
            if (this.isFirstTurn) {
                if (opponentRow != -1) {
                    this.board = this.board.move(this.processAction(opponentRow, opponentCol));
                }
                action = this.algo.nextMoveWithTime(this.board, 980 - timeTaken);
                this.isFirstTurn = false;
            } else {
                this.board = this.board.move(this.processAction(opponentRow, opponentCol));
                action = this.algo.nextMoveWithTime(this.board, 80 - timeTaken);
            }
            System.out.println(this.toSparString(action));
            this.board = this.board.move(action);
        }
    }

    private int processAction(int row, int col) {
        int boardIndex = 3 * (row / 3) + (col / 3);
        int index = 3 * (row % 3) + (col % 3);
        return 9 * boardIndex + index;
    }

    private String toSparString(int action) {
        int boardIndex = action / 9;
        int index = action % 9;
        int row = 3 * (boardIndex / 3) + index / 3;
        int col = 3 * (boardIndex % 3) + index % 3;
        return String.format("%d %d", row, col);
    }
}
