import cs2109s.BoardEvaluation;
import cs2109s.IterationSelection;

public class CS2109S {
    public static void main(String[] args) throws Exception {
//        IterationSelection runner = new IterationSelection();
//        runner.readFile("data/data.uttt");
//        runner.run();
//        runner.save("data/data-results.uttt");

        BoardEvaluation runner = new BoardEvaluation();
        runner.readFile("data/data-trimmed.uttt");
        runner.run();
        runner.save("data/data-trimmed-results.uttt");
    }
}
