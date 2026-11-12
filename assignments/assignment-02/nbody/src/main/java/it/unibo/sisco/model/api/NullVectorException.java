package it.unibo.sisco.model.api;

/**
 * Thrown when an operation is not defined on the null vector.
 */
public final class NullVectorException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Builds a new exception.
     */
    public NullVectorException() {
        super("Operation not defined on the null vector");
    }
}
