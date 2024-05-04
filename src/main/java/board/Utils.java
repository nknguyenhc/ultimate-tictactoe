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
