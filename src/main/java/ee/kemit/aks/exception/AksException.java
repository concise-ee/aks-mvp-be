package ee.kemit.aks.exception;

public class AksException extends RuntimeException {

    private final String message;

    public AksException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
