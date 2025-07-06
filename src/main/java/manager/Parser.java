package manager;

import board.Board;
import board.InvalidBoardStringException;

import java.util.Scanner;

public class Parser {
    Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("Welcome! This programme helps you parse compact board " +
                "and prints out the full board.");
        System.out.println("Key in \"exit\" to exit this programme.");
        System.out.print("Compact board: ");
        String line = scanner.nextLine();
        while (!line.equals("exit")) {
            try {
                Board board = Board.fromCompactString(line);
                this.printFullBoard(board);
            } catch (InvalidBoardStringException e) {
                System.out.println(e.getMessage());
            }
            System.out.print("Compact board: ");
            line = scanner.nextLine();
        }
    }

    private void printFullBoard(Board board) {
        System.out.println("Full board:");
        System.out.println(board);

        int boardIndex = board.getBoardIndexToMove();
        if (boardIndex == 9) {
            System.out.println("Board to move: any board");
        } else {
            int row = boardIndex / 3 + 1;
            int col = boardIndex % 3 + 1;
            System.out.printf("Board to move: (%d, %d)%n", row, col);
        }
        System.out.printf("Turn: %s%n", board.turn ? "X" : "O");
    }
}
