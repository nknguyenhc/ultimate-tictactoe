import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import algo.qlearning.QLearningAlgo;
import algo.random.RandomAlgo;
import manager.Manager;
import manager.Parser;

public class Main {
    /** Algo to test. */
    private static final BaseAlgo algo = new QLearningAlgo();
    /** Whether to print out the trace of the algo after every search. */
    private static final boolean includeTrace = true;
    /** Time control, in seconds.  */
    private static final int timeControl = 5;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Usage: java Main [option]\n" +
                    "Options:\n" +
                    "  main: manually test the algo" +
                    "  parser: parse a compact string");
        }
        switch (args[0]) {
            case "main":
                new Manager(includeTrace).run(algo);
                break;
            case "parser":
                new Parser().run();
                break;
            case "custom":
                new Manager(includeTrace).runWithStartBoard(algo);
                break;
            case "time":
                new Manager(includeTrace, timeControl).run(algo);
                break;
            default:
                System.out.printf("Unrecognised argument: %s%n", args[0]);
        }
    }
}
