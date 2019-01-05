package exceptions;

public class InvalidInputException extends Exception {
    public InvalidInputException(String errorString) {
        super(errorString);
    }
}
