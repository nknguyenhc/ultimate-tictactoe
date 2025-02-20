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

public class BoardEvaluation {
    private final List<Board> boards = new ArrayList<>();
    private final List<List<Double>> values = new ArrayList<>();

    public void readFile(String filename) throws FileNotFoundException, InvalidBoardStringException {
        File f = new File(filename);
        Scanner sc = new Scanner(f);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            this.boards.add(Board.fromCompactString(line));
        }
    }

    private void run(int id, int start, int end) {
        for (int i = start; i < end; i++) {
            if ((i - start) % 100 == 0) {
                System.out.printf("Thread #%d: %d\n", id, i - start);
            }
            Board board = this.boards.get(i);
            MctsAlgo algo = new MctsAlgo();
            double result = algo.evaluate(board, 30000);
            this.values.get(id).add(result);
        }
    }

    public void run() throws InterruptedException {
        int numOfThreads = 7;
        assert this.boards.size() % numOfThreads == 0;
        int countPerThread = this.boards.size() / numOfThreads;
        for (int i = 0; i < numOfThreads; i++) {
            this.values.add(new ArrayList<>());
        }
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            int index = i;
            Thread thread = new Thread(() -> this.run(
                    index, countPerThread * index, countPerThread * (index + 1)));
            thread.start();
            threads.add(thread);
        }
        for (Thread thread: threads) {
            thread.join();
        }
    }

    public void save(String filename) throws IOException {
        List<Double> values = new ArrayList<>();
        for (List<Double> l: this.values) {
            values.addAll(l);
        }
        assert values.size() == this.boards.size();
        FileWriter fileWriter = new FileWriter(filename);
        for (int i = 0; i < this.boards.size(); i++) {
            String string = this.boards.get(i).toCompactString() +
                    "|" +
                    String.format("%.3f", values.get(i)) +
                    "\n";
            fileWriter.write(string);
        }
        fileWriter.close();
    }
}
