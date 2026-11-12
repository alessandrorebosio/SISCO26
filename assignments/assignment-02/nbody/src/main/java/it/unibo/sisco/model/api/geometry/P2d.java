package it.unibo.sisco.model.api.geometry;

/**
 * An immutable point in a 2D space.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 */
public record P2d(double x, double y) {

    /**
     * Translates this point by a vector.
     *
     * @param v the translation vector
     * @return the translated point
     */
    public P2d sum(final V2d v) {
        return new P2d(x + v.x(), y + v.y());
    }

    /**
     * Computes the euclidean distance from another point.
     *
     * @param other the other point
     * @return the distance between the two points
     */
    public double distanceFrom(final P2d other) {
        final double dx = x - other.x();
        final double dy = y - other.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
