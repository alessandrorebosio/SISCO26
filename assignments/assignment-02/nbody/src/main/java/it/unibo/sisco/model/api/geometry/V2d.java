package it.unibo.sisco.model.api.geometry;

/**
 * An immutable vector in a 2D space.
 *
 * @param x the x component
 * @param y the y component
 */
public record V2d(double x, double y) {

    /**
     * The null vector.
     */
    public static final V2d ZERO = new V2d(0, 0);

    /**
     * Builds the vector that goes from one point to another.
     *
     * @param from the starting point
     * @param to the ending point
     * @return the vector from {@code from} to {@code to}
     */
    public static V2d between(final P2d from, final P2d to) {
        return new V2d(to.x() - from.x(), to.y() - from.y());
    }

    /**
     * Sums another vector to this one.
     *
     * @param v the vector to add
     * @return the sum of the two vectors
     */
    public V2d sum(final V2d v) {
        return new V2d(x + v.x(), y + v.y());
    }

    /**
     * Multiplies this vector by a scalar.
     *
     * @param k the scalar
     * @return the scaled vector
     */
    public V2d scalarMul(final double k) {
        return new V2d(x * k, y * k);
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
     * Computes the unit vector with the same direction of this one.
     *
     * @return the normalized vector
     * @throws NullVectorException if this vector is the null vector
     */
    public V2d normalize() throws NullVectorException {
        final double mod = module();
        if (mod > 0) {
            return new V2d(x / mod, y / mod);
        }
        throw new NullVectorException();
    }
}
