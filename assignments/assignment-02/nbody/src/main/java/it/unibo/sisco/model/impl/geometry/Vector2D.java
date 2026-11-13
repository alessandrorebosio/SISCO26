package it.unibo.sisco.model.impl.geometry;

import it.unibo.sisco.model.exceptions.NullVectorException;

/**
 * An immutable vector in a 2D space. Every operation returns a new vector, so instances can
 * be shared among threads without synchronisation.
 *
 * @param x the x component
 * @param y the y component
 */
public record Vector2D(double x, double y) {

    /**
     * The null vector.
     */
    public static final Vector2D ZERO = new Vector2D(0, 0);

    /**
     * Builds the vector that goes from one point to another.
     *
     * @param from the starting point
     * @param to the ending point
     * @return the vector from {@code from} to {@code to}
     */
    public static Vector2D between(final Point2D from, final Point2D to) {
        return new Vector2D(to.x() - from.x(), to.y() - from.y());
    }

    /**
     * Sums another vector to this one.
     *
     * @param v the vector to add
     * @return the sum of the two vectors
     */
    public Vector2D sum(final Vector2D v) {
        return new Vector2D(x + v.x(), y + v.y());
    }

    /**
     * Multiplies this vector by a scalar.
     *
     * @param k the scalar
     * @return the scaled vector
     */
    public Vector2D scalarMul(final double k) {
        return new Vector2D(x * k, y * k);
    }

    /**
     * Computes the module of this vector.
     *
     * @return the module of the vector
     */
    public double module() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Computes the unit vector with the same direction of this one, that is a vector with
     * module {@code 1}.
     *
     * @return the normalized vector
     * @throws NullVectorException if this vector is the null vector
     */
    public Vector2D normalize() throws NullVectorException {
        final double mod = module();
        if (mod > 0) {
            return new Vector2D(x / mod, y / mod);
        }
        throw new NullVectorException();
    }
}
