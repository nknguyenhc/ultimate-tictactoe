import algo.BaseAlgo;
import algo.random.RandomAlgo;
import manager.Manager;

public class Main {
    /** Algo to test. */
    private static final BaseAlgo algo = new RandomAlgo();

    public static void main(String[] args) {
        new Manager().run(algo);
    }
}
