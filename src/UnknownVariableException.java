public class UnknownVariableException extends RuntimeException {
    private static final long serialVersionUID = -1729509372649329856L;

    public UnknownVariableException() {
        super();
    }

    public UnknownVariableException(Throwable cause) {
        super(cause);
    }

    public UnknownVariableException(String message) {
        super(message);
    }

    public UnknownVariableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
