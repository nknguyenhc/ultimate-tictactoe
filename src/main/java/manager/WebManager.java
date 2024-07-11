package manager;

import algo.BaseAlgo;
import algo.mcts.MctsAlgo;
import algo.pv.PvAlgo;
import algo.qlearning.QLearningAlgo;
import algo.sarsa.SarsaAlgo;
import board.Board;
import board.Move;
import board.Utils;

public class WebManager {
    private enum State {
        START,
        CHOOSE_ALGO,
        CHOOSE_TIME,
        CHOOSE_SIDE,
        ALGO_TURN,
        HUMAN_TURN,
        GAME_FINISHED,
    }

    private State state = State.START;
    private BaseAlgo algo;
    private int timeControl;
    private Board board = new Board();

    private static final String WELCOME_MESSAGE =
            "Welcome! This is a programme to test out the engine.\n" +
            "Please choose the algo you wish to fight against:\n" +
            "  1. Sarsa (easy)\n" +
            "  2. Q-learning (medium)\n" +
            "  3. Monte-Carlo Tree Search (hard)\n" +
            "  4. PV-Algo (extreme)\n" +
            "Your choice (1-3):";
    private static final String INVALID_ALGO_MESSAGE = "Invalid choice, please indicate again (1-3):";
    private static final String CHOOSE_TIME_MESSAGE =
            "Please indicate time control for the algo, between 1 to 5 seconds.\n" +
            "Note that this time control does not apply to you (1-5):";
    private static final String INVALID_TIME_MESSAGE = "Invalid time input, please indicate again (1-5):";
    private static final String CHOOSE_SIDE_MESSAGE =
            "Please indicate which side you want to play as, X goes first, O goes second (X/O):";
    private static final String INVALID_TURN_MESSAGE = "Invalid choice, please indicate again (X/O):";
    private static final String TURN_PROMPT_APPEND = "\nPlease indicate your move, in the format R, C :";

    public String getResponse(String input) {
        switch (this.state) {
            case START:
                return this.start();
            case CHOOSE_ALGO:
                return this.chooseAlgo(input);
            case CHOOSE_TIME:
                return this.chooseTime(input);
            case CHOOSE_SIDE:
                return this.chooseSide(input);
            case HUMAN_TURN:
                return this.humanTurn(input);
            case ALGO_TURN:
                return this.algoTurn();
            default:
                throw new InvalidStateException();
        }
    }

    private String start() {
        this.state = State.CHOOSE_ALGO;
        return WELCOME_MESSAGE;
    }

    private String chooseAlgo(String input) {
        switch (input) {
            case "1":
                this.algo = new SarsaAlgo();
                return this.promptTime();
            case "2":
                this.algo = new QLearningAlgo();
                return this.promptTime();
            case "3":
                this.algo = new MctsAlgo();
                return this.promptTime();
            case "4":
                this.algo = new PvAlgo();
                return this.promptTime();
            default:
                return INVALID_ALGO_MESSAGE;
        }
    }

    private String promptTime() {
        this.state = State.CHOOSE_TIME;
        return CHOOSE_TIME_MESSAGE;
    }

    private String chooseTime(String input) {
        int time;
        try {
            time = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return INVALID_TIME_MESSAGE;
        }

        if (time <= 0 || time > 5) {
            return INVALID_TIME_MESSAGE;
        }

        this.timeControl = time;
        this.state = State.CHOOSE_SIDE;
        return CHOOSE_SIDE_MESSAGE;
    }

    private String chooseSide(String input) {
        switch (input) {
            case "X":
                this.state = State.HUMAN_TURN;
                return this.promptTurn();
            case "O":
                this.state = State.ALGO_TURN;
                return this.boardInfo();
            default:
                return INVALID_TURN_MESSAGE;
        }
    }

    private String promptTurn() {
        return this.boardInfo() + TURN_PROMPT_APPEND;
    }

    private String boardInfo() {
        int index = this.board.getBoardIndexToMove();
        if (index == 9) {
            return this.board.toString() + "Board to move: any board";
        } else {
            int row = index / 3 + 1;
            int col = index % 3 + 1;
            return this.board.toString() + String.format("Board to move: (%d, %d)", row, col);
        }
    }

    private String humanTurn(String input) {
        Move move;
        try {
            move = this.parseMove(input);
        } catch (InvalidMoveStringException e) {
            return e.getMessage() + TURN_PROMPT_APPEND;
        }
        this.board = this.board.move(move);
        if (this.board.winner() != Utils.Side.U) {
            return this.gameJudge();
        }

        this.state = State.ALGO_TURN;
        return this.boardInfo();
    }

    private Move parseMove(String input) throws InvalidMoveStringException {
        // Bytecoder does not support String::split
        if (input.length() != 4 || input.charAt(1) != ',' || input.charAt(2) != ' ') {
            throw new InvalidMoveStringException();
        }

        byte row;
        byte col;
        try {
            row = (byte) (Byte.parseByte(String.valueOf(input.charAt(0))) - 1);
            col = (byte) (Byte.parseByte(String.valueOf(input.charAt(3))) - 1);
        } catch (NumberFormatException e) {
            throw new InvalidMoveStringException();
        }

        Move move = new Move(row, col);
        if (!this.board.actions().contains(move)) {
            throw new InvalidMoveStringException();
        }
        return move;
    }

    private String algoTurn() {
        Move move = this.algo.nextMoveWithTime(this.board, this.timeControl * 1000);
        this.board = this.board.move(move);
        if (this.board.winner() != Utils.Side.U) {
            return this.gameJudge();
        }

        this.state = State.HUMAN_TURN;
        return String.format("Algo choose: %s\n", move) + this.promptTurn();
    }

    private String gameJudge() {
        Utils.Side winner = this.board.winner();
        this.state = State.GAME_FINISHED;
        return this.board.toString() + String.format("%s won !!!", winner);
    }
}
