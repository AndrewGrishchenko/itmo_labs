class placeNotFoundException extends RuntimeException {
    public placeNotFoundException (String errorMessage) {
        super(errorMessage);
    }

    public placeNotFoundException (Throwable cause) {
        super(cause);
    }

    public placeNotFoundException (String message, Throwable throwable) {
        super (message, throwable);
    }
}