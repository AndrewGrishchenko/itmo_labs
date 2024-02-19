package lab5.exceptions;

public class IncompleteScriptRuntimeException extends RuntimeException {
    public IncompleteScriptRuntimeException (String message) {
        super(message);
    }

    public IncompleteScriptRuntimeException() {
        super("неполный скрипт");
    }
}
