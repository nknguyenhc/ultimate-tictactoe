package board;

/**
 * Represents a parse error. The message contains the explanation of what went wrong.
 */
public class InvalidBoardStringException extends Exception {
    public InvalidBoardStringException(String message) {
        super(message);
    }
}
