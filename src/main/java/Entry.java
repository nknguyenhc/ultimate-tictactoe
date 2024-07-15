import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import algo.pv.PvAlgo;
import algo.qlearning.QLearningAlgo;
import algo.sarsa.SarsaAlgo;
import manager.Manager;

import java.util.Scanner;

/**
 * The entry point of the programme, for end-users.
 */
public class Entry {
    private final Scanner scanner = new Scanner(System.in);
    private BaseAlgo algo;
    private int time;
    private boolean allowPondering;

    private void selectAlgo() {
        System.out.print("Algos to fight against:\n" +
                "  1. SARSA (easy)\n" +
                "  2. Q-Learning (medium)\n" +
                "  3. Monte-Carlo Tree Search (hard)\n" +
                "  4. PV Search (hard)\n" +
                "Key in your choice (1-4): ");
        String response = this.scanner.nextLine();
        boolean isExit = false;
        while (!isExit) {
            switch (response) {
                case "1":
                    isExit = true;
                    this.algo = new SarsaAlgo();
                    break;
                case "2":
                    isExit = true;
                    this.algo = new QLearningAlgo();
                    break;
                case "3":
                    isExit = true;
                    this.algo = new MctsAlgo();
                    break;
                case "4":
                    isExit = true;
                    this.algo = new PvAlgo();
                    break;
                default:
                    System.out.print("Invalid choice, please key in your choice again (1-2): ");
                    response = this.scanner.nextLine();
            }
        }
    }

    private void selectTimeControl() {
        System.out.print("Time control limits the amount of time allocated to the algo.\n" +
                "This time control does not apply to you.\n");
        System.out.print("Select time control, in number of seconds (1-10): ");
        String response = this.scanner.nextLine();
        boolean isExit = false;
        while (!isExit) {
            try {
                int time = Integer.parseInt(response);
                if (time <= 0 || time > 10) {
                    System.out.print("Time control must be an integer between 1 and 10!\n" +
                            "Please key in time control again (1-10): ");
                    response = this.scanner.nextLine();
                    continue;
                }
                this.time = time * 1000;
                isExit = true;
            } catch (NumberFormatException e) {
                System.out.print("Time control must be an integer between 1 and 10!\n" +
                        "Please key in time control again (1-10): ");
                response = this.scanner.nextLine();
            }
        }
    }

    private void selectPondering() {
        System.out.print("Allow the algo to think while you are thinking? (Y/N) ");
        String response = this.scanner.nextLine();
        boolean isExit = false;
        while (!isExit) {
            switch (response) {
                case "Y":
                    isExit = true;
                    this.allowPondering = true;
                    break;
                case "N":
                    isExit = true;
                    this.allowPondering = false;
                    break;
                default:
                    System.out.print("Invalid response, please indicate again (Y/N): ");
                    response = this.scanner.nextLine();
            }
        }
    }

    private void run() {
        this.selectAlgo();
        this.selectTimeControl();
        this.selectPondering();
        new Manager(false, this.time, this.allowPondering).run(this.algo);
    }

    public static void main(String[] args) {
        new Entry().run();
    }
}
