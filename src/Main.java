import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import algo.random.RandomAlgo;
import manager.Manager;

public class Main {
    /** Algo to test. */
    private static final BaseAlgo algo = new MctsAlgo();
    /** Whether the print out the trace of the algo after every search. */
    private static final boolean includeTrace = true;

    public static void main(String[] args) {
        new Manager(includeTrace).run(algo);
    }
}
