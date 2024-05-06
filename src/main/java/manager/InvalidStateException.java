package manager;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException() {
        super("Invalid game state encountered!");
    }
}
