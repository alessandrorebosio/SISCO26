package it.unibo.sisco.model.api.geometry;

/**
 * Thrown when an operation is not defined on the null vector.
 *
 * @see V2d#normalize()
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
