package lab6_core.models;

public class ValueChecker {
    private final boolean check;
    private final String message;
    private final String error;

    public ValueChecker (boolean check) {
        this.check = check;
        this.message = null;
        this.error = null;
    }

    public ValueChecker (boolean check, String message) {
        this.check = check;
        this.message = message;
        this.error = null;
    }

    public ValueChecker (boolean check, String message, String error) {
        this.check = check;
        this.message = message;
        this.error = error;
    }

    public boolean getCheck() {
        return check;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
