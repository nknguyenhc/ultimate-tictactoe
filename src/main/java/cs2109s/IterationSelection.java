package cs2109s;

import algo.mcts.MctsAlgo;
import board.Board;
import board.InvalidBoardStringException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IterationSelection {
    private final List<Board> boards = new ArrayList<>();
    private final List<Double> values = new ArrayList<>();

    public void readFile(String filename) throws FileNotFoundException, InvalidBoardStringException {
        File f = new File(filename);
        Scanner sc = new Scanner(f);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            this.boards.add(Board.fromCompactString(line));
        }
    }

    public void run() {
        int i = 0;
        for (Board board: this.boards) {
            if (i % 1000 == 0) {
                System.out.println(i);
            }
            i++;
            MctsAlgo algo = new MctsAlgo();
            double result = algo.evaluate(board, 500);
            this.values.add(result);
        }
    }

    public void save(String filename) throws IOException {
        assert this.boards.size() == this.values.size();
        FileWriter fileWriter = new FileWriter(filename);
        for (int i = 0; i < this.boards.size(); i++) {
            String stringBuilder = this.boards.get(i).toCompactString() +
                    "|" +
                    String.format("%.3f", this.values.get(i)) +
                    "\n";
            fileWriter.write(stringBuilder);
        }
        fileWriter.close();
    }
}
