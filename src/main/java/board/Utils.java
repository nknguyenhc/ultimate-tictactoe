package board;

public class Utils {
    static final short[] winningLines = {
            0b100010001,
            0b001010100,
            0b000000111,
            0b000111000,
            0b111000000,
            0b001001001,
            0b010010010,
            0b100100100,
    };

    static final boolean[] wins = Utils.computeWins();

    static boolean[] computeWins() {
        boolean[] result = new boolean[0b111111111];
        for (int i = 0; i < 0b111111111; i++) {
            for (short winningLine: Utils.winningLines) {
                if ((i & winningLine) == winningLine) {
                    result[i] = true;
                    break;
                }
            }
        }
        return result;
    }

    static final short[] nearWinningLines = {
            0b100010000,
            0b100000001,
            0b000010001,
            0b001010000,
            0b001000100,
            0b000010100,
            0b000000110,
            0b000000101,
            0b000000011,
            0b000110000,
            0b000101000,
            0b000011000,
            0b110000000,
            0b101000000,
            0b011000000,
            0b001001000,
            0b001000001,
            0b000001001,
            0b010010000,
            0b010000010,
            0b000010010,
            0b100100000,
            0b100000100,
            0b000100100
    };

    static final short[] blockingWinningLines = {
            0b000000001,
            0b000010000,
            0b100000000,
            0b000000100,
            0b000010000,
            0b001000000,
            0b000000001,
            0b000000010,
            0b000000100,
            0b000001000,
            0b000010000,
            0b000100000,
            0b001000000,
            0b010000000,
            0b100000000,
            0b000000001,
            0b000001000,
            0b001000000,
            0b000000010,
            0b000010000,
            0b010000000,
            0b000000100,
            0b000100000,
            0b100000000
    };

    /**
     * Represents the state of a sub-board or a board.
     */
    public enum Side {
        /** Undetermined */
        U,
        /** X wins */
        X,
        /** O wins */
        O,
        /** Draw */
        D,
    }

    static short filled = 0b111111111;
}
