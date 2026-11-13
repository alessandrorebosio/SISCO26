package it.unibo.sisco.model.exceptions;

import it.unibo.sisco.model.api.Body;

/**
 * Thrown when the gravitational force between two bodies is infinite, that is when the two
 * bodies are in the same position.
 *
 * @see Body#computeGForceBy(Body)
 */
public final class InfiniteForceException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Infinite force between bodies in the same position";

    /**
     * Builds a new exception.
     */
    public InfiniteForceException() {
        super(MESSAGE);
    }

    /**
     * Builds a new exception with a cause.
     *
     * @param cause the cause of the exception, for example a {@code NullVectorException}
     */
    public InfiniteForceException(final Throwable cause) {
        super(MESSAGE, cause);
    }
}
