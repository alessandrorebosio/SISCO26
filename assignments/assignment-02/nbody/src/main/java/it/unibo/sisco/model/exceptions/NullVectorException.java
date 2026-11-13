package it.unibo.sisco.model.exceptions;

import it.unibo.sisco.model.impl.geometry.Vector2D;

/**
 * Thrown when an operation is not defined on the null vector.
 *
 * @see Vector2D#normalize()
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
