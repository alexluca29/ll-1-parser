package exceptions;

public class InvalidSymbolException extends Exception {
    public InvalidSymbolException(String errorString) {
        super(errorString);
    }
}
