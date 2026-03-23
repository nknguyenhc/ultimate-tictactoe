package manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import algo.mcts.MctsAlgo;
import board.Board;
import board.InvalidBoardStringException;

public class FYPEvaluator {
    private final int time = 5000;

    public void evaluate(String infile, String outfile) {
        String log;
        try {
            Path filePath = Paths.get(infile);
            log = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String[] games = log.split("\n------\n");
        if (games.length != 500) {
            throw new RuntimeException(String.format("Need 500 games, got %d", games.length));
        }

        double increase = 0;
        int increaseCount = 0;
        double decrease = 0;
        int decreaseCount = 0;
        double stayTheSame = 0;
        int stayTheSameCount = 0;
        for (String game: games) {
            String[] parts = game.split("Response: ");
            if (parts.length != 2) {
                throw new RuntimeException(String.format("Invalid game, need \"Response\": %s", game));
            }
            Board board = this.boardFromPrompt(parts[0]);
            byte action;
            try {
                action = this.actionFromResponse(parts[1]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (!board.actions().contains(action)) {
                System.out.println("Invalid");
                continue;
            }

            MctsAlgo algo = new MctsAlgo();
            algo.nextMoveWithTime(board, time);
            double before = algo.evaluate();

            board = board.move(action);
            algo = new MctsAlgo();
            algo.nextMoveWithTime(board, time);
            double after = - algo.evaluate();

            if (after <= before + 0.05 && after >= before - 0.05) {
                stayTheSame += after - before;
                stayTheSameCount++;
                System.out.printf("Stay the same: %.3f\n", after - before);
            } else if (after >= before) {
                increase += after - before;
                increaseCount++;
                System.out.printf("Increase: %.3f\n", after - before);
            } else {
                decrease += before - after;
                decreaseCount++;
                System.out.printf("Decrease: %.3f\n", after - before);
            }
        }

        String outLog = String.format(
                "Avg increase: %.3f\nIncrease count: %d\nAvg decrease: %.3f\nDecrease count: %d\nOverall average: %.3f",
                increase / increaseCount,
                increaseCount,
                -decrease / decreaseCount,
                decreaseCount,
                (increase + stayTheSame - decrease) / (increaseCount + stayTheSameCount + decreaseCount));
        try {
            Path filePath = Paths.get(outfile);
            Files.writeString(filePath, outLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Board boardFromPrompt(String prompt) {
        String[] parts = prompt.split("Board:");
        if (parts.length != 7) {
            throw new RuntimeException(String.format(
                    "Invalid number of boards in the prompt:\n%s\n, expected 5, got %d", prompt, parts.length));
        }
        String lastBoard = this.getLastBoardFromPart(parts[parts.length - 1]);
        int lastMove = this.getLastMoveFromPart(parts[parts.length - 2]);
        try {
            return Board.fromFypString(lastBoard, lastMove);
        } catch (InvalidBoardStringException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid board string");
        }
    }

    private String getLastBoardFromPart(String part) {
        String[] parts = part.split("Move:");
        if (parts.length != 2) {
            throw new RuntimeException(String.format(
                    "Invalid part:\n%s\n, expected 2 parts of move, got %d", part, parts.length));
        }
        return parts[0].strip();
    }

    private int getLastMoveFromPart(String part) {
        String[] parts = part.split("Move:");
        if (parts.length != 2) {
            throw new RuntimeException(String.format(
                    "Invalid part:\n%s\n, expected 2 parts of move, got %d", part, parts.length));
        }
        return Integer.parseInt(parts[1].strip()) - 1;
    }

    private byte actionFromResponse(String response) {
        return (byte) (Integer.parseInt(response.strip()) - 1);
    }
}
