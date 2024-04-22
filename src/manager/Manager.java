package manager;

import algo.BaseAlgo;
import board.Board;
import board.InvalidBoardStringException;
import board.Move;
import board.Utils;

import java.util.Scanner;

public class Manager {
    private final Scanner scanner = new Scanner(System.in);
    private Board board = new Board();
    private BaseAlgo algo;
    private final boolean includeTrace;

    public Manager(boolean includeTrace) {
        this.includeTrace = includeTrace;
    }

    public void run(BaseAlgo algo) {
        this.algo = algo;
        this.printWelcomeMessage();
        boolean humanTurn = this.determineTurn();
        while (this.board.winner() == Utils.Side.U) {
            System.out.println(this.board);
            if (this.board.getTurn() == humanTurn) {
                this.humanTurn();
            } else {
                this.algoTurn();
            }
        }

        switch (this.board.winner()) {
            case X:
                if (humanTurn) {
                    this.notifyHumanWin();
                } else {
                    this.notifyAlgoWin();
                }
                break;
            case O:
                if (humanTurn) {
                    this.notifyAlgoWin();
                } else {
                    this.notifyHumanWin();
                }
                break;
            case D:
                this.notifyDraw();
                break;
        }
    }

    public void runWithStartBoard(BaseAlgo algo) {
        System.out.print("Board to start (compact form): ");
        String line = this.scanner.nextLine();
        while (true) {
            try {
                this.board = Board.fromCompactString(line);
                break;
            } catch (InvalidBoardStringException e) {
                System.out.println(e.getMessage());
                System.out.print("Please key in your board again (compact form): ");
                line = this.scanner.nextLine();
            }
        }
        this.run(algo);
    }

    private void printWelcomeMessage() {
        System.out.println("Welcome! This is a programme to test out the engine.");
        System.out.println("Make sure that the algo indicated in Main class is the algo you want to test.");
    }

    private boolean determineTurn() {
        System.out.print("Please indicate if you want to go first (X), or go second (O): (X/O) ");
        String line = this.scanner.nextLine();
        while (true) {
            switch (line) {
                case "X":
                    return true;
                case "O":
                    return false;
                default:
                    System.out.print("Invalid input, please indicate again: (X/O) ");
                    line = this.scanner.nextLine();
            }
        }
    }

    private void humanTurn() {
        this.printBoardIndexToMove();
        System.out.print("Please indicate your move, in the format R, C : ");
        String line = this.scanner.nextLine();
        Move move;
        while (true) {
            try {
                move = this.parseMove(line);
                break;
            } catch (InvalidMoveStringException e) {
                System.out.println(e.getMessage());
                System.out.print("Please indicate your move, in the format R, C : ");
                line = this.scanner.nextLine();
            }
        }
        this.board = this.board.move(move);
    }

    private void printBoardIndexToMove() {
        int boardIndex = board.getBoardIndexToMove();
        if (boardIndex == 9) {
            System.out.println("Board to move: any board");
            return;
        }

        int row = boardIndex / 3 + 1;
        int col = boardIndex % 3 + 1;
        System.out.printf("Board to move: (%d, %d)%n", row, col);
    }

    private Move parseMove(String moveString) throws InvalidMoveStringException {
        String[] numbers = moveString.split(", ");
        if (numbers.length != 2) {
            throw new InvalidMoveStringException();
        }

        byte row;
        byte col;
        try {
            row = (byte) (Byte.parseByte(numbers[0]) - 1);
            col = (byte) (Byte.parseByte(numbers[1]) - 1);
        } catch (NumberFormatException e) {
            throw new InvalidMoveStringException();
        }

        Move move = new Move(row, col);
        if (!this.board.actions().contains(move)) {
            throw new InvalidMoveStringException();
        }
        return move;
    }

    private void algoTurn() {
        Move move = this.algo.nextMove(this.board);
        System.out.printf("Algo chose: %s%n", move);
        this.board = this.board.move(move);

        if (this.includeTrace) {
            System.out.println(this.algo.trace());
            System.out.printf("Algo chose: %s%n", move);
        }
    }

    private void notifyHumanWin() {
        System.out.println("You won!");
    }

    private void notifyAlgoWin() {
        System.out.println("Algo won!");
    }

    private void notifyDraw() {
        System.out.println("Draw!");
    }
}
