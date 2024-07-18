import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import algo.mcts.parallel.ParallelMctsAlgo;
import algo.pv.PvAlgo;
import algo.qlearning.QLearningAlgo;
import algo.sarsa.SarsaAlgo;
import manager.AlgoFight;
import manager.Manager;
import manager.Parser;

/**
 * The main testing routine.
 */
public class Main {
    /** Algo to test. */
    private static final BaseAlgo algo = new MctsAlgo();
    /** Whether to print out the trace of the algo after every search. */
    private static final boolean includeTrace = true;
    /** Time control, in seconds.  */
    private static final int timeControl = 1000;
    private static final boolean allowPondering = true;

    /** Algo 1 for automated fight. */
    private static final BaseAlgo algo1 = new MctsAlgo();
    /** Algo 2 for automated fight. */
    private static final BaseAlgo algo2 = new ParallelMctsAlgo(true);
    private static final boolean includeTrace1 = false;
    private static final boolean includeTrace2 = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Usage: java Main [option]\n" +
                    "Options:\n" +
                    "  main: manually test the algo\n" +
                    "  parser: parse a compact string\n" +
                    "  custom: start from a custom board\n" +
                    "  time: manually test the algo with time control\n" +
                    "  fight: run an automated fight between two algos\n" +
                    "  fight-time: run an automated fight between two algos with time control\n");
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
            case "fight":
                new AlgoFight(algo1, algo2, includeTrace1, includeTrace2, allowPondering).run();
                break;
            case "fight-time":
                new AlgoFight(algo1, algo2, includeTrace1, includeTrace2, allowPondering).runWithTime(timeControl);
                break;
            default:
                System.out.printf("Unrecognised argument: %s%n", args[0]);
        }
    }
}
